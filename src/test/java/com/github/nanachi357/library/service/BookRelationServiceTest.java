package com.github.nanachi357.library.service;

import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BookRelationServiceTest {

    private final BookRelationService bookRelationService = new BookRelationService();

    @Test
    void addAuthorToBook_updatesBothSides() {
        Book book = new Book("Clean Code");
        Author author = author();

        bookRelationService.addAuthorToBook(book, author);

        assertThat(book.getAuthors()).containsExactly(author);
        assertThat(author.getBooks()).containsExactly(book);
    }

    @Test
    void removeAuthorFromBook_updatesBothSides() {
        Book book = new Book("Clean Code");
        Author author = author();
        bookRelationService.addAuthorToBook(book, author);

        bookRelationService.removeAuthorFromBook(book, author);

        assertThat(book.getAuthors()).doesNotContain(author);
        assertThat(author.getBooks()).doesNotContain(book);
    }

    @Test
    void addReaderToBook_updatesBothSides() {
        Book book = new Book("Clean Code");
        Reader reader = new Reader("Iryna Bondar");

        bookRelationService.addReaderToBook(book, reader);

        assertThat(book.getReaders()).containsExactly(reader);
        assertThat(reader.getBooks()).containsExactly(book);
    }

    @Test
    void removeReaderFromBook_updatesBothSides() {
        Book book = new Book("Clean Code");
        Reader reader = new Reader("Iryna Bondar");
        bookRelationService.addReaderToBook(book, reader);

        bookRelationService.removeReaderFromBook(book, reader);

        assertThat(book.getReaders()).doesNotContain(reader);
        assertThat(reader.getBooks()).doesNotContain(book);
    }

    @Test
    void removeAllAuthorsFromBook_removesAllLinksFromBothSides() {
        Book book = new Book("Clean Code");
        Author firstAuthor = author("First Author");
        Author secondAuthor = author("Second Author");
        bookRelationService.addAuthorToBook(book, firstAuthor);
        bookRelationService.addAuthorToBook(book, secondAuthor);

        bookRelationService.removeAllAuthorsFromBook(book);

        assertThat(book.getAuthors()).isEmpty();
        assertThat(firstAuthor.getBooks()).doesNotContain(book);
        assertThat(secondAuthor.getBooks()).doesNotContain(book);
    }

    @Test
    void removeAllReadersFromBook_removesAllLinksFromBothSides() {
        Book book = new Book("Clean Code");
        Reader firstReader = new Reader("First Reader");
        Reader secondReader = new Reader("Second Reader");
        bookRelationService.addReaderToBook(book, firstReader);
        bookRelationService.addReaderToBook(book, secondReader);

        bookRelationService.removeAllReadersFromBook(book);

        assertThat(book.getReaders()).isEmpty();
        assertThat(firstReader.getBooks()).doesNotContain(book);
        assertThat(secondReader.getBooks()).doesNotContain(book);
    }

    private static Author author() {
        return author("Robert Martin");
    }

    private static Author author(String name) {
        return new Author(name, LocalDate.of(1952, 12, 5), "United States");
    }
}
