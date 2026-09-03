package com.kaamsetu.modules.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewRequest {

    private UUID assignmentId;

    private UUID jobId;

    private UUID revieweeId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Minimum rating is 1.0")
    @DecimalMax(value = "5.0", message = "Maximum rating is 5.0")
    private BigDecimal rating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal punctualityRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal qualityRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal behaviorRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal workManagementRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal paymentExperienceRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal timeManagementRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal reliabilityRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal skillRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal overallExperienceRating;

    private String reviewText;
}
