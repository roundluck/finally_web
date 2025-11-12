package edu.ntu.maintenance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.ntu.maintenance.entity.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CreateRequestDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String dorm,
        String room,
        String category,
        @NotNull PriorityLevel priority,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX") OffsetDateTime preferredEntryTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX") OffsetDateTime completionTarget,
        String assetTag,
        String photoUrl
) {}
