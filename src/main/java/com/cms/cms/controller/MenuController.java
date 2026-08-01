package com.cms.cms.controller;

import com.cms.cms.entity.Menu;
import com.cms.cms.entity.MenuItem;
import com.cms.cms.repository.MenuRepository;
import com.cms.cms.repository.MenuItemRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private AuditLogService auditLogService;

    // ========== Menu CRUD ==========

    @GetMapping
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
    }

    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        Menu saved = menuRepository.save(menu);
        auditLogService.log("CREATE", "Menu", saved.getId(), "Created menu: " + saved.getName());
        return saved;
    }

    @PutMapping("/{id}")
    public Menu updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        Menu existing = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        existing.setName(menu.getName());
        existing.setLocation(menu.getLocation());
        Menu updated = menuRepository.save(existing);
        auditLogService.log("UPDATE", "Menu", id, "Updated menu: " + updated.getName());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        auditLogService.log("DELETE", "Menu", id, "Deleted menu: " + menu.getName());
        menuRepository.deleteById(id);
    }

    // ========== Menu Items CRUD ==========

    @GetMapping("/{menuId}/items")
    public List<MenuItem> getMenuItems(@PathVariable Long menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

    @PostMapping("/{menuId}/items")
    public MenuItem addMenuItem(@PathVariable Long menuId, @RequestBody MenuItem item) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        item.setMenu(menu);

        // Handle parent if provided
        if (item.getParent() != null && item.getParent().getId() != null) {
            MenuItem parent = menuItemRepository.findById(item.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent item not found"));
            item.setParent(parent);
        }

        MenuItem saved = menuItemRepository.save(item);
        auditLogService.log("CREATE", "MenuItem", saved.getId(), "Added item: " + saved.getLabel() + " to menu: " + menu.getName());
        return saved;
    }

    @PutMapping("/items/{itemId}")
    public MenuItem updateMenuItem(@PathVariable Long itemId, @RequestBody MenuItem item) {
        MenuItem existing = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        existing.setLabel(item.getLabel());
        existing.setUrl(item.getUrl());
        existing.setOrderIndex(item.getOrderIndex());

        if (item.getParent() != null && item.getParent().getId() != null) {
            MenuItem parent = menuItemRepository.findById(item.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent item not found"));
            existing.setParent(parent);
        } else {
            existing.setParent(null);
        }

        MenuItem updated = menuItemRepository.save(existing);
        auditLogService.log("UPDATE", "MenuItem", itemId, "Updated item: " + updated.getLabel());
        return updated;
    }

    @DeleteMapping("/items/{itemId}")
    public void deleteMenuItem(@PathVariable Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        auditLogService.log("DELETE", "MenuItem", itemId, "Deleted item: " + item.getLabel());
        menuItemRepository.deleteById(itemId);
    }
}