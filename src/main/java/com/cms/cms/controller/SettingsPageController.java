package com.cms.cms.controller;

import com.cms.cms.entity.Setting;
import com.cms.cms.repository.SettingRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingsPageController {

    @Autowired
    private SettingRepository settingRepository;

    @GetMapping
    public String settingsPage(Model model) {
        List<Setting> settings = settingRepository.findAll();
        model.addAttribute("settings", settings);
        return "settings";
    }
}