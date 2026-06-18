package com.github.nanachi357.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "readers")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "readers", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();

    protected Reader() {
    }

    public Reader(String name) {
        this.name = name;
    }

}
