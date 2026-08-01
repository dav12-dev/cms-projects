package com.cms.cms.repository;

import com.cms.cms.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByStatus(String status);
}