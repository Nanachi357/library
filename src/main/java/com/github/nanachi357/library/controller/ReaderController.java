package com.github.nanachi357.library.controller;

import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.repository.ReaderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/readers")
public class ReaderController {

    private final ReaderRepository readerRepository;

    public ReaderController(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @GetMapping
    public List<Reader> getAll() {
        return readerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reader> getById(@PathVariable Long id) {
        return readerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
