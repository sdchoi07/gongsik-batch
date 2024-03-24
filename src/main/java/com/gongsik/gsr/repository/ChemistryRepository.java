package com.gongsik.gsr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongsik.gsr.entity.ChemistryEntity;

@Repository
public interface ChemistryRepository extends JpaRepository<ChemistryEntity, String> {

	Optional<ChemistryEntity> findByChemistryNo(String gubun3);


}
