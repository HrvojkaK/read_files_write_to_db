import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseFill {
	
	Connection con;
	final String sch;
	
	public DatabaseFill(String s) {
		sch = s;
		this.connectToDb();
		this.createTables();
	}
	
	public void fill() {
    	final File folder = new File("C:/data");
    	if (!folder.isDirectory()) {
    		System.out.print("Folder does not exist");
    		return;
    	}
    	
    	for (final File fileEntry : folder.listFiles()) {	   		
            if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".csv")) {          	
            	String fileName = fileEntry.getName();
            	String fName = fileName.substring(0, fileName.length() - 4);
            	//System.out.println("\n" + String.format("%s found.", fName));
            	
                String line;
                try {
                	//check if country with country_name=fName exists as an entry in country table (if not, insert), get id
                	int countryId = -1;
                	
                	while(true) { 
                		String q = String.format("SELECT id FROM `%s`.country where country_name='%s';", sch, fName.toLowerCase());
                    	PreparedStatement st = this.con.prepareStatement(q);
                    	ResultSet rs = st.executeQuery();
                    	if(rs.next()) {
                    		countryId = rs.getInt(1);
                    		break;
                    	}
                    	else {
                    		//System.out.println("INSERTING "+countryId);
                    		//insert country into database 
                    		String qIn = String.format("INSERT INTO `%s`.country (country_name) VALUES ('%s');", sch, fName.toLowerCase());
                    		Statement s = this.con.createStatement();
                    		s.execute(qIn);
                    	}                		
                	}
                	            	
                	//read content of cvs file and fill savings table if it is empty
                    BufferedReader br = new BufferedReader(new FileReader("C:/data/" + fileName));
                    while ((line = br.readLine()) != null) {
                        if (!line.isEmpty()){ //line must not be empty
                            String[] values = line.split(",");
                            if(values.length != 3) continue;                        

                            try{
                            	String town = values[0];
                            	String currency = values[1];
                                double amount = Double.parseDouble(values[2]);

                                int savingsId = -1;
                                while(true) {
                            		String q = String.format("SELECT * FROM `%s`.savings WHERE country_id=%d AND town='%s' AND currency='%s';", sch, countryId,town,currency);
                                	PreparedStatement st = this.con.prepareStatement(q);
                                	ResultSet rs = st.executeQuery();

                                	if(rs.next()) {
                                		savingsId = rs.getInt("id");
                                		break;
                                	}
                                	else {
                                		//System.out.println("savings INSERTING "+savingsId);
                                		//insert country into database 
                                		String qIn = String.format("INSERT INTO `%s`.savings (town,currency,amount,country_id) VALUES ('%s','%s',%f,%d)", sch, town,currency,amount,countryId);
                                		Statement s = this.con.createStatement();
                                		s.execute(qIn);
                                	}                		
                            	}
                                
                            } catch (NumberFormatException e) {
                                System.out.println("Caught str->double; exception: " + e);
                            } catch (ArrayIndexOutOfBoundsException e){
                                System.out.println("Array out of bounds exception caught: " + e);
                            } catch (Exception e) {
                                System.out.println("Caught exception: " + e); //NullPointerException , NumberFormatException
                            }
                         }
                    }//while
                    
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    System.out.println("caught exc file not found"); //
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.out.println("exc 2, IO exc"); //
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("some exception"); //
                }//closing try-except: reading the file
             
            }//close if: filename ends with .csv
        }//close for: all files    	
	}
	
	
	public void connectToDb() {
		try {
			//register the driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			//establish connection
			this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+sch,"hbstudent","hbstudent");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void createTables() {
		DatabaseMetaData dbm;
		ResultSet tables;
		try {
			Statement stm = this.con.createStatement();
			dbm = con.getMetaData();
			// check if "country" table is there
			tables = dbm.getTables(sch, null, "COUNTRY", new String[] {"TABLE"});
			if (tables.next()) {
				// Table exists	
				//System.out.println("POSTOJI ");
			}			
			else {
			  // Table does not exist   
				String query = "CREATE TABLE `"+ sch +"`.`country` (\r\n"
						+ "						  `id` INT NOT NULL AUTO_INCREMENT,\r\n"
						+ "						  `country_name` VARCHAR(45) NOT NULL,\r\n"
						+ "						  PRIMARY KEY (`id`));";
				
				stm.execute(query);				
			}
			
			//check if "savings" table exists
			tables = dbm.getTables(sch, null, "SAVINGS", new String[] {"TABLE"});		
			if (tables.next()) {
				// Table exists				
			}			
			else {
			  // Table does not exist    
				String query = "CREATE TABLE `" +sch+ "`.`savings` (\r\n"
						+ "					  `id` INT NOT NULL AUTO_INCREMENT,\r\n"
						+ "					  `town` VARCHAR(45) NOT NULL,\r\n"
						+ "					  `currency` VARCHAR(5) NOT NULL,\r\n"
						+ "					  `amount` DOUBLE NOT NULL,\r\n"
						+ "					  `country_id` INT NOT NULL,\r\n"
						+ "					  PRIMARY KEY (`id`),\r\n"
						+ "					  INDEX `country_id_idx` (`country_id` ASC) VISIBLE,\r\n"
						+ "					  CONSTRAINT `country_id`\r\n"
						+ "					    FOREIGN KEY (`country_id`)\r\n"
						+ "					    REFERENCES `santas-csvs`.`country` (`id`)\r\n"
						+ "					    ON DELETE NO ACTION\r\n"
						+ "					    ON UPDATE NO ACTION);";
				
				stm.execute(query);				
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

}
