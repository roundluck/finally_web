package edu.ntu.maintenance.dto;

import edu.ntu.maintenance.entity.RequestStatus;

import java.time.OffsetDateTime;

public record TimelineEntryResponse(
        Long id,
        RequestStatus status,
        String note,
        OffsetDateTime createdAt,
        UserResponse createdBy
) {}
