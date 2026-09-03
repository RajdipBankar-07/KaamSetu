package com.kaamsetu.modules.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity {

    @Id
    @Column(length = 50)
    private String id; // e.g. "IN"

    @Column(nullable = false, unique = true, length = 10)
    private String code; // "IN"

    @Column(nullable = false, length = 100)
    private String name; // "India"

    @Column(name = "name_en", length = 100)
    @Builder.Default
    private String nameEn = "India";

    @Column(name = "name_hi", length = 100)
    @Builder.Default
    private String nameHi = "भारत";

    @Column(name = "name_mr", length = 100)
    @Builder.Default
    private String nameMr = "भारत";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
