package com.evolva.santa.model;

import java.util.HashMap;

public class MoneyCount {

	private String moneyLocation;
	
	private HashMap<String,Double> amountsCurrencies = new HashMap<String,Double>();
	
	public String getMoneyLocation() {
		return moneyLocation;
	}

	public void setMoneyLocation(String moneyLocation) {
		this.moneyLocation = moneyLocation;
	}

	public HashMap<String, Double> getAmountsCurrencies() {
		return amountsCurrencies;
	}

	public void setAmountsCurrencies(HashMap<String, Double> amountsCurrencies) {
		this.amountsCurrencies = amountsCurrencies;
	}

	

}
