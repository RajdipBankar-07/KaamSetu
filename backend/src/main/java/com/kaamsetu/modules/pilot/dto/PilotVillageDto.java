package com.kaamsetu.modules.pilot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PilotVillageDto {
    private String district;
    private String taluka;
    private String talukaMr;
    private String villageName;
    private String villageNameMr;
    private String pinCode;
    private long activeWorkers;
    private long activeProviders;
    private long openJobs;
    private List<String> prominentSectors;
}
