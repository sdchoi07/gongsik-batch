package com.gongsik.gsr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gongsik.gsr.entity.InventoryEntity;


@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, String>{

	Optional<InventoryEntity> findByInvenLClsNoAndInvenMClsNoAndInvenSClsNo(String gubun1, String gubun2,
			String gubun3);

}
