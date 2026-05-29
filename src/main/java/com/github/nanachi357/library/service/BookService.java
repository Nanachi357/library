package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateBookRequest;
import com.github.nanachi357.library.dto.PatchBookRequest;
import com.github.nanachi357.library.dto.UpdateBookRequest;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.repository.AuthorRepository;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ReaderRepository readerRepository;
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

    @Transactional
    public boolean addAuthorToBook(Long bookId, Long authorId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Optional<Author> authorOptional = authorRepository.findById(authorId);

        if (bookOptional.isEmpty() || authorOptional.isEmpty()) {
            return false;
        }

        Book book = bookOptional.get();
        Author author = authorOptional.get();

        book.addAuthor(author);

        return true;
    }

    @Transactional
    public boolean addReaderToBook(Long bookId, Long readerId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Optional<Reader> readerOptional = readerRepository.findById(readerId);

        if (bookOptional.isEmpty() || readerOptional.isEmpty()) {
            return false;
        }

        Book book = bookOptional.get();
        Reader reader = readerOptional.get();

        book.addReader(reader);

        return true;
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isEmpty()) {
            return false;
        }

        Book book = bookOptional.get();
        book.removeAllAuthors();
        book.removeAllReaders();
        bookRepository.delete(book);

        return true;
    }

}
