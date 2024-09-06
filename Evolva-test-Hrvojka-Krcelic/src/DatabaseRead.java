import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseRead {

	Connection con;
	String sch;
	
	public DatabaseRead(String s) {
		sch = s;
		this.connectToDb();
	}
	
	public void connectToDb() {
		try {
			//register the driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			//establish connection
			this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + sch,"hbstudent","hbstudent");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    public void countFromDb() {
    	//store the total amounts by currencies:
    	HashMap<String,Double> totalAmounts = new HashMap<String,Double>(); //dict, key:currency, value:amount
    	
    	try {    		 
            String qCountry = "SELECT * FROM `" + sch + "`.country";
            PreparedStatement pCountry = con.prepareStatement(qCountry);
            ResultSet rCountry = pCountry.executeQuery();
            
            //bufferedWriter for htlm report:
            File f = new File("C:/data/izvjecse.html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("<!DOCTYPE html>  \r\n"
            		+ "<html>  \r\n"
            		+ "<body>  \r\n");
 
            //go through ResultSet
            while (rCountry.next()) {
 
            	HashMap<String,Double> amounts = new HashMap<String,Double>();
            	
                int countryId = rCountry.getInt("id");
                String country = rCountry.getString("country_name");
                //write to html file
                String c = "<h2>" + String.format("%s found.", country) + "</h2>" + "\n";
                bw.write(c);
                
                //use country_id to fetch all the data from savings table for this country
                String qSavings = String.format("SELECT * FROM `%s`.savings WHERE country_id=%d", sch, countryId);
                PreparedStatement pSavings = con.prepareStatement(qSavings);
                ResultSet rSavings = pSavings.executeQuery();
                while(rSavings.next()) {
                	//get values from row in table
                	int id = rSavings.getInt("id");
                    String currency = rSavings.getString("currency");
                    double amount = rSavings.getDouble("amount");
                    
                    if(amounts.keySet().contains(currency)) {
                    	amounts.put(currency, amounts.get(currency) + amount);
                    }
                    else {
                    	amounts.put(currency, amount);
                    }
                }

                //write to html
            	bw.write("<p>Totals by currencies:</p> \n");
                for (String curr: amounts.keySet()) {
                    String ln = "<p>" + curr.toString() + ": " +  amounts.get(curr).toString() + "</p>" + "\n"; 
                	bw.write(ln);
                    
                    //add to total sums:
                    if(totalAmounts.keySet().contains(curr)) {
                    	totalAmounts.put(curr, totalAmounts.get(curr) + amounts.get(curr));
                    }
                    else {
                    	totalAmounts.put(curr, amounts.get(curr));
                    }                    
                }  
                
            }//close while loop fpr country

        	//write to html
        	bw.write("<h2>Money in all countries:</h2> \n");        	
        	for (String curr: totalAmounts.keySet()) {
                String l = "<p>" + curr.toString() + ": " +  totalAmounts.get(curr).toString() + "</p>" + "\n"; 
            	bw.write(l);
        	}
        	//close tags and bw
        	bw.write("</body>  \r\n" + "</html>");
        	bw.close();
	    } catch (Exception e){
	        e.printStackTrace();
	    }
    }	


}
