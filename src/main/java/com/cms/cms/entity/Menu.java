package com.cms.cms.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location; // HEADER, FOOTER, SIDEBAR

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<MenuItem> items = new ArrayList<>();

    // Constructors
    public Menu() {}

    public Menu(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<MenuItem> getItems() { return items; }
    public void setItems(List<MenuItem> items) { this.items = items; }
}