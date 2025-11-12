package edu.ntu.maintenance.repository;

import edu.ntu.maintenance.entity.MaintenanceRequest;
import edu.ntu.maintenance.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStudentId(Long studentId);

    List<MaintenanceRequest> findByTechnicianId(Long technicianId);

    List<MaintenanceRequest> findByDorm(String dorm);

    List<MaintenanceRequest> findByStatus(RequestStatus status);
}
