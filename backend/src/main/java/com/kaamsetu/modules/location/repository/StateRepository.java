package com.kaamsetu.modules.location.repository;

import com.kaamsetu.modules.location.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, String> {
    List<StateEntity> findByCountryIdOrderByNameAsc(String countryId);
}
