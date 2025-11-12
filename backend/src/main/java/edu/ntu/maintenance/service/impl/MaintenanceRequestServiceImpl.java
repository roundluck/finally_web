package edu.ntu.maintenance.service.impl;

import edu.ntu.maintenance.controller.mapper.MaintenanceRequestMapper;
import edu.ntu.maintenance.dto.*;
import edu.ntu.maintenance.entity.*;
import edu.ntu.maintenance.exception.BadRequestException;
import edu.ntu.maintenance.exception.ResourceNotFoundException;
import edu.ntu.maintenance.repository.AppUserRepository;
import edu.ntu.maintenance.repository.MaintenanceRequestRepository;
import edu.ntu.maintenance.repository.RequestTimelineEntryRepository;
import edu.ntu.maintenance.service.MaintenanceRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    private final MaintenanceRequestRepository requestRepository;
    private final AppUserRepository userRepository;
    private final RequestTimelineEntryRepository timelineRepository;

    public MaintenanceRequestServiceImpl(MaintenanceRequestRepository requestRepository,
                                         AppUserRepository userRepository,
                                         RequestTimelineEntryRepository timelineRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.timelineRepository = timelineRepository;
    }

    @Override
    public MaintenanceRequestResponse submitRequest(CreateRequestDto dto, String username) {
        AppUser student = findUser(username);
        if (student.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("Only students can submit new maintenance requests");
        }
        MaintenanceRequest request = new MaintenanceRequest();
        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setDorm(dto.dorm());
        request.setRoom(dto.room());
        request.setCategory(dto.category());
        request.setPriority(dto.priority());
        request.setStatus(RequestStatus.NEW);
        request.setPreferredEntryTime(dto.preferredEntryTime());
        request.setCompletionTarget(dto.completionTarget());
        request.setAssetTag(dto.assetTag());
        request.setPhotoUrl(dto.photoUrl());
        request.setStudent(student);
        request = requestRepository.save(request);
        appendTimeline(request, student, RequestStatus.NEW, "Request created");
        return MaintenanceRequestMapper.toResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequestResponse> listRequests(String username, RequestStatus status, String dorm, boolean mine) {
        AppUser requester = findUser(username);
        List<MaintenanceRequest> requests;
        if (requester.getRole() == UserRole.STUDENT) {
            requests = requestRepository.findByStudentId(requester.getId());
        } else if (requester.getRole() == UserRole.TECHNICIAN) {
            requests = requestRepository.findByTechnicianId(requester.getId());
        } else {
            requests = requestRepository.findAll();
            if (mine) {
                requests = requests.stream()
                        .filter(req -> req.getManager() != null && req.getManager().getId().equals(requester.getId()))
                        .toList();
            }
        }
        if (status != null) {
            requests = requests.stream()
                    .filter(req -> req.getStatus() == status)
                    .toList();
        }
        if (dorm != null && !dorm.isBlank()) {
            String normalized = dorm.trim().toLowerCase();
            requests = requests.stream()
                    .filter(req -> req.getDorm() != null && req.getDorm().toLowerCase().contains(normalized))
                    .toList();
        }
        return requests.stream()
                .map(MaintenanceRequestMapper::toResponse)
                .toList();
    }

    @Override
    public MaintenanceRequestResponse assignTechnician(Long requestId, AssignTechnicianRequest dto, String managerUsername) {
        AppUser manager = findUser(managerUsername);
        if (manager.getRole() != UserRole.MANAGER) {
            throw new BadRequestException("Only managers can assign technicians");
        }
        MaintenanceRequest request = findRequest(requestId);
        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new BadRequestException("Cannot assign a closed request");
        }
        AppUser technician = userRepository.findById(dto.technicianId())
                .filter(user -> user.getRole() == UserRole.TECHNICIAN)
                .orElseThrow(() -> new BadRequestException("Technician not found"));
        request.setManager(manager);
        request.setTechnician(technician);
        request.setStatus(RequestStatus.ASSIGNED);
        request.setCompletionTarget(dto.completionTarget());
        MaintenanceRequest saved = requestRepository.save(request);
        appendTimeline(saved, manager, RequestStatus.ASSIGNED,
                dto.note() != null ? dto.note() : "Technician assigned" );
        return MaintenanceRequestMapper.toResponse(saved);
    }

    @Override
    public MaintenanceRequestResponse updateStatus(Long requestId, StatusUpdateRequest dto, String username) {
        AppUser actor = findUser(username);
        MaintenanceRequest request = findRequest(requestId);
        RequestStatus nextStatus = dto.status();
        validateStatusTransition(actor, request, nextStatus);
        request.setStatus(nextStatus);
        MaintenanceRequest saved = requestRepository.save(request);
        appendTimeline(saved, actor, nextStatus, dto.note());
        if (nextStatus == RequestStatus.CANCELLED && actor.getRole() == UserRole.STUDENT) {
            saved.setTechnician(null);
            saved.setManager(null);
        }
        return MaintenanceRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceRequestResponse getRequest(Long id) {
        MaintenanceRequest request = findRequest(id);
        return MaintenanceRequestMapper.toResponse(request);
    }

    private void validateStatusTransition(AppUser actor, MaintenanceRequest request, RequestStatus nextStatus) {
        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new BadRequestException("Request already closed");
        }
        if (actor.getRole() == UserRole.STUDENT) {
            if (!request.getStudent().getId().equals(actor.getId())) {
                throw new BadRequestException("Students can only act on their own requests");
            }
            if (nextStatus != RequestStatus.CANCELLED) {
                throw new BadRequestException("Students can only cancel their requests");
            }
        } else if (actor.getRole() == UserRole.TECHNICIAN) {
            if (request.getTechnician() == null || !request.getTechnician().getId().equals(actor.getId())) {
                throw new BadRequestException("Technicians can only update assigned requests");
            }
            Set<RequestStatus> allowed = EnumSet.of(RequestStatus.IN_PROGRESS, RequestStatus.COMPLETED);
            if (!allowed.contains(nextStatus)) {
                throw new BadRequestException("Technicians can only mark in-progress or completed");
            }
        } else if (actor.getRole() == UserRole.MANAGER) {
            Set<RequestStatus> allowed = EnumSet.of(RequestStatus.UNDER_REVIEW, RequestStatus.CANCELLED);
            if (!allowed.contains(nextStatus)) {
                throw new BadRequestException("Managers can only move to UNDER_REVIEW or CANCELLED via this endpoint");
            }
        }
    }

    private AppUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(username)));
    }

    private MaintenanceRequest findRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request %d not found".formatted(id)));
    }

    private void appendTimeline(MaintenanceRequest request, AppUser actor, RequestStatus status, String note) {
        RequestTimelineEntry entry = new RequestTimelineEntry();
        entry.setRequest(request);
        entry.setCreatedBy(actor);
        entry.setStatus(status);
        entry.setNote(note);
        timelineRepository.save(entry);
        request.getTimelineEntries().add(entry);
    }
}
