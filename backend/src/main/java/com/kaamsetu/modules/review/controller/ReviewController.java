package com.kaamsetu.modules.review.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.review.dto.SubmitReviewRequest;
import com.kaamsetu.modules.review.entity.ReviewEntity;
import com.kaamsetu.modules.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Review & Rating Module", description = "Bilateral ratings, reviews, and trust feedback")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit a bilateral multi-dimensional rating and review")
    public ResponseEntity<ApiResponse<ReviewEntity>> submitReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SubmitReviewRequest request) {
        ReviewEntity review = reviewService.submitReview(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(review, "review.submitted", "Review submitted successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all completed assignments eligible for next-day rating for current user")
    public ResponseEntity<ApiResponse<List<com.kaamsetu.modules.review.dto.PendingRatingDto>>> getPendingRatings(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<com.kaamsetu.modules.review.dto.PendingRatingDto> list = reviewService.getPendingRatings(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reviews received by a user")
    public ResponseEntity<ApiResponse<List<ReviewEntity>>> getUserReviews(@PathVariable UUID userId) {
        List<ReviewEntity> list = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
