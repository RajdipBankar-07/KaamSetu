package com.kaamsetu.modules.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "districts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictEntity {

    @Id
    @Column(length = 50)
    private String id; // e.g. "dist-pune"

    @Column(name = "state_id", nullable = false, length = 50)
    private String stateId; // "IN-MH"

    @Column(length = 20)
    private String code; // "PUN"

    @Column(nullable = false, length = 100)
    private String name; // "Pune"

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "name_hi", length = 100)
    private String nameHi;

    @Column(name = "name_mr", length = 100)
    private String nameMr;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
