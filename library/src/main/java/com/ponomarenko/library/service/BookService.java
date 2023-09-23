package com.ponomarenko.library.service;

import com.ponomarenko.library.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BookService {

    public List<Book> findAllBooks();

    public List<Book> searchBooks(String keyword);
    public List<Book> searchBooksPageable(String keyword, Pageable pageable);

    public List<Book> searchBooksSortAndPageable(String keyword, Sort sort, Pageable pageable);

    public Book findBookById(Long id);


    public void createBook(Book book);

    public void updateBook(Book book);

    public void deleteBook(Long id);
}
