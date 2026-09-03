package com.kaamsetu.modules.location.repository;

import com.kaamsetu.modules.location.entity.VillageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<VillageEntity, String> {
    
    @Query("SELECT v FROM VillageEntity v WHERE (v.subDistrictId = :subDistrictId OR v.talukaId = :subDistrictId) AND v.isActive = true ORDER BY v.name ASC")
    List<VillageEntity> findBySubDistrictIdOrTalukaId(@Param("subDistrictId") String subDistrictId);

    @Query("SELECT v FROM VillageEntity v WHERE (v.subDistrictId = :subDistrictId OR v.talukaId = :subDistrictId) AND v.isActive = true AND " +
           "(LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(COALESCE(v.nameEn, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(COALESCE(v.nameMr, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(COALESCE(v.nameHi, '')) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY v.name ASC")
    List<VillageEntity> searchVillages(@Param("subDistrictId") String subDistrictId, @Param("query") String query);

    List<VillageEntity> findByTalukaIdOrderByNameAsc(String talukaId);
}
