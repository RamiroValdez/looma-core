package com.amool.hexagonal.domain.model;

public class Tag {
    private Long id;
    private String name;

    public Tag() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
