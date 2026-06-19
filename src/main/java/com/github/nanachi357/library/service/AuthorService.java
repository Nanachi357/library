package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.AuthorResponse;
import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateAuthorRequest;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.dto.PatchAuthorRequest;
import com.github.nanachi357.library.dto.UpdateAuthorRequest;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.exception.ResourceNotFoundException;
import com.github.nanachi357.library.mapper.AuthorMapper;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.repository.AuthorRepository;
import com.github.nanachi357.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;
    private final BookRelationService bookRelationService;

    public PageResponse<AuthorResponse> getAll(Pageable pageable) {
        var authors = authorRepository.findAll(pageable)
                .map(authorMapper::toResponse);

        return PageResponse.from(authors);
    }

    public Optional<AuthorResponse> getById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponse);
    }

    public AuthorResponse create(CreateAuthorRequest request) {
        var author = authorMapper.toEntity(request);
        Author savedAuthor = authorRepository.save(author);

        return authorMapper.toResponse(savedAuthor);
    }

    @Transactional
    public Optional<AuthorResponse> update(Long id, UpdateAuthorRequest request) {
        return authorRepository.findById(id)
                .map(author -> {
                    authorMapper.updateEntity(request, author);
                    return authorMapper.toResponse(author);
                });
    }

    @Transactional
    public Optional<AuthorResponse> patch(Long id, PatchAuthorRequest request) {
        return authorRepository.findById(id)
                .map(author -> {
                    authorMapper.patchEntity(request, author);
                    return authorMapper.toResponse(author);
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooksByAuthor(Long authorId, Pageable pageable) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }

        var books = bookRepository.findAllByAuthorId(authorId, pageable)
                .map(bookMapper::toResponse);

        return PageResponse.from(books);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            return false;
        }

        Author author = authorOptional.get();

        new HashSet<>(author.getBooks())
                .forEach(book -> bookRelationService.removeAuthorFromBook(book, author));

        authorRepository.delete(author);
        return true;
    }

}
