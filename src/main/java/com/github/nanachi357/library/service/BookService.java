package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateBookRequest;
import com.github.nanachi357.library.dto.PatchBookRequest;
import com.github.nanachi357.library.dto.UpdateBookRequest;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    public Optional<BookResponse> getById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponse);
    }

    public BookResponse create(CreateBookRequest request) {
        var book = bookMapper.toEntity(request);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toResponse(savedBook);
    }

    @Transactional
    public Optional<BookResponse> update(Long id, UpdateBookRequest request) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookMapper.updateEntity(request, book);
                    return bookMapper.toResponse(book);
                });
    }

    @Transactional
    public Optional<BookResponse> patch(Long id, PatchBookRequest request) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookMapper.patchEntity(request, book);
                    return bookMapper.toResponse(book);
                });
    }

    public boolean deleteById(Long id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }

        bookRepository.deleteById(id);
        return true;
    }

}
