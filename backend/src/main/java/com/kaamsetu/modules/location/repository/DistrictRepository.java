package com.kaamsetu.modules.location.repository;

import com.kaamsetu.modules.location.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, String> {
    List<DistrictEntity> findByStateIdOrderByNameAsc(String stateId);
}
