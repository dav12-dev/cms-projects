package com.cms.cms.controller;

import com.cms.cms.entity.Setting;
import com.cms.cms.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingController {
    @Autowired private SettingRepository settingRepository;

    @GetMapping public List<Setting> getAllSettings() { return settingRepository.findAll(); }
    @PutMapping("/{key}") public Setting updateSetting(@PathVariable String key, @RequestBody String value) {
        Setting setting = settingRepository.findByKey(key).orElse(new Setting());
        setting.setKey(key);
        setting.setValue(value);
        return settingRepository.save(setting);
    }
}