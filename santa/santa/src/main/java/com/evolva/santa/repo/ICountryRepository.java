package com.evolva.santa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.evolva.santa.model.Country;


public interface ICountryRepository extends JpaRepository<Country, Integer> {
	Country findOneByCountryName(String countryName);

}
