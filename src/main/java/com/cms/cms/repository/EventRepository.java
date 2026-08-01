package com.cms.cms.repository;

import com.cms.cms.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    List<Event> findByEventDateAfter(LocalDate date);

    // NEW: Find events between two dates
    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);
}