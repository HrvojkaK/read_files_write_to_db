package com.evolva.santa.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.springframework.stereotype.Component;
import com.evolva.santa.model.Country;
import com.evolva.santa.repo.ICountryRepository;



@Component
public class FileService {
	
    private ICountryRepository repoC;
	
	public FileService(ICountryRepository r) {
		repoC = r;
	}

	public String fillDatabase(String path) {
		
		final File folder = new File(path);
    	if (!folder.isDirectory()) {
    		System.out.print("Folder does not exist");
    		return "error";
    	}
    	
    	for (final File fileEntry : folder.listFiles()) {    		
    		
            if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".csv")) {
            	
            	String fName = fileEntry.getName(); //name of the file           	
            	//check if this contry already in db:            	
            	Country result = repoC.findOneByCountryName(fName);
            	if(result != null) continue;
            	
            	//make new Country class instance
            	Country country = new Country();
            	country.setCountryName(fName);
          	
                String line; //a line from csv file
                try {
                    BufferedReader br = new BufferedReader(new FileReader(path + "/" + fName));
                    while ((line = br.readLine()) != null) {
                        if (!line.isEmpty()){ //line must not be empty
                            String[] values = line.split(",");
                            if(values.length != 3) continue;  //throwaway lines with more or less than 3 entries

                        	String town = values[0];
                        	String currency = values[1];
                            double amount = Double.parseDouble(values[2]);

                            //add this Savings to the country
                            country.addSavings(town, currency, amount);
                         }
                    }
                    br.close();
                } catch (Exception e){
                    e.printStackTrace();
                }//closing try-except: reading the file

                repoC.save(country);
            	
            }//close if: filename ends with .csv
        }//close for: all files
		
		
		return "redirect:/report";
	}
}
