package com.ponomarenko.library.controller;

import com.ponomarenko.library.entity.Book;
import com.ponomarenko.library.exception.NotFoundException;
import com.ponomarenko.library.repository.BookRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {
    private final String exceptionName = "book";
    private final BookRepository repository;

    BookController(BookRepository repository){
        this.repository = repository;
    }

    @GetMapping("/books")
    List<Book> all(){
        return repository.findAll();
    }

    @PostMapping("/books")
    Book newBook(@RequestBody Book newBook){
        return repository.save(newBook);
    }

    @GetMapping("/books/{id}")
    Book one(@PathVariable Long id){
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));
    }

    @PutMapping("/books/{id}")
    Book replaceBook(@RequestBody Book newBook, @PathVariable Long id){
        return repository.findById(id)
                .map(book -> {
                    book.setIsbn(newBook.getIsbn());
                    book.setName(newBook.getName());
                    book.setSerialName(newBook.getSerialName());
                    book.setDescription(newBook.getDescription());
                    return repository.save(book);
                })
                .orElseGet(() -> {
                    newBook.setId(id);
                    return repository.save(newBook);
                });
    }

    @DeleteMapping("/books/{id}")
    void deleteBook(@PathVariable Long id){
        repository.deleteById(id);
    }
}
