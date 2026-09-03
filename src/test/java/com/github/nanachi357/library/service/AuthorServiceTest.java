package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.AuthorResponse;
import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.exception.ResourceNotFoundException;
import com.github.nanachi357.library.mapper.AuthorMapper;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.repository.AuthorRepository;
import com.github.nanachi357.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookRelationService bookRelationService;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void getAll_shouldReturnPagedAuthorResponsesAndPreserveMetadata() {
        Pageable pageable = PageRequest.of(1, 2);
        Author firstAuthor = author("First Author");
        Author secondAuthor = author("Second Author");
        AuthorResponse firstResponse = response(1L, "First Author");
        AuthorResponse secondResponse = response(2L, "Second Author");
        when(authorRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(firstAuthor, secondAuthor), pageable, 5));
        when(authorMapper.toResponse(firstAuthor)).thenReturn(firstResponse);
        when(authorMapper.toResponse(secondAuthor)).thenReturn(secondResponse);

        PageResponse<AuthorResponse> response = authorService.getAll(pageable);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
        verify(authorRepository).findAll(pageable);
        verify(authorMapper).toResponse(firstAuthor);
        verify(authorMapper).toResponse(secondAuthor);
    }

    @Test
    void getBooksByAuthor_existingAuthor_shouldReturnPagedBookResponses() {
        Long authorId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Book firstBook = new Book("Clean Code");
        Book secondBook = new Book("Refactoring");
        BookResponse firstResponse = new BookResponse(1L, "Clean Code");
        BookResponse secondResponse = new BookResponse(2L, "Refactoring");
        when(authorRepository.existsById(authorId)).thenReturn(true);
        when(bookRepository.findAllByAuthorId(authorId, pageable))
                .thenReturn(new PageImpl<>(List.of(firstBook, secondBook), pageable, 4));
        when(bookMapper.toResponse(firstBook)).thenReturn(firstResponse);
        when(bookMapper.toResponse(secondBook)).thenReturn(secondResponse);

        PageResponse<BookResponse> response = authorService.getBooksByAuthor(authorId, pageable);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(4);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isFalse();
        verify(authorRepository).existsById(authorId);
        verify(bookRepository).findAllByAuthorId(authorId, pageable);
        verify(bookMapper).toResponse(firstBook);
        verify(bookMapper).toResponse(secondBook);
    }

    @Test
    void getBooksByAuthor_missingAuthor_shouldThrowResourceNotFoundException() {
        Long authorId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        when(authorRepository.existsById(authorId)).thenReturn(false);

        assertThatThrownBy(() -> authorService.getBooksByAuthor(authorId, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author not found with id: " + authorId);

        verify(bookRepository, never()).findAllByAuthorId(authorId, pageable);
    }

    @Test
    void deleteById_existingAuthor_shouldUnlinkBooksBeforeDeletingAuthor() {
        Long authorId = 1L;
        Author author = author("Neil Gaiman");
        Book firstBook = new Book("Good Omens");
        Book secondBook = new Book("Neverwhere");
        author.getBooks().add(firstBook);
        author.getBooks().add(secondBook);
        List<String> events = new ArrayList<>();
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        doAnswer(invocation -> {
            events.add("unlink");
            return null;
        }).when(bookRelationService).removeAuthorFromBook(any(Book.class), same(author));
        doAnswer(invocation -> {
            events.add("delete");
            return null;
        }).when(authorRepository).delete(author);

        boolean deleted = authorService.deleteById(authorId);

        assertThat(deleted).isTrue();
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRelationService, times(2)).removeAuthorFromBook(bookCaptor.capture(), same(author));
        assertThat(bookCaptor.getAllValues()).containsExactlyInAnyOrder(firstBook, secondBook);
        verify(authorRepository).delete(author);
        assertThat(events).containsExactly("unlink", "unlink", "delete");
    }

    @Test
    void deleteById_missingAuthor_shouldReturnFalseAndNotDeleteAuthor() {
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        boolean deleted = authorService.deleteById(authorId);

        assertThat(deleted).isFalse();
        verifyNoInteractions(bookRelationService);
        verify(authorRepository, never()).delete(any(Author.class));
    }

    private static Author author(String name) {
        return new Author(name, LocalDate.of(1960, 11, 10), "United Kingdom");
    }

    private static AuthorResponse response(Long id, String name) {
        return new AuthorResponse(id, name, LocalDate.of(1960, 11, 10), "United Kingdom");
    }
}
