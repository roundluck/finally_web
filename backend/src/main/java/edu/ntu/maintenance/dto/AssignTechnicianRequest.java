package edu.ntu.maintenance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record AssignTechnicianRequest(
        @NotNull Long technicianId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX") OffsetDateTime completionTarget,
        String note
) {}
