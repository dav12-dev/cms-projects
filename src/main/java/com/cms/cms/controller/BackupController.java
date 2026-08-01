package com.cms.cms.controller;

import com.cms.cms.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadBackup() {
        String sql = backupService.generateBackup();
        byte[] bytes = sql.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "backup_" + System.currentTimeMillis() + ".sql");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreBackup(@RequestParam("file") MultipartFile file) {
        try {
            String sql = new String(file.getBytes(), StandardCharsets.UTF_8);
            String result = backupService.restoreBackup(sql);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read file: " + e.getMessage());
        }
    }
}