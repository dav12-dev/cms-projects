package com.cms.cms.controller;

import com.cms.cms.entity.Event;
import com.cms.cms.repository.EventRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    // NEW: Get events for a specific month
    @GetMapping("/month")
    public List<Event> getEventsByMonth(@RequestParam int year, @RequestParam int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return eventRepository.findByEventDateBetween(start, end);
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        Event saved = eventRepository.save(event);
        auditLogService.log("CREATE", "Event", saved.getId(), "Created event: " + saved.getTitle());
        return saved;
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());
        existing.setEventDate(event.getEventDate());
        existing.setEventTime(event.getEventTime());
        existing.setLocation(event.getLocation());
        existing.setStatus(event.getStatus());
        Event updated = eventRepository.save(existing);
        auditLogService.log("UPDATE", "Event", id, "Updated event: " + updated.getTitle());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        auditLogService.log("DELETE", "Event", id, "Deleted event: " + event.getTitle());
        eventRepository.deleteById(id);
    }
}