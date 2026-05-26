package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.AuthorResponse;
import com.github.nanachi357.library.dto.CreateAuthorRequest;
import com.github.nanachi357.library.dto.PatchAuthorRequest;
import com.github.nanachi357.library.dto.UpdateAuthorRequest;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.mapper.AuthorMapper;
import com.github.nanachi357.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public List<AuthorResponse> getAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .toList();
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

    public boolean deleteById(Long id) {
        if (!authorRepository.existsById(id)) {
            return false;
        }

        authorRepository.deleteById(id);
        return true;
    }

}
