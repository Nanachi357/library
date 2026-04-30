package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.mapper.ReaderMapper;
import com.github.nanachi357.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;

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

    public boolean deleteById(Long id) {
        if (!readerRepository.existsById(id)) {
            return false;
        }

        readerRepository.deleteById(id);
        return true;
    }

}
