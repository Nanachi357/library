package com.github.nanachi357.library.controller;

import com.github.nanachi357.library.entity.Author;
import com.github.nanachi357.library.entity.Book;
import com.github.nanachi357.library.entity.Reader;
import com.github.nanachi357.library.repository.AuthorRepository;
import com.github.nanachi357.library.repository.BookRepository;
import com.github.nanachi357.library.repository.ReaderRepository;
import com.github.nanachi357.library.service.BookRelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BookEndpointIntegrationTest {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16-alpine");

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(POSTGRES_IMAGE)
            .withDatabaseName("library_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRelationService bookRelationService;

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        readerRepository.deleteAll();
    }

    @Test
    void getBooks_returnsPagedResponseSortedByTitleAsc() throws Exception {
        bookRepository.save(new Book("Refactoring"));
        bookRepository.save(new Book("Clean Code"));
        bookRepository.save(new Book("Domain-Driven Design"));

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[1].title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void getBooks_ignoresSortQueryParameter() throws Exception {
        bookRepository.save(new Book("Refactoring"));
        bookRepository.save(new Book("Clean Code"));
        bookRepository.save(new Book("Domain-Driven Design"));

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[1].title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.content[2].title").value("Refactoring"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getBooksByAuthor_returnsOnlyLinkedBooksPagedAndSorted() throws Exception {
        Author targetAuthor = authorRepository.save(author("Target Author"));
        Author unrelatedAuthor = authorRepository.save(author("Unrelated Author"));
        Book cleanCode = bookRepository.save(new Book("Clean Code"));
        Book refactoring = bookRepository.save(new Book("Refactoring"));
        Book domainDrivenDesign = bookRepository.save(new Book("Domain-Driven Design"));
        Book unrelatedBook = bookRepository.save(new Book("Algorithms"));

        bookRelationService.addAuthorToBook(cleanCode, targetAuthor);
        bookRelationService.addAuthorToBook(refactoring, targetAuthor);
        bookRelationService.addAuthorToBook(domainDrivenDesign, targetAuthor);
        bookRelationService.addAuthorToBook(unrelatedBook, unrelatedAuthor);
        bookRepository.save(cleanCode);
        bookRepository.save(refactoring);
        bookRepository.save(domainDrivenDesign);
        bookRepository.save(unrelatedBook);

        mockMvc.perform(get("/authors/{authorId}/books", targetAuthor.getId())
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[1].title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.content[*].title").value(contains("Clean Code", "Domain-Driven Design")))
                .andExpect(jsonPath("$.content[*].title").value(not(hasItem("Algorithms"))))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void getBooksByReader_returnsOnlyLinkedBooksPagedAndSorted() throws Exception {
        Reader targetReader = readerRepository.save(new Reader("Target Reader"));
        Reader unrelatedReader = readerRepository.save(new Reader("Unrelated Reader"));
        Book cleanCode = bookRepository.save(new Book("Clean Code"));
        Book refactoring = bookRepository.save(new Book("Refactoring"));
        Book domainDrivenDesign = bookRepository.save(new Book("Domain-Driven Design"));
        Book unrelatedBook = bookRepository.save(new Book("Algorithms"));

        bookRelationService.addReaderToBook(cleanCode, targetReader);
        bookRelationService.addReaderToBook(refactoring, targetReader);
        bookRelationService.addReaderToBook(domainDrivenDesign, targetReader);
        bookRelationService.addReaderToBook(unrelatedBook, unrelatedReader);
        bookRepository.save(cleanCode);
        bookRepository.save(refactoring);
        bookRepository.save(domainDrivenDesign);
        bookRepository.save(unrelatedBook);

        mockMvc.perform(get("/readers/{readerId}/books", targetReader.getId())
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[1].title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.content[*].title").value(contains("Clean Code", "Domain-Driven Design")))
                .andExpect(jsonPath("$.content[*].title").value(not(hasItem("Algorithms"))))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void getBooksByMissingAuthor_returnsNotFound() throws Exception {
        mockMvc.perform(get("/authors/{authorId}/books", 999999L)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void getBooksByMissingReader_returnsNotFound() throws Exception {
        mockMvc.perform(get("/readers/{readerId}/books", 999999L)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    private static Author author(String name) {
        return new Author(name, LocalDate.of(1970, 1, 1), "Ukraine");
    }
}
