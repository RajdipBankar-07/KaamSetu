package com.kaamsetu.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveReportRequest {

    @NotBlank(message = "Resolution status is required")
    private String status; // 'RESOLVED' | 'DISMISSED'

    private String resolutionNotes;

    private String targetAction; // 'NONE' | 'ISSUE_WARNING' | 'RESTRICT_USER' | 'BAN_USER'
}
