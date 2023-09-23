package com.ponomarenko.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "author_id")
    private Long id;

    @Column(name = "author_name", length = 50, nullable = false)
    private String name;

    @Column(name = "author_description", length = 250, nullable = true, unique = false)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE , CascadeType.REMOVE}, mappedBy = "authors")
    private Set<Book> books = new HashSet<Book>();

    public Author(){}

    public Author(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Author))
            return false;
        Author author = (Author) o;
        return Objects.equals(this.id, author.id) && Objects.equals(this.name, author.name)
                && Objects.equals(this.description, author.description);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.name, this.description);
    }

    @Override
    public String toString(){
        return "Author{" + "id=" + this.id
                + ", name='" + this.name + "\'"
                + ", description='" + this.description + "\'" + '}';
    }
}
