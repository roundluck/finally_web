package edu.ntu.maintenance.service;

import edu.ntu.maintenance.dto.*;
import edu.ntu.maintenance.entity.RequestStatus;

import java.util.List;

public interface MaintenanceRequestService {
    MaintenanceRequestResponse submitRequest(CreateRequestDto dto, String username);

    List<MaintenanceRequestResponse> listRequests(String username, RequestStatus status, String dorm, boolean mine);

    MaintenanceRequestResponse assignTechnician(Long requestId, AssignTechnicianRequest dto, String managerUsername);

    MaintenanceRequestResponse updateStatus(Long requestId, StatusUpdateRequest dto, String username);

    MaintenanceRequestResponse getRequest(Long id);
}
