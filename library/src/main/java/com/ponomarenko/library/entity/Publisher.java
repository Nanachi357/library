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
@Table(name = "publishers")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "publisher_id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "publishers")
    private Set<Book> books = new HashSet<Book>();

    public Publisher(){}

    public Publisher(String name){
        this.name = name;
    }


    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Publisher))
            return false;
        Publisher publisher = (Publisher) o;
        return Objects.equals(this.id, publisher.id) && Objects.equals(this.name, publisher.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString(){
        return "Publisher{" + "id=" + this.id
                + ", name='" + this.name + "\'" + '}';
    }
}
