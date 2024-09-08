package com.evolva.santa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.evolva.santa.model.Savings;


public interface ISavingsRepository extends JpaRepository<Savings, Integer>{

}
