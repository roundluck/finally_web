package edu.ntu.maintenance.dto;

import edu.ntu.maintenance.entity.PriorityLevel;
import edu.ntu.maintenance.entity.RequestStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record MaintenanceRequestResponse(
        Long id,
        String title,
        String description,
        String dorm,
        String room,
        String category,
        PriorityLevel priority,
        RequestStatus status,
        OffsetDateTime preferredEntryTime,
        OffsetDateTime completionTarget,
        String assetTag,
        String photoUrl,
        UserResponse student,
        UserResponse manager,
        UserResponse technician,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<TimelineEntryResponse> timeline
) {}
