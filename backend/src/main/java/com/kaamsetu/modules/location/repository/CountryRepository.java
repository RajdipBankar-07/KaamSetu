package com.kaamsetu.modules.location.repository;

import com.kaamsetu.modules.location.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, String> {
    Optional<CountryEntity> findByCodeIgnoreCase(String code);
}
