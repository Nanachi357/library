package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.PatchReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.dto.UpdateReaderRequest;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.mapper.ReaderMapper;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final ReaderMapper readerMapper;
    private final BookMapper bookMapper;

    public List<ReaderResponse> getAll() {
        return readerRepository.findAll().stream()
                .map(readerMapper::toResponse)
                .toList();
    }

    public Optional<ReaderResponse> getById(Long id) {
        return readerRepository.findById(id)
                .map(readerMapper::toResponse);
    }

    public ReaderResponse create(CreateReaderRequest request) {
        var reader = readerMapper.toEntity(request);
        Reader savedReader = readerRepository.save(reader);

        return readerMapper.toResponse(savedReader);
    }

    @Transactional
    public Optional<ReaderResponse> update(Long id, UpdateReaderRequest request) {
        return readerRepository.findById(id)
                .map(reader -> {
                    readerMapper.updateEntity(request, reader);
                    return readerMapper.toResponse(reader);
                });
    }

    @Transactional
    public Optional<ReaderResponse> patch(Long id, PatchReaderRequest request) {
        return readerRepository.findById(id)
                .map(reader -> {
                    readerMapper.patchEntity(request, reader);
                    return readerMapper.toResponse(reader);
                });
    }

    @Transactional(readOnly = true)
    public Optional<List<BookResponse>> getBooksByReader(Long readerId) {
        if (!readerRepository.existsById(readerId)) {
            return Optional.empty();
        }

        List<BookResponse> books = bookRepository.findAllByReaderId(readerId).stream()
                .map(bookMapper::toResponse)
                .toList();

        return Optional.of(books);
    }

    public boolean deleteById(Long id) {
        if (!readerRepository.existsById(id)) {
            return false;
        }

        readerRepository.deleteById(id);
        return true;
    }

}
