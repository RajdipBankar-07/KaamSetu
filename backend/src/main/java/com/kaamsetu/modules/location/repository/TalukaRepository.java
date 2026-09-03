package com.kaamsetu.modules.location.repository;

import com.kaamsetu.modules.location.entity.TalukaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalukaRepository extends JpaRepository<TalukaEntity, String> {
    List<TalukaEntity> findByDistrictIdOrderByNameAsc(String districtId);
}
