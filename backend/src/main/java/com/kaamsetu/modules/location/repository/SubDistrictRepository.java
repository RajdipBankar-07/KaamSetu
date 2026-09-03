package com.kaamsetu.modules.location.repository;

import com.kaamsetu.modules.location.entity.SubDistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubDistrictRepository extends JpaRepository<SubDistrictEntity, String> {
    List<SubDistrictEntity> findByDistrictIdAndIsActiveTrueOrderByNameAsc(String districtId);
    List<SubDistrictEntity> findByDistrictIdOrderByNameAsc(String districtId);
    boolean existsByDistrictIdAndNameIgnoreCase(String districtId, String name);
}
