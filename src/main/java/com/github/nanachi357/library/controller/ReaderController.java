package com.github.nanachi357.library.controller;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.dto.PatchReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.dto.UpdateReaderRequest;
import com.github.nanachi357.library.service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping
    public PageResponse<ReaderResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return readerService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderResponse> getById(@PathVariable Long id) {
        return readerService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{readerId}/books")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByReader(
            @PathVariable Long readerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        return ResponseEntity.ok(readerService.getBooksByReader(readerId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReaderResponse create(@Valid @RequestBody CreateReaderRequest request) {
        return readerService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReaderRequest request
    ) {
        var response = readerService.update(id, request);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.get());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReaderResponse> patch(
            @PathVariable Long id,
            @Valid @RequestBody PatchReaderRequest request
    ) {
        var response = readerService.patch(id, request);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (readerService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
