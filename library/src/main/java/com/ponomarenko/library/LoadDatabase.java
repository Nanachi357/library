package com.ponomarenko.library;

import com.ponomarenko.library.entity.Author;
import com.ponomarenko.library.entity.Book;
import com.ponomarenko.library.entity.Publisher;
import com.ponomarenko.library.repository.AuthorRepository;
import com.ponomarenko.library.repository.BookRepository;
import com.ponomarenko.library.repository.PublisherRepository;
import com.ponomarenko.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

//    @Bean
//    public CommandLineRunner initialAuthorDatabase(AuthorRepository repository) {
//        return (args) -> {
//            log.info("Preloading" + repository.save(new Author("Craig Walls", "dummy description")));
//            log.info("Preloading" + repository.save(new Author("Mark Heckler", "Test description1")));
//        };
//    }

//    @Bean
//    public CommandLineRunner initialBookDatabase(BookRepository repository) {
//        return (args) -> {
//            log.info("Preloading" + repository.save(new Book("AP1287", "Spring in Action ", "CXEF12389",
//                    "If you need to learn Spring, look no further than this widely beloved and comprehensive guide! " +
//                            "Fully revised for Spring 5.3, and packed with interesting real-world examples to get your hands dirty with Spring.")));
//            log.info("Preloading" + repository.save(new Book("BP567#R", "Spring Boot: Up and Running", "KCXEF12389", "Description1")));
//        };
//    }

//    @Bean
//    public CommandLineRunner initialPublisherDatabase(PublisherRepository repository) {
//        return (args) -> {
//            log.info("Preloading" + repository.save(new Publisher("Manning Publications")));
//            log.info("Preloading" + repository.save(new Publisher("O'Reilly Media")));
//        };
//    }

    @Bean
    public CommandLineRunner initialCreate(BookService bookService) {
        return (args) -> {

            Book book = new Book("AP1287", "Spring in Action ", "CXEF12389",
                    "If you need to learn Spring, look no further than this widely beloved and comprehensive guide! " +
                            "Fully revised for Spring 5.3, and packed with interesting real-world examples to get your hands dirty with Spring.");
            Author author = new Author("Craig Walls", "dummy description");
            Publisher publisher = new Publisher("Manning Publications");

            book.addAuthors(author);
            book.addPublishers(publisher);

            bookService.createBook(book);

            Book book1 = new Book("BP567#R", "Spring Boot: Up and Running", "KCXEF12389", "Description1");
            Author author1 = new Author("Mark Heckler", "Test description1");
            Publisher publisher1 = new Publisher("O'Reilly Media");

            book1.addAuthors(author1);
            book1.addPublishers(publisher1);

            bookService.createBook(book1);

            Book book2 = new Book("Test isbn", "Test name", "Test serial name", "Test description2");
            Author author2 = new Author("Test author name", "Test description");
            Publisher publisher2 = new Publisher("Test publisher name");

            book.addAuthors(author2);
            book.addPublishers(publisher2);

            bookService.createBook(book2);

            Book book3 = new Book("Test isbn1", "Test name1", "Test serial name1", "Test description3");
            Author author3 = new Author("Test author name1", "Test description1");
            Publisher publisher3 = new Publisher("Test publisher name1");

            book1.addAuthors(author3);
            book1.addPublishers(publisher3);

            bookService.createBook(book3);

            Book book4 = new Book("Test isbn4", "Test name4", "Test serial name4", "Test description4");
            Author author4 = new Author("Test author name4", "Test description4");
            Publisher publisher4 = new Publisher("Test publisher name4");

            book2.addAuthors(author4);
            book2.addPublishers(publisher4);

            bookService.createBook(book4);

        };
    }
}
