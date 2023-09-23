package com.ponomarenko.library.service.impl;

import com.ponomarenko.library.entity.Book;
import com.ponomarenko.library.exception.NotFoundException;
import com.ponomarenko.library.repository.BookRepository;
import com.ponomarenko.library.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final String exceptionName = "book";

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword != null) {
            return bookRepository.search(keyword);
        }
        return bookRepository.findAll();
    }
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBooksPageable(String keyword, Pageable pageable) {
        if (keyword != null) {
            return bookRepository.searchPageable(keyword, pageable);
        }
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public List<Book> searchBooksSortAndPageable(String keyword, Sort sort, Pageable pageable){
        if (keyword != null){
            return bookRepository.searchPageableSort(keyword, sort, pageable);
        }
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));
    }


    @Override
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        final Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));

        bookRepository.deleteById(book.getId());
    }
}
