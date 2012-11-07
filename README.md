# SmartRoot to R-SWMS

## Overall
---

These project aims at transform SmartRoot data into a R-SWMS RootSys file.
The RootSys file can then be used to run simulation in R-SWMS with an experimentaly generated root system. Different version of the project are currently being developed.



## Versions
---

### Java application	

The first version of the program was coded as a small Java application that can retrieve data from a SmartRoot database to create the R-SWMS files. The program work fine, but requires the data to be stored into a database, which is not always the case (an restrict the use of the program).

### R script

A second version was created as an R script. The version is lighter than hte previous one as it process data contained in a CSV file (instead of in a SQL database) and only requires R to run.

### SmartRoot plugin

In the future, we plan to include an export function in SmartRoot that will directly create the R-SWMS RootSys file. This is yet to be done.
 



## Requirements
---

### Java program

- [Java](http://www.java.com/en/) (for the script)
- [SQL database](http://www.mysql.com/) (for data storage)
- [SmartRoot](http://www.uclouvain.be/en-smartroot)
- [ImageJ](http://rsb.info.nih.gov/ij/index.html)

### R script

- [R](http://www.r-project.org/) (to run the script)
- [SmartRoot](http://www.uclouvain.be/en-smartroot)
- [ImageJ](http://rsb.info.nih.gov/ij/index.html) 
