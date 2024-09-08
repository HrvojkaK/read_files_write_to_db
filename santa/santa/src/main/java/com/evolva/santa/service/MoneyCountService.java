package com.evolva.santa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.evolva.santa.model.Country;
import com.evolva.santa.model.MoneyCount;
import com.evolva.santa.model.Savings;
import com.evolva.santa.repo.ICountryRepository;

@Service
public class MoneyCountService {

	private ICountryRepository repoC;
	
	public MoneyCountService(ICountryRepository r) {
		repoC = r;
	}
	
	
	public List<MoneyCount> getCount() {
		
		List<Country> countries = repoC.findAll();
		
		List<MoneyCount> counts = new ArrayList<MoneyCount>();		
		HashMap<String,Double> totals = new HashMap<String,Double>(); //for total sum over all countries
		
		for(Country country: countries) {
			
			MoneyCount count = new MoneyCount();
			//get country name
			count.setMoneyLocation(country.getCountryName() + " found.");
			//go through the savings in different currencies
			for(Savings sav: country.getSavings()) {
				//currency name: if the count obj does not have it yet, set it
				HashMap<String,Double> aux = count.getAmountsCurrencies();				
				String auxKey = sav.getCurrency(); //currency name				
				
				if(aux.containsKey(sav.getCurrency())) {
					//just add to old amount
					aux.put(auxKey, aux.get(auxKey) + sav.getAmount());
					count.setAmountsCurrencies(aux);					
				} else { //add currency, amount pair 
					aux.put(auxKey, sav.getAmount());
					count.setAmountsCurrencies(aux);
				}
				
				//add to totals:
				if(totals.containsKey(auxKey)) {
					totals.put(auxKey, totals.get(auxKey) + sav.getAmount());
				}
				else {
					totals.put(auxKey, sav.getAmount());
				}
			}			
			//add count to the list
			counts.add(count);
		}
		//ad a final one, for all countries (totals):
		MoneyCount fin = new MoneyCount();
		fin.setMoneyLocation("Money in all countries:");
		fin.setAmountsCurrencies(totals);
		counts.add(fin);
		
		return counts;
	}

}
