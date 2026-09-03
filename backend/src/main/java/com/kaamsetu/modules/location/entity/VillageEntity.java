package com.kaamsetu.modules.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "villages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageEntity {

    @Id
    @Column(length = 50)
    private String id; // e.g. "vil-ranjangaon"

    @Column(name = "sub_district_id", length = 50)
    private String subDistrictId; // e.g. "subdist-shirur" or "tal-shirur"

    @Column(name = "taluka_id", length = 50)
    private String talukaId; // backward compatibility alias

    @Column(length = 20)
    private String code;

    @Column(nullable = false, length = 150)
    private String name; // "Ranjangaon"

    @Column(name = "name_en", length = 150)
    private String nameEn;

    @Column(name = "name_hi", length = 150)
    private String nameHi;

    @Column(name = "name_mr", length = 150)
    private String nameMr;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
