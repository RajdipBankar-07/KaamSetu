package com.kaamsetu.core.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaamsetu.modules.location.entity.*;
import com.kaamsetu.modules.location.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Seeds Complete All-India Administrative Location Hierarchy Master Dataset.
 * Covers all 36 States & UTs, nationwide Districts, Sub-Districts, and Villages.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class LocationDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final SubDistrictRepository subDistrictRepository;
    private final TalukaRepository talukaRepository;
    private final VillageRepository villageRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        patchSchema();
        seedLocationCatalog();
    }

    private void patchSchema() {
        try {
            // Master tables column patching
            jdbcTemplate.execute("ALTER TABLE IF EXISTS countries ADD COLUMN IF NOT EXISTS name_en VARCHAR(100)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS countries ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE");

            jdbcTemplate.execute("ALTER TABLE IF EXISTS states ADD COLUMN IF NOT EXISTS name_en VARCHAR(100)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS states ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE");

            jdbcTemplate.execute("ALTER TABLE IF EXISTS districts ADD COLUMN IF NOT EXISTS code VARCHAR(20)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS districts ADD COLUMN IF NOT EXISTS name_en VARCHAR(100)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS districts ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE");

            // Sub-districts table DDL creation if not exists
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS sub_districts (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "district_id VARCHAR(50) NOT NULL, " +
                "code VARCHAR(20), " +
                "name VARCHAR(100) NOT NULL, " +
                "name_en VARCHAR(100), " +
                "name_hi VARCHAR(100), " +
                "name_mr VARCHAR(100), " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE" +
                ")"
            );

            jdbcTemplate.execute("ALTER TABLE IF EXISTS villages ADD COLUMN IF NOT EXISTS sub_district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS villages ADD COLUMN IF NOT EXISTS taluka_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS villages ADD COLUMN IF NOT EXISTS code VARCHAR(20)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS villages ADD COLUMN IF NOT EXISTS name_en VARCHAR(150)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS villages ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE");

            // User & Entity schema patching
            jdbcTemplate.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS country_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS state_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS sub_district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS taluka_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS village_id VARCHAR(50)");

            jdbcTemplate.execute("ALTER TABLE IF EXISTS workers ADD COLUMN IF NOT EXISTS country_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS workers ADD COLUMN IF NOT EXISTS state_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS workers ADD COLUMN IF NOT EXISTS district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS workers ADD COLUMN IF NOT EXISTS sub_district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS workers ADD COLUMN IF NOT EXISTS taluka_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS workers ADD COLUMN IF NOT EXISTS village_id VARCHAR(50)");

            jdbcTemplate.execute("ALTER TABLE IF EXISTS providers ADD COLUMN IF NOT EXISTS country_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS providers ADD COLUMN IF NOT EXISTS state_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS providers ADD COLUMN IF NOT EXISTS district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS providers ADD COLUMN IF NOT EXISTS sub_district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS providers ADD COLUMN IF NOT EXISTS taluka_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS providers ADD COLUMN IF NOT EXISTS village_id VARCHAR(50)");

            jdbcTemplate.execute("ALTER TABLE IF EXISTS jobs ADD COLUMN IF NOT EXISTS country_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS jobs ADD COLUMN IF NOT EXISTS state_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS jobs ADD COLUMN IF NOT EXISTS district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS jobs ADD COLUMN IF NOT EXISTS sub_district_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS jobs ADD COLUMN IF NOT EXISTS taluka_id VARCHAR(50)");
            jdbcTemplate.execute("ALTER TABLE IF EXISTS jobs ADD COLUMN IF NOT EXISTS village_id VARCHAR(50)");
        } catch (Exception e) {
            log.debug("Location schema column patch note: {}", e.getMessage());
        }
    }

    private void seedLocationCatalog() {
        log.info("🌱 [LocationDataInitializer] Initializing Complete India Administrative Location Master Data...");

        // 1. Country
        CountryEntity india = CountryEntity.builder()
                .id("IN")
                .code("IN")
                .name("India")
                .nameEn("India")
                .nameMr("भारत")
                .nameHi("भारत")
                .isActive(true)
                .build();
        countryRepository.save(india);

        // 2. States & Union Territories (36 States & UTs)
        try {
            ClassPathResource res = new ClassPathResource("data/india_states.json");
            if (res.exists()) {
                try (InputStream is = res.getInputStream()) {
                    List<StateEntity> states = objectMapper.readValue(is, new TypeReference<List<StateEntity>>() {});
                    stateRepository.saveAll(states);
                    log.info("  ✓ Loaded {} States & Union Territories", states.size());
                }
            }
        } catch (Exception e) {
            log.error("Failed loading states dataset: {}", e.getMessage());
        }

        // 3. All-India Districts
        try {
            ClassPathResource res = new ClassPathResource("data/india_districts.json");
            if (res.exists()) {
                try (InputStream is = res.getInputStream()) {
                    List<DistrictEntity> districts = objectMapper.readValue(is, new TypeReference<List<DistrictEntity>>() {});
                    districtRepository.saveAll(districts);
                    log.info("  ✓ Loaded {} Districts across India", districts.size());
                }
            }
        } catch (Exception e) {
            log.error("Failed loading districts dataset: {}", e.getMessage());
        }

        // 4. Sub-Districts / Talukas / Tehsils
        try {
            ClassPathResource res = new ClassPathResource("data/india_subdistricts.json");
            if (res.exists()) {
                try (InputStream is = res.getInputStream()) {
                    List<SubDistrictEntity> subDistricts = objectMapper.readValue(is, new TypeReference<List<SubDistrictEntity>>() {});
                    subDistrictRepository.saveAll(subDistricts);

                    // Sync into TalukaEntity for backward compatibility
                    for (SubDistrictEntity sd : subDistricts) {
                        TalukaEntity t = TalukaEntity.builder()
                                .id(sd.getId())
                                .districtId(sd.getDistrictId())
                                .code(sd.getCode())
                                .name(sd.getName())
                                .nameEn(sd.getNameEn())
                                .nameHi(sd.getNameHi())
                                .nameMr(sd.getNameMr())
                                .isActive(sd.getIsActive())
                                .build();
                        talukaRepository.save(t);
                    }
                    log.info("  ✓ Loaded {} Sub-Districts/Talukas", subDistricts.size());
                }
            }
        } catch (Exception e) {
            log.error("Failed loading sub-districts dataset: {}", e.getMessage());
        }

        // 5. Villages
        try {
            ClassPathResource res = new ClassPathResource("data/india_villages.json");
            if (res.exists()) {
                try (InputStream is = res.getInputStream()) {
                    List<VillageEntity> villages = objectMapper.readValue(is, new TypeReference<List<VillageEntity>>() {});
                    villageRepository.saveAll(villages);
                    log.info("  ✓ Loaded {} Villages", villages.size());
                }
            }
        } catch (Exception e) {
            log.error("Failed loading villages dataset: {}", e.getMessage());
        }

        log.info("✅ [LocationDataInitializer] Complete India Location Hierarchy Master Data successfully initialized.");
    }
}
