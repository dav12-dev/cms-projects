package com.cms.cms.controller;

import com.cms.cms.entity.Media;
import com.cms.cms.repository.MediaRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    @Autowired private MediaRepository mediaRepository;
    @Autowired private AuditLogService auditLogService;

    @GetMapping public List<Media> getAllMedia() { return mediaRepository.findAll(); }
    @PostMapping public Media uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        Media media = new Media();
        media.setFileName(file.getOriginalFilename());
        media.setFileType(file.getContentType());
        media.setFileSize(file.getSize());
        media.setData(file.getBytes());
        Media saved = mediaRepository.save(media);
        auditLogService.log("UPLOAD", "Media", saved.getId(), "Uploaded: " + saved.getFileName());
        return saved;
    }
    @GetMapping("/{id}") public ResponseEntity<byte[]> downloadMedia(@PathVariable Long id) {
        Media media = mediaRepository.findById(id).orElseThrow();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType(media.getFileType()));
        headers.setContentDispositionFormData("attachment", media.getFileName());
        return new ResponseEntity<>(media.getData(), headers, HttpStatus.OK);
    }
    @DeleteMapping("/{id}") public void deleteMedia(@PathVariable Long id) {
        Media media = mediaRepository.findById(id).orElseThrow();
        auditLogService.log("DELETE", "Media", id, "Deleted: " + media.getFileName());
        mediaRepository.deleteById(id);
    }
}