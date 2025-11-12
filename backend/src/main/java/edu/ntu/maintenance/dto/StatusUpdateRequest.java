package edu.ntu.maintenance.dto;

import edu.ntu.maintenance.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull RequestStatus status,
        String note
) {}
