package com.cms.cms.repository;

import com.cms.cms.entity.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormSubmissionRepository extends JpaRepository<FormSubmission, Long> {
    List<FormSubmission> findByFormId(Long formId);
}