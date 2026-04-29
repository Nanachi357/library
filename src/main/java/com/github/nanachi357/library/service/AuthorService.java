package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.AuthorResponse;
import com.github.nanachi357.library.dto.CreateAuthorRequest;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorResponse> getAll() {
        return authorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<AuthorResponse> getById(Long id) {
        return authorRepository.findById(id)
                .map(this::toResponse);
    }

    public AuthorResponse create(CreateAuthorRequest request) {
        Author author = new Author(request.name(), request.birthDate(), request.country());
        Author savedAuthor = authorRepository.save(author);

        return toResponse(savedAuthor);
    }

    public boolean deleteById(Long id) {
        if (!authorRepository.existsById(id)) {
            return false;
        }

        authorRepository.deleteById(id);
        return true;
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBirthDate(),
                author.getCountry()
        );
    }

}
