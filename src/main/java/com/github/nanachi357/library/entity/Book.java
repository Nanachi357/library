package com.github.nanachi357.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "author_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "author_id"})
    )
    private Set<Author> authors = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_reader",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "reader_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "reader_id"})
    )
    private Set<Reader> readers = new HashSet<>();

    protected Book() {
    }

    public Book(String title) {
        this.title = title;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void addReader(Reader reader) {
        readers.add(reader);
        reader.getBooks().add(this);
    }

    public void removeReader(Reader reader) {
        readers.remove(reader);
        reader.getBooks().remove(this);
    }

    public void removeAllAuthors() {
        for (Author author : new HashSet<>(authors)) {
            removeAuthor(author);
        }
    }

    public void removeAllReaders() {
        for (Reader reader : new HashSet<>(readers)) {
            removeReader(reader);
        }
    }

}
