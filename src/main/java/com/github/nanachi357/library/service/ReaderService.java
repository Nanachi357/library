package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.dto.PatchReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.dto.UpdateReaderRequest;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.exception.ResourceNotFoundException;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.mapper.ReaderMapper;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final ReaderMapper readerMapper;
    private final BookMapper bookMapper;
    private final BookRelationService bookRelationService;

    public PageResponse<ReaderResponse> getAll(Pageable pageable) {
        var readers = readerRepository.findAll(pageable)
                .map(readerMapper::toResponse);

        return PageResponse.from(readers);
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
    public PageResponse<BookResponse> getBooksByReader(Long readerId, Pageable pageable) {
        if (!readerRepository.existsById(readerId)) {
            throw new ResourceNotFoundException("Reader not found with id: " + readerId);
        }

        var books = bookRepository.findAllByReaderId(readerId, pageable)
                .map(bookMapper::toResponse);

        return PageResponse.from(books);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<Reader> readerOptional = readerRepository.findById(id);

        if (readerOptional.isEmpty()) {
            return false;
        }

        Reader reader = readerOptional.get();

        new HashSet<>(reader.getBooks())
                .forEach(book -> bookRelationService.removeReaderFromBook(book, reader));

        readerRepository.delete(reader);
        return true;
    }

}
