package edu.ntu.maintenance.controller.mapper;

import edu.ntu.maintenance.dto.MaintenanceRequestResponse;
import edu.ntu.maintenance.dto.TimelineEntryResponse;
import edu.ntu.maintenance.entity.MaintenanceRequest;
import edu.ntu.maintenance.entity.RequestTimelineEntry;

import java.util.Comparator;
import java.util.List;

public final class MaintenanceRequestMapper {

    private MaintenanceRequestMapper() {}

    public static MaintenanceRequestResponse toResponse(MaintenanceRequest request) {
        List<TimelineEntryResponse> timeline = request.getTimelineEntries().stream()
                .sorted(Comparator.comparing(RequestTimelineEntry::getCreatedAt))
                .map(entry -> new TimelineEntryResponse(
                        entry.getId(),
                        entry.getStatus(),
                        entry.getNote(),
                        entry.getCreatedAt(),
                        UserMapper.toResponse(entry.getCreatedBy())
                ))
                .toList();
        return new MaintenanceRequestResponse(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getDorm(),
                request.getRoom(),
                request.getCategory(),
                request.getPriority(),
                request.getStatus(),
                request.getPreferredEntryTime(),
                request.getCompletionTarget(),
                request.getAssetTag(),
                request.getPhotoUrl(),
                UserMapper.toResponse(request.getStudent()),
                UserMapper.toResponse(request.getManager()),
                UserMapper.toResponse(request.getTechnician()),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                timeline
        );
    }
}
