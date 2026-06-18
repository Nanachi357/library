package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateBookRequest;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.dto.PatchBookRequest;
import com.github.nanachi357.library.dto.UpdateBookRequest;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.exception.ResourceNotFoundException;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.repository.AuthorRepository;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ReaderRepository readerRepository;
    private final BookMapper bookMapper;
    private final BookRelationService bookRelationService;

    public PageResponse<BookResponse> getAll(Pageable pageable) {
        var books = bookRepository.findAll(pageable)
                .map(bookMapper::toResponse);

        return PageResponse.from(books);
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

    @Transactional
    public void addAuthorToBook(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        bookRelationService.addAuthorToBook(book, author);
    }

    @Transactional
    public void addReaderToBook(Long bookId, Long readerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reader not found with id: " + readerId));

        bookRelationService.addReaderToBook(book, reader);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isEmpty()) {
            return false;
        }

        Book book = bookOptional.get();
        bookRelationService.removeAllAuthorsFromBook(book);
        bookRelationService.removeAllReadersFromBook(book);
        bookRepository.delete(book);

        return true;
    }

}
