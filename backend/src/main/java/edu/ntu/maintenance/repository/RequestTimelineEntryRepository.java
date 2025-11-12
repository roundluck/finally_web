package edu.ntu.maintenance.repository;

import edu.ntu.maintenance.entity.RequestTimelineEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestTimelineEntryRepository extends JpaRepository<RequestTimelineEntry, Long> {
    List<RequestTimelineEntry> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
