package com.cms.cms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    private String url;

    @Column(name = "order_index")
    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private MenuItem parent;

    // Constructors
    public MenuItem() {}

    public MenuItem(String label, String url, int orderIndex) {
        this.label = label;
        this.url = url;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public MenuItem getParent() { return parent; }
    public void setParent(MenuItem parent) { this.parent = parent; }
}