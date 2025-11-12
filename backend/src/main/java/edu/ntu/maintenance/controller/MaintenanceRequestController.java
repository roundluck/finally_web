package edu.ntu.maintenance.controller;

import edu.ntu.maintenance.dto.*;
import edu.ntu.maintenance.entity.RequestStatus;
import edu.ntu.maintenance.service.MaintenanceRequestService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class MaintenanceRequestController {

    private final MaintenanceRequestService requestService;

    public MaintenanceRequestController(MaintenanceRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public MaintenanceRequestResponse submitRequest(@AuthenticationPrincipal UserDetails principal,
                                                    @Valid @RequestBody CreateRequestDto dto) {
        return requestService.submitRequest(dto, principal.getUsername());
    }

    @GetMapping
    public List<MaintenanceRequestResponse> list(@AuthenticationPrincipal UserDetails principal,
                                                 @RequestParam(required = false) RequestStatus status,
                                                 @RequestParam(required = false) String dorm,
                                                 @RequestParam(defaultValue = "false") boolean mine) {
        return requestService.listRequests(principal.getUsername(), status, dorm, mine);
    }

    @GetMapping("/{id}")
    public MaintenanceRequestResponse getOne(@PathVariable Long id) {
        return requestService.getRequest(id);
    }

    @PatchMapping("/{id}/assign")
    public MaintenanceRequestResponse assignTechnician(@AuthenticationPrincipal UserDetails principal,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody AssignTechnicianRequest dto) {
        return requestService.assignTechnician(id, dto, principal.getUsername());
    }

    @PatchMapping("/{id}/status")
    public MaintenanceRequestResponse updateStatus(@AuthenticationPrincipal UserDetails principal,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody StatusUpdateRequest dto) {
        return requestService.updateStatus(id, dto, principal.getUsername());
    }
}
