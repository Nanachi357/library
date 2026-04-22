package com.github.nanachi357.library.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birthDate;

    private String country;

    protected Author() {
    }

    public Author(String name, LocalDate birthDate, String country) {
        this.name = name;
        this.birthDate = birthDate;
        this.country = country;
    }

}
