package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.exception.ResourceNotFoundException;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.mapper.ReaderMapper;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderMapper readerMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookRelationService bookRelationService;

    @InjectMocks
    private ReaderService readerService;

    @Test
    void getAll_shouldReturnPagedReaderResponsesAndPreserveMetadata() {
        Pageable pageable = PageRequest.of(1, 2);
        Reader firstReader = new Reader("First Reader");
        Reader secondReader = new Reader("Second Reader");
        ReaderResponse firstResponse = new ReaderResponse(1L, "First Reader");
        ReaderResponse secondResponse = new ReaderResponse(2L, "Second Reader");
        when(readerRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(firstReader, secondReader), pageable, 5));
        when(readerMapper.toResponse(firstReader)).thenReturn(firstResponse);
        when(readerMapper.toResponse(secondReader)).thenReturn(secondResponse);

        PageResponse<ReaderResponse> response = readerService.getAll(pageable);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
        verify(readerRepository).findAll(pageable);
        verify(readerMapper).toResponse(firstReader);
        verify(readerMapper).toResponse(secondReader);
    }

    @Test
    void getBooksByReader_existingReader_shouldReturnPagedBookResponses() {
        Long readerId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Book firstBook = new Book("Clean Code");
        Book secondBook = new Book("Refactoring");
        BookResponse firstResponse = new BookResponse(1L, "Clean Code");
        BookResponse secondResponse = new BookResponse(2L, "Refactoring");
        when(readerRepository.existsById(readerId)).thenReturn(true);
        when(bookRepository.findAllByReaderId(readerId, pageable))
                .thenReturn(new PageImpl<>(List.of(firstBook, secondBook), pageable, 4));
        when(bookMapper.toResponse(firstBook)).thenReturn(firstResponse);
        when(bookMapper.toResponse(secondBook)).thenReturn(secondResponse);

        PageResponse<BookResponse> response = readerService.getBooksByReader(readerId, pageable);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(4);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
        verify(readerRepository).existsById(readerId);
        verify(bookRepository).findAllByReaderId(readerId, pageable);
        verify(bookMapper).toResponse(firstBook);
        verify(bookMapper).toResponse(secondBook);
    }

    @Test
    void getBooksByReader_missingReader_shouldThrowResourceNotFoundException() {
        Long readerId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        when(readerRepository.existsById(readerId)).thenReturn(false);

        assertThatThrownBy(() -> readerService.getBooksByReader(readerId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reader not found with id: " + readerId);

        verify(bookRepository, never()).findAllByReaderId(readerId, pageable);
    }

    @Test
    void deleteById_existingReader_shouldUnlinkBooksBeforeDeletingReader() {
        Long readerId = 1L;
        Reader reader = new Reader("Iryna Bondar");
        Book firstBook = new Book("Good Omens");
        Book secondBook = new Book("Neverwhere");
        reader.getBooks().add(firstBook);
        reader.getBooks().add(secondBook);
        List<String> events = new ArrayList<>();
        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        doAnswer(invocation -> {
            events.add("unlink");
            return null;
        }).when(bookRelationService).removeReaderFromBook(any(Book.class), same(reader));
        doAnswer(invocation -> {
            events.add("delete");
            return null;
        }).when(readerRepository).delete(reader);

        boolean deleted = readerService.deleteById(readerId);

        assertThat(deleted).isTrue();
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRelationService, times(2)).removeReaderFromBook(bookCaptor.capture(), same(reader));
        assertThat(bookCaptor.getAllValues()).containsExactlyInAnyOrder(firstBook, secondBook);
        verify(readerRepository).delete(reader);
        assertThat(events).containsExactly("unlink", "unlink", "delete");
    }

    @Test
    void deleteById_missingReader_shouldReturnFalseAndNotDeleteReader() {
        Long readerId = 1L;
        when(readerRepository.findById(readerId)).thenReturn(Optional.empty());

        boolean deleted = readerService.deleteById(readerId);

        assertThat(deleted).isFalse();
        verifyNoInteractions(bookRelationService);
        verify(readerRepository, never()).delete(any(Reader.class));
    }
}
