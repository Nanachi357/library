package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;

    public List<ReaderResponse> getAll() {
        return readerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ReaderResponse> getById(Long id) {
        return readerRepository.findById(id)
                .map(this::toResponse);
    }

    public ReaderResponse create(CreateReaderRequest request) {
        Reader reader = new Reader(request.name());
        Reader savedReader = readerRepository.save(reader);

        return toResponse(savedReader);
    }

    public boolean deleteById(Long id) {
        if (!readerRepository.existsById(id)) {
            return false;
        }

        readerRepository.deleteById(id);
        return true;
    }

    private ReaderResponse toResponse(Reader reader) {
        return new ReaderResponse(
                reader.getId(),
                reader.getName()
        );
    }

}
