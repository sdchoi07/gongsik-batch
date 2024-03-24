package com.gongsik.gsr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongsik.gsr.entity.SeedEntity;
@Repository
public interface SeedRepository extends JpaRepository<SeedEntity, String>{

	Optional<SeedEntity> findBySeedNo(String gubun3);

}
