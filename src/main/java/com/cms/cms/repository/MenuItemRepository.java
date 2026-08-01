package com.cms.cms.repository;

import com.cms.cms.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByMenuId(Long menuId);
    List<MenuItem> findByParentId(Long parentId);
}