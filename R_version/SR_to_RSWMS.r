#author: Guillaume Lobet, Université catholique de Louvain, Earth and Life Institute 
#date created: 2012-10-05
# This program takes the SmartRoot node data contain in a CSV file and transform it 
# into an R-SWMS input file. The program can conver buch of root system csv is contained
# in the selected folder

###########################################  User inputs  ###################################################

growthRate <- 2   	# Mean root growth rate in cm (used for root age assessment)
inFolder <- "/Users/guillaumelobet/Desktop/roots/"		# Source folder containing the SmartRoot CSV files
outFolder <- "/Users/guillaumelobet/Desktop/"		# Destination folder for the R-SWMS files

############################################  Functions  #####################################################

eucl.dist <- function(x1, y1, x2, y2){
	return(sqrt((x2-x1)^2+(y2-y1)^2))
}

############################################################################################################

# Parameters for the simulation
fl <- list.files(inFolder)
fll <- length(fl)

g <- c(growthRate, growthRate/2, growthRate/3)	
	
# Length of the unbranched zone
LAUZ <- 2	
	
# Start conversion
print("Conversion from SmartRoot to R-SWMS started")
print("---------------------------")

# Navigate the different SmartRoot files
for(i in 1:fll){
	
	print("------------------------------")
	print(paste("File ", fl[i], " is being processed", sep=""))
	# 0) Initialize
	currentID <- 0
	
	# 1) Get the raw data
	rawData <- read.csv(paste(inFolder, fl[i], sep=""))
	rawData <- rawData[order(rawData$rootOrder, rawData$Root, rawData$bLength),]
	rawData$rootOrder <- rawData$rootOrder + 1	
	nSeg <- nrow(rawData)
	nApex <- nrow(rawData[which(rawData$aLength == 0),])
	for(j in 1:nSeg){rawData$ID[j] <- j}
	
	ro.list <- sort(unique(rawData$rootOrder)); nRO <- length(ro.list)
	r.list <- sort(unique(rawData$Root)); nR <- length(r.list)
	pr.list <- sort(unique(rawData$parentRoot)); nPR <- length(pr.list)
			
	# 2) Get collar informations (position and identifier)
	c.min <- min(rawData[which(rawData$bLength==0 & rawData$rootOrder==1),]$Y)
	collar <- rawData[which(rawData$bLength==0 & rawData$rootOrder==1 & rawData$Y == c.min),]
	xDev <- collar$X
	yDev = collar$Y
	bCollarID <- collar$ID
	collarRoot <- collar$Root
	collarImg <- collar$Img

	rawData$Y <- -rawData$Y - yDev
	rawData$X <- rawData$X - xDev
	

	# 3) Get the collar 
	cData <- rawData[which(rawData$Root == collarRoot),]
	cData$length <- c(0,diff(cData$bLength))
	cData$vol <- (cData$Diam+1e-4) * (cData$length+1e-4) * pi	
	for(j in 1:nrow(cData)){
		cData$segID[j] <- j
		cData$prevID[j] <- j-1
		currentID <- currentID+1
	}
	cData$oTime <- cData$bLength / g[1]

	# 4) Create the segment dataset
	sData <- data.frame(matrix(nrow=nSeg, ncol=19, 0))
	colnames(sData) <- colnames(cData)
	for(j in 1:nrow(cData)){sData[j, ] <- cData[j, ]}
	
	for(o in 1:nRO){
		tData <- rawData[which(rawData$rootOrder == ro.list[o]),]
		r.list <- sort(unique(tData$Root))
		nR <- length(r.list)
		for(j in 1:nR){
			if(tData[which(tData$Root == r.list[j]),]$Root[1] != collarRoot){
				dat <- tData[which(tData$Root == r.list[j]),]
				dat$length <- c(0,diff(dat$bLength))
				dat$vol <- (dat$Diam+1e-4) * (dat$length+1e-4) * pi
				dat$segID <- 0
				dat$prevID <- 0
				
				startID <- currentID
				for(k in 1:nrow(dat)){
					dat$segID[k] <- k+startID
					if(dat$bLength[k] > 0) dat$prevID[k] <- dat$segID[k-1]
					if(dat$bLength[k] == 0){
						base <- dat[1,]
						if(base$parentRoot == -1) base$parentRoot <- cData$Root[1]
						parent <- sData[which(sData$Root == base$parentRoot),]
						dmin <- 1e5
						for(m in 1:nrow(parent)){
							d <- eucl.dist(base$X, base$Y, parent$X[m], parent$Y[m])
							if(d < dmin){
								dmin <- d
								p <- parent$segID[m]
							}
						}
						dat$prevID[k] <- p		
					}
					currentID <- currentID+1
				}
				dat$oTime <- sData[which(sData$segID == dat$prevID[1]),]$oTime + dat$bLength / g[dat$rootOrder]				
				for(n in 1:nrow(dat)){sData[(n+startID), ] <- dat[n, ]}
			}
		}
	}		
		

	# 5) Create the apex dataset
	aData <- rawData[which(rawData$aLength == 0),]#data.frame(matrix(nrow=nApex, ncol=19, 0))
	
	# 6) Print the data
	 fileout <- paste(outFolder, "RootSys_", substr(fl[i], 1, (nchar(fl[i])-4)), sep="")
	 write("Time", fileout)
	 write(paste(" ", round(max(sData$oTime)), sep=""), fileout, append=T)
	 write("", fileout, append=T)
	 write("Number of seeds", fileout, append=T)
	 write(" 1", fileout, append=T)
	 write("", fileout, append=T)
	 write("ID, X and Y coordinates of the seeds (one per line)", fileout, append=T)
	 write(paste(" 1 ", sData$X[1]," ", sData$Y[1], sep=""), fileout, append=T)
	 write("", fileout, append=T)
	 write("Root DM, shoot DM, leaf area:", fileout, append=T)
	 write(" 0.0 0.0 0.0", fileout, append=T)
	 write("", fileout, append=T)
	 write("Average soil strength and solute concentration experienced by root system", fileout, append=T)
	 write(" 0.0 0.0", fileout, append=T)
	 write("", fileout, append=T)
	 write("Total # of axes", fileout, append=T)
	 write(" 1", fileout, append=T)
	 write("", fileout, append=T)
	 write("Total # of branches, including axis(es)", fileout, append=T)
	 write(paste(" ", nApex, sep=""), fileout, append=T)
	 write("", fileout, append=T)
	 write("Total # of segments records", fileout, append=T)
	 write(paste(" ", nSeg, sep=""), fileout, append=T)
	 write("", fileout, append=T)
	 
	 # Print the segment informations
	 write("segID#	x	y	z	prev	or	br#	length	surface	mass", fileout, append=T)
	 write("origination time", fileout, append=T)
	 for(j in 1:nSeg){
		 write(paste("",
		 	sData$segID[j], sData$X[j], 0, sData$Y[j], sData$prevID[j], sData$rootOrder[j], sData$Root[j],
		 	sData$length[j], sData$vol[j], 0,
		 	sep=" "), 
		 	fileout, append=T)
		 write(paste(" ", sData$oTime[j], sep=""), fileout, append=T)
	 }
	 
	 # Print the apex informations
	 write("", fileout, append=T)
	 write("Total # of growing branch tips:", fileout, append=T)
	 write(paste(" ", nApex, sep=""), fileout, append=T)
	 write("", fileout, append=T)
	 write("tipID#	xg	yg	zg	sg.bhd.tp	ord	br#	tot.br.lgth	axs#", fileout, append=T)
	 write("overlength # of estblished points", fileout, append=T)
	 write("time of establishing (-->)", fileout, append=T)
	 for(j in 1:nApex){
	 	write(paste("",
	 		j, aData$X[j], 0, aData$Y[j], aData$prevID[j], aData$rootOrder[j], aData$Root[j],
	 		aData$bLength[j], 0,
	 		sep=" "), fileout, append=T)
	 	write(" 0.0 0", fileout, append=T)	
	 }	
}

print("================================")
print("File processing done")





