package com.github.nanachi357.library.service;

import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class BookRelationService {

    public void addAuthorToBook(Book book, Author author) {
        book.getAuthors().add(author);
        author.getBooks().add(book);
    }

    public void removeAuthorFromBook(Book book, Author author) {
        book.getAuthors().remove(author);
        author.getBooks().remove(book);
    }

    public void addReaderToBook(Book book, Reader reader) {
        book.getReaders().add(reader);
        reader.getBooks().add(book);
    }

    public void removeReaderFromBook(Book book, Reader reader) {
        book.getReaders().remove(reader);
        reader.getBooks().remove(book);
    }

    public void removeAllAuthorsFromBook(Book book) {
        for (Author author : new HashSet<>(book.getAuthors())) {
            removeAuthorFromBook(book, author);
        }
    }

    public void removeAllReadersFromBook(Book book) {
        for (Reader reader : new HashSet<>(book.getReaders())) {
            removeReaderFromBook(book, reader);
        }
    }

}
