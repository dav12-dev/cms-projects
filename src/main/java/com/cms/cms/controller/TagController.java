package com.cms.cms.controller;

import com.cms.cms.entity.Tag;
import com.cms.cms.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public List<Tag> getAllTags() { return tagRepository.findAll(); }

    @PostMapping
    public Tag createTag(@RequestBody Tag tag) {
        return tagRepository.save(tag);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagRepository.deleteById(id);
    }
}