package com.cms.cms.service;

import com.cms.cms.entity.AuditLog;
import com.cms.cms.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String action, String entityType, Long entityId, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setUsername(username);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}