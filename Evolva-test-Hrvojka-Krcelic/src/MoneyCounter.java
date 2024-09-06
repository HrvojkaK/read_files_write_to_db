import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MoneyCounter {

	public static void main(String[] args) {
		//MoneyCounter mc = new MoneyCounter();
		//mc.count();
		
		//will check if tables exist (if not, will create them), and fill them with data
		DatabaseFill df = new DatabaseFill("santas-csvs");
		df.fill();
		//will read data from db, and write a html report
		DatabaseRead dr = new DatabaseRead("santas-csvs");
		dr.countFromDb();

	}

    public void count() {
    	System.out.print("Searching for money in C:/data");
    	
    	final File folder = new File("C:/data");
    	if (!folder.isDirectory()) {
    		System.out.print("Folder does not exist");
    		return;
    	}
    	//store the total amounts by currencies:
    	HashMap<String,Double> totalAmounts = new HashMap<String,Double>(); //dict, key:currency, value:amount
    	for (final File fileEntry : folder.listFiles()) {    		
    		
            if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".csv")) { //only files with .csv extension            	
            	String fName = fileEntry.getName();
            	System.out.println("\n" + String.format("\"%s\" found.", fName)); //print what file is found
            	
                String line; //a line in csv file
                HashMap<String,Double> amounts = new HashMap<String,Double>(); //map for just this one file's data
                try {
                    BufferedReader br = new BufferedReader(new FileReader("C:/data/" + fName));
                    while ((line = br.readLine()) != null) {
                        if (!line.isEmpty()){ //line must not be empty
                            String[] values = line.split(","); //COMMA separated value
                            if(values.length != 3) continue;                       

                            try{
                            	String currency = values[1];
                                double amount = Double.parseDouble(values[2]);

                                if(amounts.keySet().contains(currency)) { //if map already has this currency, just add to old val
                                	amounts.put(currency, amounts.get(currency) + amount);
                                }
                                else {
                                	amounts.put(currency, amount);
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Caught str->double; exception: " + e);
                            } catch (ArrayIndexOutOfBoundsException e){
                                System.out.println("Array out of bounds exception caught: " + e);
                            } catch (Exception e) {
                                System.out.println("Caught exception: " + e);
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
                
                System.out.println("Totals by currencies:");
                for (String curr: amounts.keySet()) {
                    System.out.println(curr.toString() + ": " +  amounts.get(curr).toString());             
                    //add to total sums:
                    if(totalAmounts.keySet().contains(curr)) {
                    	totalAmounts.put(curr, totalAmounts.get(curr) + amounts.get(curr));
                    }
                    else {
                    	totalAmounts.put(curr, amounts.get(curr));
                    }                    
                }
            	
            }//close if: filename ends with .csv
        }//close for: all files
    	
    	//print the sums of it
    	System.out.println("\n" + "Money in all countries:");
    	for (String curr: totalAmounts.keySet()) {
            System.out.println(curr.toString() + ": " +  totalAmounts.get(curr).toString());
    	}
    }	
	
}
