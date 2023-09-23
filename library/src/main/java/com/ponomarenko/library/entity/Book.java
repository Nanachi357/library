package com.ponomarenko.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Setter
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "book_id")
    private Long id;

    @Column(name = "isbn", length = 50, nullable = false)
    private String isbn;

    @Column(name = "book_name", length = 50, nullable = false)
    private String name;

    @Column(name = "serialName", length = 50, nullable = true)
    private String serialName;

    @Column(name = "description", length = 250, nullable = true)
    private String description;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinTable(name = "books_authors", joinColumns = { @JoinColumn(name = "book_id") }, inverseJoinColumns = {
            @JoinColumn(name = "author_id") })
    private Set<Author> authors = new HashSet<Author>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "books_publishers", joinColumns = { @JoinColumn(name = "book_id") }, inverseJoinColumns = {
            @JoinColumn(name = "publisher_id") })
    private Set<Publisher> publishers = new HashSet<Publisher>();
    public Book(){}

    public Book(String isbn, String name, String serialName, String description) {
        this.isbn = isbn;
        this.name = name;
        this.serialName = serialName;
        this.description = description;
    }


    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Book))
            return false;
        Book book = (Book) o;
        return Objects.equals(this.id, book.id) && Objects.equals(this.isbn, book.isbn)
                && Objects.equals(this.name, book.name)
                && Objects.equals(this.serialName, book.serialName)
                && Objects.equals(this.description, book.description);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.isbn, this.name, this.serialName, this.description);
    }

    @Override
    public String toString(){
        return "Book{" + "id=" + this.id
                + ", isbn='" + this.isbn + "\'"
                + ", name='" + this.name + "\'"
                + ", serialName='" + this.serialName + "\'"
                + ", description='" + this.description + "\'" + '}';
    }

    public void addAuthors(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
    }

    public void addPublishers(Publisher publisher) {
        this.publishers.add(publisher);
        publisher.getBooks().add(this);
    }
}
