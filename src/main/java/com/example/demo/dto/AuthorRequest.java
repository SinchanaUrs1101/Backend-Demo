package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthorRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 60)
    private String nationality;

    // Generate getters and setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}