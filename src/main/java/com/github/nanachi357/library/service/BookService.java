package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateBookRequest;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BookResponse> getById(Long id) {
        return bookRepository.findById(id)
                .map(this::toResponse);
    }

    public BookResponse create(CreateBookRequest request) {
        Book book = new Book(request.title());
        Book savedBook = bookRepository.save(book);

        return toResponse(savedBook);
    }

    public boolean deleteById(Long id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }

        bookRepository.deleteById(id);
        return true;
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle()
        );
    }

}
