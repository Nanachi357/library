package com.github.nanachi357.library.service;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.PageResponse;
import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.exception.ResourceNotFoundException;
import com.github.nanachi357.library.mapper.BookMapper;
import com.github.nanachi357.library.repository.AuthorRepository;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookRelationService bookRelationService;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAll_shouldReturnPagedBookResponsesAndPreserveMetadata() {
        Pageable pageable = PageRequest.of(1, 2);
        Book firstBook = new Book("Clean Code");
        Book secondBook = new Book("Refactoring");
        BookResponse firstResponse = new BookResponse(1L, "Clean Code");
        BookResponse secondResponse = new BookResponse(2L, "Refactoring");
        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(firstBook, secondBook), pageable, 5));
        when(bookMapper.toResponse(firstBook)).thenReturn(firstResponse);
        when(bookMapper.toResponse(secondBook)).thenReturn(secondResponse);

        PageResponse<BookResponse> response = bookService.getAll(pageable);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toResponse(firstBook);
        verify(bookMapper).toResponse(secondBook);
    }

    @Test
    void addAuthorToBook_existingBookAndAuthor_shouldLinkRelation() {
        Long bookId = 1L;
        Long authorId = 2L;
        Book book = new Book("Good Omens");
        Author author = author();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        bookService.addAuthorToBook(bookId, authorId);

        verify(bookRepository).findById(bookId);
        verify(authorRepository).findById(authorId);
        verify(bookRelationService).addAuthorToBook(book, author);
    }

    @Test
    void addAuthorToBook_missingBook_shouldThrowResourceNotFoundException() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.addAuthorToBook(bookId, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found with id: " + bookId);

        verify(authorRepository, never()).findById(2L);
        verifyNoInteractions(bookRelationService);
    }

    @Test
    void addAuthorToBook_missingAuthor_shouldThrowResourceNotFoundException() {
        Long bookId = 1L;
        Long authorId = 2L;
        Book book = new Book("Good Omens");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.addAuthorToBook(bookId, authorId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author not found with id: " + authorId);

        verifyNoInteractions(bookRelationService);
    }

    @Test
    void addReaderToBook_existingBookAndReader_shouldLinkRelation() {
        Long bookId = 1L;
        Long readerId = 2L;
        Book book = new Book("Good Omens");
        Reader reader = new Reader("Iryna Bondar");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));

        bookService.addReaderToBook(bookId, readerId);

        verify(bookRepository).findById(bookId);
        verify(readerRepository).findById(readerId);
        verify(bookRelationService).addReaderToBook(book, reader);
    }

    @Test
    void addReaderToBook_missingBook_shouldThrowResourceNotFoundException() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.addReaderToBook(bookId, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found with id: " + bookId);

        verify(readerRepository, never()).findById(2L);
        verifyNoInteractions(bookRelationService);
    }

    @Test
    void addReaderToBook_missingReader_shouldThrowResourceNotFoundException() {
        Long bookId = 1L;
        Long readerId = 2L;
        Book book = new Book("Good Omens");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(readerRepository.findById(readerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.addReaderToBook(bookId, readerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Reader not found with id: " + readerId);

        verifyNoInteractions(bookRelationService);
    }

    @Test
    void deleteById_existingBook_shouldUnlinkRelationsBeforeDeletingBook() {
        Long bookId = 1L;
        Book book = new Book("Good Omens");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        boolean deleted = bookService.deleteById(bookId);

        assertThat(deleted).isTrue();
        InOrder ordered = inOrder(bookRelationService, bookRepository);
        ordered.verify(bookRelationService).removeAllAuthorsFromBook(book);
        ordered.verify(bookRelationService).removeAllReadersFromBook(book);
        ordered.verify(bookRepository).delete(book);
    }

    @Test
    void deleteById_missingBook_shouldReturnFalseAndNotDeleteBook() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        boolean deleted = bookService.deleteById(bookId);

        assertThat(deleted).isFalse();
        verifyNoInteractions(bookRelationService);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    private static Author author() {
        return new Author("Neil Gaiman", LocalDate.of(1960, 11, 10), "United Kingdom");
    }
}
