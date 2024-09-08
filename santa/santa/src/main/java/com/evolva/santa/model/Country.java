package com.evolva.santa.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="country")
public class Country {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="country_name", nullable=false, unique=true)
	private String countryName;

	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
	private List<Savings> savings = new ArrayList<>();
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public void addSavings(String town, String currency, double amount) {
		this.savings.add(new Savings(town, currency, amount, this));
	}
	
	public List<Savings> getSavings() {
		return this.savings;
	}

}
