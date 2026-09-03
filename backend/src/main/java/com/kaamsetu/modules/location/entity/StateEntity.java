package com.kaamsetu.modules.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "states")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {

    @Id
    @Column(length = 50)
    private String id; // e.g. "IN-MH"

    @Column(name = "country_id", nullable = false, length = 50)
    private String countryId; // "IN"

    @Column(nullable = false, length = 10)
    private String code; // "MH"

    @Column(nullable = false, length = 100)
    private String name; // "Maharashtra"

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
