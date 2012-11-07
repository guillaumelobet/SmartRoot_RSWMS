/**
 * @author Guillaume Lobet, Université catholique de Louvain, Earth and Life Institute
 * 
 * This program takes the SmartRoot node data contain in a SQL database and transform it 
 * into an R-SWMS input file. The program can conver buch of root system if more than one 
 * datatable is contained into the database.
 *
 * Table name have to be EXPxx_Ryy_N
 */

package sr_to_rswms;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SRToRSWMS {

	// SQL connection
	private Connection con = null;
	private Statement sql = null;
	java.sql.ResultSet rs;
	   
	// Parameters for the simulation
	static public String exp;
	static public int numRS;
	static public float growthRate;
	static public String outFolder, usr, psw, database, connectionPath, driver;
	static String connection;
	
	// Length of the unbranched zone
	static int LAUZ = 2;
	
	// Run parameters
	int rh;
	int nSeg = 0,  nApex = 0;
	
	// Statistic about the conversion
	int done = 0, missed = 0, nSegTot = 0, nApexTot = 0;
	
	float xDev = 0;
	float yDev = 0;
	int bCollarID = 0;
	int eCollarID = 0;
	String collar, collarImg;
	int finalDate = 21;
	
	// Vector containing the data
	float[][] rawData;
	float[][] segData;
	float[][] apexData;
	String[] parentNames;
	String[] imgNames;
	String[] rootNames;
	int[] segID;
	
	
	/**
	 * Launch the user interface
	 * @param args
	 */
	public static void main(String[] args) {
	    STRInterface f = new STRInterface();
	    f.setVisible(true);
	}
	
	
	/**
	 * Main function which convert the root
	 */
	public SRToRSWMS(){	
		if(startSQL(driver, connectionPath+database, usr, psw)){
			STRInterface.updateOutput("Conversion from SmartRoot to R-SWMS started");
			STRInterface.updateOutput("---------------------------");
			connection = connectionPath+database+"?user="+usr+"&password="+psw;
			for (int i=1; i <= numRS; i++){
			rh = i;
				addID();	
				if (getNumSeg()){
					
					getNumApex();

					// Initialize the data tables
					rawData = new float[nSeg][8];
					segData = new float[nSeg][13];
					apexData = new float[nApex][11];
					segID = new int[nSeg];
					parentNames = new String[nApex];
					imgNames = new String[nApex];
					rootNames = new String[nApex];
					
					getCollarInfo();
					getData(getCollar());
					processSegData();
					processApexData();
					updateOriginationDateData();
					printData();
					done ++;
					nApexTot += nApex;
					nSegTot += nSeg;
				}
				else missed ++;
			}
			STRInterface.updateOutput("---------------------------");
			STRInterface.updateOutput("Conversion from SmartRoot to R-SWMS done");
			STRInterface.updateOutput("---------------------------");
			STRInterface.updateOutput("# of tested conversion(s): "+(rh));
			STRInterface.updateOutput("# of successful conversion(s): "+done);
			STRInterface.updateOutput("# of missed conversion(s): "+missed);
			STRInterface.updateOutput("---------------------------");
			STRInterface.updateOutput("# of root segment(s) processed: "+nSegTot);
			STRInterface.updateOutput("# of root apex(es) processed: "+nApexTot);
		}
	}
	
	
	/**
	 * Add ID field in the table if it does not exist
	 */
	public void addID(){
		try{	
	  		rs=sql.executeQuery("SELECT max(ID) FROM EXP"+exp+"_R"+rh+"_N");
	  		return;
		}
	  	catch(Exception e){
	  		try{
	  			sql.execute("ALTER TABLE "+database+".EXP"+exp+"_R"+rh+"_N ADD COLUMN ID INT NOT NULL AUTO_INCREMENT AFTER pathRoot, ADD PRIMARY KEY (ID)");
	  		}
	  		catch(SQLException esql){
		  		STRInterface.updateOutput("Table SmartRoot.EXP"+exp+"_R"+rh+"_N does not contain ID field : "+esql);
	  		}
	  		return;
	  	}		
	}
	
	
	/**
	 * Get the number of segments in the database table
	 */
	public boolean getNumSeg(){
		try{	
	  		rs=sql.executeQuery("SELECT max(ID) FROM EXP"+exp+"_R"+rh+"_N");
	  		rs.first();
	  		nSeg=rs.getInt(1);
	  		return true;
		}
		
	  	catch(Exception e){
	  		STRInterface.updateOutput("Table SmartRoot.EXP"+exp+"_R"+rh+"_N doesn't exist : "+e);
	  		return false;
	  	}
	}
	
	
	/**
	 * Get the number of axpes in the database table
	 */
	
	public void getNumApex(){
		try{
			java.sql.ResultSet rs=sql.executeQuery("SELECT count(Root) FROM EXP"+exp+"_R"+rh+"_N WHERE aLength=0");
	  		rs.first();
	  		nApex=rs.getInt(1);
	  		}
		
	  	catch(Exception e){
	  		STRInterface.updateOutput("Connection 2 ratée in SRToRSWMS for "+"EXP"+exp+"_R"+rh+"_N "+e);
	  	}
	}
	
	
	/**
	 * Get the postion and identifier of the collar
	 */
	public void getCollarInfo(){
		try{
	  		java.sql.ResultSet rs=sql.executeQuery("SELECT ID, X, Root, Img, Y "+
	  												"FROM EXP"+exp+"_R"+rh+
	  												"_N WHERE bLength=0 AND rootOrder=0 ORDER BY Y");
	  		rs.first();
	  		xDev = rs.getFloat(2);
	  		yDev = rs.getFloat(5);
		  	bCollarID = rs.getInt(1);
		  	collar = rs.getString(3);
		  	collarImg = rs.getString(4);
	  	}
	  	catch(Exception e){
	  		STRInterface.updateOutput("Connection 3 ratée in SRToRSWMS for "+"EXP"+exp+"_R"+rh+" "+e);
	  	}
	}
	
	/**
	 * Get the collar position
	 * @return
	 */
	public int getCollar(){
		try{
	  		java.sql.ResultSet rs=sql.executeQuery("SELECT ID, X, Y, rootOrder, Root, parentRoot, bLength, Diam, aLength, Img "+
	  												" FROM EXP"+exp+"_R"+rh+"_N WHERE ID >= "+bCollarID+" AND rootOrder=0 ORDER BY ID");
	  		int j = 0;
	  		while (rs.next()){
	  			rawData[j][0] = j+1;	// ID
	  			segID[rs.getInt(1)-1] = j+1;
	  			rawData[j][1] = rs.getFloat(2) - xDev;		// X
	  			rawData[j][2] = -(rs.getFloat(3) - yDev);	// Y
	  			rawData[j][3] = rs.getFloat(4);				// rootOrder
	  			rawData[j][4] = 1;							// root
	  			rawData[j][5] = rs.getFloat(7);				// bLength
	  			rawData[j][6] = rs.getFloat(8);				// Diam
	  			rawData[j][7] = rs.getFloat(9); 			// aLength
		  		j++;
	  			if(rs.getFloat(9) == 0){ // if the segment is an apex
			  		parentNames[0] = rs.getString(6);
			  		imgNames[0] = rs.getString(10);
			  		rootNames[0] = rs.getString(5);
  		  			eCollarID = rs.getInt(1);
			  		return j;
		  		}		  			
	  		}
	  	}
	  	catch(Exception e){
	  		STRInterface.updateOutput("Connection 4 ratée in SRToRSWMS for "+"EXP"+exp+"_R"+rh+" "+e);
	  	}
  		return  -1;
	}
	
	
	/**
	 * Get all the data in the database
	 */
	public void getData(int jInit){
		try{
	  		java.sql.ResultSet rs=sql.executeQuery("SELECT ID, X, Y, rootOrder, Root, parentRoot, " +
	  												"bLength, Diam, aLength, Img " +
	  												"FROM EXP"+exp+"_R"+rh+"_N "+
	  												"WHERE ID <"+bCollarID+" OR ID > "+eCollarID+" "+
	  												"ORDER BY rootOrder, Root, bLength");
	  		int j = jInit;
	  		int k = 1;
	  		while(rs.next()){
	  			rawData[j][0] = j+1;						// ID
	  			segID[rs.getInt(1)-1] = j+1;				// Segment ID
	  			rawData[j][1] = rs.getFloat(2) - xDev;		// X
	  			rawData[j][2] = -(rs.getFloat(3) - yDev);	// Y
	  			rawData[j][3] = rs.getFloat(4);				// rootOrder
	  			rawData[j][4] = k+1;						// root
	  			rawData[j][5] = rs.getFloat(7);				// bLength
	  			rawData[j][6] = rs.getFloat(8);				// Diam
	  			rawData[j][7] = rs.getFloat(9); 			// aLength
	  			if (rs.getDouble(9) == 0) {
  					imgNames[k] = rs.getString(10);
  					rootNames[k] = rs.getString(5);
	  				if(rawData[j][3] == 0) parentNames[k] = collar;
	  				else parentNames[k] = rs.getString(6);
	  				k++;
	  			}
		  		j++;
	  		}
		}
	  	catch(Exception e){
	  		STRInterface.updateOutput("Connection 5 ratée in SRToRSWMS for "+"EXP"+exp+"_R"+rh+" "+e);
	  	}
	}
	
	/**
	 * Process the data for the segments
	 */
	public void processSegData(){
		float g = growthRate;
		float maxLength = 0;
		
		for(int i = 0; i < nSeg; i++){
			
			// growth rate
			if(rawData[i][3] == 1) g = growthRate/2; 
			if(rawData[i][3] == 2) g = growthRate/3; 
			
			// segID
			segData[i][0] = rawData[i][0];
			
			// x , y, z
			segData[i][1] = rawData[i][1];
			segData[i][2] = 0;
			segData[i][3] = rawData[i][2];
			
			// prev
			if (i == 0) 
				segData[i][4] = 0;
			else if (i != 0 && rawData[i][4] == rawData[i-1][4]) 
				segData[i][4] = segData[i-1][0];
			else if (i != 0 && rawData[i][4] != rawData[i-1][4]) 
				segData[i][4] = getParentNode(segData[i][1], segData[i][3], rawData[i][4], rawData[i][0], (int)rawData[i][3]+1);
			
			// or (order)
			segData[i][5] = rawData[i][3]+1;
			
			// br#
			segData[i][6] = rawData[i][4];
			
			// length
			if (i == 0){
				segData[i][7] = 0.01f;
				maxLength = rawData[i][7];
			}
			else if (i != 0 && rawData[i][5] == 0){
				segData[i][7] = 0.01f;
				maxLength = rawData[i][7];
			}
			else if (i != 0 && rawData[i][5] != 0)
				segData[i][7] = rawData[i][5] - rawData[i-1][5];
			
			if (segData[i][7] < 0) System.out.println("Error: Negative length = "+segData[i][7]);
			
			// surface
			if (i == 0)
				segData[i][8] = 0.01f * rawData[i][6] * (float) Math.PI;
			else if (i != 0 && rawData[i][5] == 0)
				segData[i][8] = 0.01f * rawData[i][6] * (float) Math.PI;
			else if (i != 0 && rawData[i][5] != 0)
				segData[i][8] = segData[i][7] * rawData[i][6] * (float) Math.PI;

			// mass
			segData[i][9] = 0;
			
			// origination time
			segData[i][10] = rawData[i][5] * (finalDate / maxLength);
			
			// apex lenght
			segData[i][11] = rawData[i][7];

			// base length
			segData[i][12] = rawData[i][5];
		}
	}
	
	
	/**
	 * Update the origination date of the segment based on the parental node age
	 */
	private void updateOriginationDateData(){
		float d = 0;
		float l = 0;
		int parent = 0;
		for(int i = 0; i < nSeg; i++){
			if(segData[i][5] == 2){ // if second order root
				for(int j = 0; j < nSeg; j++){
					if(segData[i][4] == segData[j][0]){  // find the parent node
						if(segData[i][6] != segData[j][6]){
							segData[i][10] = segData[j][10] + LAUZ; //
							d = segData[i][10];
							l = segData[i][11];		
							parent = j;
						}
						break;
					}	
				}
				for(int j = 0; j < nSeg; j++){
					if(segData[i][4] == segData[j][0]){  // find the parent node
						if(segData[i][6] == segData[j][6])
							segData[i][10] = (segData[i][12] * (finalDate - d) / l) + d; 
						break;
					}	
				}
			}
		}
		for(int i = 0; i < nSeg; i++){
			if(segData[i][5] == 3){ // if second order root
				for(int j = 0; j < nSeg; j++){
					if(segData[i][4] == segData[j][0]){  // find the parent node
						if(segData[i][6] != segData[j][6]){
							segData[i][10] = segData[i][10] + segData[j][10] + LAUZ; //
							d = segData[i][10];
							l = segData[i][11];	
							parent = j;
						}
						break;
					}	
				}
				for(int j = 0; j < nSeg; j++){
					if(segData[i][4] == segData[j][0]){  // find the parent node
						if(segData[i][6] == segData[j][6])
							segData[i][10] = (segData[i][12] * (finalDate - d) / l) + d; 
						break;
					}	
				}
			}
		}
	}
	
	/**
	 * Process the apex data
	 */
	public void processApexData(){
		int sg = 0;
		int j = 0;
		for(int i = 0; i < nSeg; i++){
			if(rawData[i][7] == 0){ // alength = 0
				// tipID
				apexData[j][0] = rawData[i][4];
				
				// xg, yg, zg
				apexData[j][1] = rawData[i][1];
				apexData[j][2] = 0;
				apexData[j][3] = rawData[i][2];
	
				//sg.bhd.tp
				apexData[j][4] = sg;
				sg = 0;
				
				//ord
				apexData[j][5] = rawData[i][3] + 1;
	
				//br
				apexData[j][6] = rawData[i][4];
	
				// tot.br.lgth
				apexData[j][7] = rawData[i][5];
				
				j++;
			}
			else sg++;
		}
		
	}
	
	/**
	 * Get the parent node based on proximity (newtonian distance)
	 * @param x
	 * @param y
	 * @param root
	 * @param node
	 * @param order
	 * @return
	 */
	public float getParentNode(float x, float y, float root, float node, int order){
		float f = root;
		float dist = 1e10f;
		String n = parentNames[(int)root - 1];
		
		String re = "SELECT ID, x, y FROM EXP"+exp+"_R"+rh+"_N WHERE Root = '"+n+"'";
		
		try{
	  		rs=sql.executeQuery(re);
	  		
	  		float d;
	  		while(rs.next()){
		  		d = (float) Point2D.distance((x+xDev), -(y+yDev), rs.getFloat(2), rs.getFloat(3));
		  		if (d < dist){
		  			dist = d;
		  			f = segID[rs.getInt(1)-1];
		  		}
	  		}	  		
		}
		
	  	catch(Exception e){
	  		STRInterface.updateOutput("Connection 6 ratée in SRToRSWMS for "+"EXP"+exp+"_R"+rh+" "+e);
	  	}
	  	if(root == f) STRInterface.updateOutput("--- Possible error while finding parent of root #"+(int)root+" (order = "+(int) order+")"+
	  			" in EXP"+exp+"_R"+rh+": #parent node ("+(int)f+") = #current root("+(int)root+")");
	  	if(node == f) STRInterface.updateOutput("--- Error while finding parent of root #"+(int)root+" (order = "+(int) order+")"+
	  			" in EXP"+exp+"_R"+rh+": #parent node ("+(int)f+") = #current node("+(int)node+")");
		return f;
	}
	
	/**
	 * Print the data in an external file
	 */
	public void printData(){
	
		try{
			  // BRANCHES
			  PrintWriter root = new PrintWriter(new FileWriter(outFolder+"RootSys_"+"EXP"+exp+"_R"+rh));
			  root.println("Time");
			  root.println(finalDate);
			  root.println("");
			  root.println("Number of seeds");
			  root.println(" 1");
			  root.println("");
			  root.println("ID, X and Y coordinates of the seeds (one per line)");
			  root.println(" 1 "+segData[0][1]+" "+segData[0][2]);
			  root.println("");
			  root.println("Root DM, shoot DM, leaf area:");
			  root.println(" 0.0 0.0 0.0");
			  root.println("");
			  root.println("Average soil strength and solute concentration experienced by root system:");
			  root.println(" 0.0 0.0");
			  root.println("");
			  root.println("Total # of axes");
			  root.println(" "+1);
			  root.println("");
			  root.println("Total # of branches, including axis(es)");
			  root.println(" "+nApex);
			  root.println("");
			  root.println("Total # of segments records:");
			  root.println(" "+nSeg);
			  root.println("");
			  root.println("segID#	x	y	z	prev	or	br#	length	surface	mass");
			  root.println("origination time");
			  for (int i=0; i < nSeg; i++){
				  root.println(" "+(int) segData[i][0]+" "+segData[i][1]+" "+segData[i][2]+" "+segData[i][3]+" "+(int) segData[i][4]+" "+
						  (int) segData[i][5]+" "+(int) segData[i][6]+" "+segData[i][7]+" "+segData[i][8]+" "+segData[i][9]);
				  root.println(" "+segData[i][10]);
			  }
			  
			  // APEXES
			  root.println("");
			  root.println("Total # of growing branch tips:");
			  root.println(" "+nApex);
			  root.println("");
			  root.println("tipID#	xg	yg	zg	sg.bhd.tp	ord	br#	tot.br.lgth	axs#");
			  root.println("overlength # of estblished points");
			  root.println("time of establishing (-->)");
			  
			  for (int i=0; i < nApex; i++){
				  root.println(" "+(int) apexData[i][0]+" "+apexData[i][1]+" "+apexData[i][2]+" "+apexData[i][3]+" "+(int) apexData[i][4]+" "+
						  (int) apexData[i][5]+" "+(int) apexData[i][6]+" "+apexData[i][7]+" "+(int) apexData[i][8]);
				  root.println(" "+apexData[i][9]+" "+(int) apexData[i][10]);
			  }
			  root.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Start the SQL connection
	 * @param driver
	 * @param url
	 * @param user
	 * @param pw
	 * @return
	 */
	public boolean startSQL(String driver, String url, String user, String pw) {
		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			STRInterface.updateOutput("The driver " + driver + " was not found.");
			return false;
		}
		try {
			con = DriverManager.getConnection(url, user, pw);
			sql = con.createStatement(rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY);     
		}
		catch (SQLException sqlE) {
			STRInterface.updateOutput("The specified database was not found.");
			STRInterface.updateOutput(url);
			return false;
		}
		return true;
	}
}
