package com.kaamsetu.modules.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingRatingDto {

    private UUID assignmentId;
    private UUID jobId;
    private String jobTitle;
    private String category;
    private UUID otherPartyUserId;
    private String otherPartyName;
    private String otherPartyRole; // 'PROVIDER' | 'WORKER'
    private String otherPartyAvatar;
    private LocalDate startDate;
    private LocalDate expectedCompletionDate;
    private LocalDate actualCompletionDate;
    private Instant completedAt;
    private BigDecimal agreedWage;
    private boolean isEligibleForRating; // True if next-day has arrived
}
