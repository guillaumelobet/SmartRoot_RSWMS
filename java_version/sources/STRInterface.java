/**
 * @author Guillaume Lobet, Université catholique de Louvain, Earth and Life Institute
 * 
 * This class produce the interface 
 */

package sr_to_rswms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class STRInterface extends JFrame implements ActionListener{
		
		private static final long serialVersionUID = 702956125300963149L;
		
		boolean b1 = false;
		boolean b2 = false;
		boolean b3 = false;
		boolean b4 = false;
		boolean b5 = false;
		boolean b6 = false;
		
		JLabel experience = new JLabel("Experience Info ");	
		JLabel exp = new JLabel("Experience number ");
		JLabel rh = new JLabel("Number of rhizotrons ");
		JLabel growth = new JLabel("Mean growth rate [cm/days]");

		JLabel database = new JLabel("Database Info ");
		JLabel name = new JLabel("Name ");
		JLabel usr = new JLabel("Username ");
		JLabel psw = new JLabel("Password ");
		JLabel conn = new JLabel("Connection ");
		JLabel dr = new JLabel("Driver ");
		
		JTextField expF = new JTextField("XIII", 5); 
		JTextField rhF = new JTextField("17", 5); 
		JTextField nameF = new JTextField("SmartRoot", 20); 
		JTextField usrF = new JTextField("root", 20); 
		JTextField pswF = new JTextField("", 20);
		JTextField connF = new JTextField("jdbc:mysql://localhost/"); 
		JTextField drF = new JTextField("com.mysql.jdbc.Driver");
		JTextField growthF = new JTextField("2.0", 5);
		JLabel output = new JLabel("Output options ");
		JTextField outputFolderName = new JTextField("/Users/guillaumelobet/Desktop/", 25);
		
		public static JTextArea textOutput;
		
		JButton outputChooseFolder;
		
		private JPanel container;
		
		private JButton buttonRun;
		private JLabel title;
		
		public STRInterface(){
			super();
			build();
		}
		
		private void build(){
			this.setTitle("SmartRoot to R-SWMS"); 					
			this.setSize(540,660); 									
			this.setLocationRelativeTo(null); 						
			this.setResizable(false) ; 								
			this.getContentPane().add(getContainer());
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 	
		}

		private JPanel getContainer(){
			container = new JPanel(new BorderLayout()) ; 								
		     
			title = new JLabel("");
			title.setFont(new java.awt.Font("Square721 Ex BT", java.awt.Font.BOLD, 30));
			title.setBounds(50, 20, 500, 35);;
						
			outputChooseFolder = new JButton("Choose output folder");
			outputChooseFolder.setActionCommand("OUTPUT_FOLDER");
			outputChooseFolder.addActionListener(this);

			
			// Experience block
			
		    GridBagLayout trsfGb = new GridBagLayout();

		    JPanel trsfPanel = new JPanel();
		    trsfPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		    GridBagConstraints trsfc1 = new GridBagConstraints();
		    trsfPanel.setLayout(trsfGb);
	      
		    trsfc1.fill = GridBagConstraints.HORIZONTAL;

		    trsfc1.gridx = 0;
		    trsfc1.gridy = 1;
		    trsfPanel.add(exp, trsfc1);
		    trsfc1.gridx = 1;
		    trsfPanel.add(expF, trsfc1);
	      
		    trsfc1.gridx = 0;
		    trsfc1.gridy = 2;
		    trsfPanel.add(rh, trsfc1);
		    trsfc1.gridx = 1;
		    trsfPanel.add(rhF, trsfc1);
		    
		    trsfc1.gridx = 0;
		    trsfc1.gridy = 3;
		    trsfPanel.add(growth, trsfc1);
		    trsfc1.gridx = 1;
		    trsfPanel.add(growthF, trsfc1);
		    
	      	JPanel panel1 = new JPanel(new BorderLayout());
	      	panel1.setBorder(BorderFactory.createLineBorder(Color.gray));
	      	panel1.add(trsfPanel, BorderLayout.WEST);
		      
	      	JPanel panel1N = new JPanel(new BorderLayout());
	      	panel1N.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel1N.add(experience, BorderLayout.NORTH);
	      	panel1N.add(panel1, BorderLayout.SOUTH);
	      	
	      	
	      	// Database block
	      	
		    JPanel trsfPanel2 = new JPanel();
		    trsfPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		    GridBagConstraints trsfc2 = new GridBagConstraints();
		    trsfPanel2.setLayout(trsfGb);
		    trsfc2.fill = GridBagConstraints.HORIZONTAL;

		    trsfc2.gridx = 0;
		    trsfc2.gridy = 0;
		    trsfPanel2.add(dr, trsfc2);
		    trsfc2.gridx = 1;
		    trsfPanel2.add(drF, trsfc2);
	      
		    trsfc2.gridx = 0;
		    trsfc2.gridy = 1;
		    trsfPanel2.add(conn, trsfc2);
		    trsfc2.gridx = 1;
		    trsfPanel2.add(connF, trsfc2);
		    
		    trsfc2.gridx = 0;
		    trsfc2.gridy = 2;
		    trsfPanel2.add(name, trsfc2);
		    trsfc2.gridx = 1;
		    trsfPanel2.add(nameF, trsfc2);
		    
		    trsfc2.gridx = 0;
		    trsfc2.gridy = 3;
		    trsfPanel2.add(usr, trsfc2);
		    trsfc2.gridx = 1;
		    trsfPanel2.add(usrF, trsfc2);
		    
		    trsfc2.gridx = 0;
		    trsfc2.gridy = 4;
		    trsfPanel2.add(psw, trsfc2);
		    trsfc2.gridx = 1;
		    trsfPanel2.add(pswF, trsfc2);
		    
	      	JPanel panel2 = new JPanel(new BorderLayout());
	      	panel2.setBorder(BorderFactory.createLineBorder(Color.gray));
	      	panel2.add(trsfPanel2, BorderLayout.WEST);
		      
	      	JPanel panel2N = new JPanel(new BorderLayout());
	      	panel2N.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel2N.add(database, BorderLayout.NORTH);
	      	panel2N.add(panel2, BorderLayout.SOUTH);
	      	      	
			// Export block
			
		    JPanel trsfPanel3 = new JPanel();
		    trsfPanel3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		    GridBagConstraints trsfc3 = new GridBagConstraints();
		    trsfPanel3.setLayout(trsfGb);
		    trsfc3.fill = GridBagConstraints.HORIZONTAL;

		    trsfc3.gridx = 0;
		    trsfc3.gridy = 0;
		    trsfPanel3.add(outputFolderName, trsfc3);
		    trsfc3.gridx = 1;
		    trsfPanel3.add(outputChooseFolder, trsfc3);
		    
	      	JPanel panel3 = new JPanel(new BorderLayout());
	      	panel3.setBorder(BorderFactory.createLineBorder(Color.gray));
	      	panel3.add(trsfPanel3, BorderLayout.WEST);
		      
	      	JPanel panel3N = new JPanel(new BorderLayout());
	      	panel3N.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel3N.add(output, BorderLayout.NORTH);
	      	panel3N.add(panel3, BorderLayout.SOUTH);	
	      	
	      	
	      	JPanel panel4 = new JPanel(new BorderLayout());
	      	panel4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel4.add(panel1N, BorderLayout.NORTH);
	      	panel4.add(panel2N, BorderLayout.CENTER);
	      	panel4.add(panel3N, BorderLayout.SOUTH);

			buttonRun = new JButton("RUN");
			buttonRun.addActionListener(this) ;
			buttonRun.setPreferredSize(new Dimension(100, 20));

	      	JPanel panel5 = new JPanel(new BorderLayout());
	      	panel5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel5.add(buttonRun, BorderLayout.EAST);
	      	
	      	
	      	JPanel panel6 = new JPanel(new BorderLayout());
	      	panel6.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel6.add(panel4, BorderLayout.NORTH);
	      	panel6.add(panel5, BorderLayout.SOUTH);
	        			
			
			textOutput = new JTextArea(10, 43);
			JScrollPane scrollPane = new JScrollPane(textOutput); 
			textOutput.setEditable(false);
			
	      	JPanel textCont = new JPanel(new BorderLayout());
	      	textCont.setBorder(BorderFactory.createLineBorder(Color.gray));
	      	textCont.add(scrollPane, BorderLayout.WEST);
			
	      	JPanel panel7 = new JPanel(new BorderLayout());
	      	panel7.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	      	panel7.add(scrollPane, BorderLayout.NORTH);
	      	
	        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/logo.gif")));
	        JLabel logo = new JLabel("",icon, JLabel.CENTER);
	        logo.setPreferredSize(new Dimension(300, 40));
	      	
	      	
			container.add(logo, BorderLayout.NORTH);
			container.add(panel6, BorderLayout.CENTER);
			container.add(panel7, BorderLayout.SOUTH);
			
			return container ;
		}

	
		public static void updateOutput(String t){
			String tPrev = textOutput.getText();;
			String tAct = tPrev+"\n"+t;
			textOutput.setText(tAct);			
		}
		
		public void actionPerformed(ActionEvent e){
		      if (e.getActionCommand() == "OUTPUT_FOLDER") { 
		    	  JFileChooser fc = new JFileChooser();
				   fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				   int returnVal = fc.showDialog(this, "Save");
				   
		          if (returnVal == JFileChooser.APPROVE_OPTION){ 
		        	  String fName = fc.getSelectedFile().toString();
		        	  if(!fName.endsWith("/")) fName = fName.concat("/");

		        	  outputFolderName.setText(fName);
		          }
		      }
		      
		      else if (e.getActionCommand() == "RUN") {
		    	  if(outputFolderName.getText().equals("")){
		    		  updateOutput("Please choose a destination folder");
		    	  }
		    	  else{
		    		  updateOutput("RUN");
			    	  SRToRSWMS.database = nameF.getText();
			    	  SRToRSWMS.usr = usrF.getText();
			    	  SRToRSWMS.psw = pswF.getText();
			    	  SRToRSWMS.connectionPath = connF.getText();
			    	  SRToRSWMS.driver = drF.getText();
			    	  SRToRSWMS.exp = expF.getText();
			    	  SRToRSWMS.numRS = Integer.parseInt(rhF.getText());
			    	  SRToRSWMS.growthRate = Float.parseFloat(growthF.getText());
			    	  SRToRSWMS.outFolder = outputFolderName.getText();
			    	  new SRToRSWMS();
		    	  }
		      }
		}
	}
