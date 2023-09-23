package com.ponomarenko.library.controller;

import com.ponomarenko.library.entity.Author;
import com.ponomarenko.library.exception.NotFoundException;
import com.ponomarenko.library.repository.AuthorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthorController {
    private final String exceptionName = "author";
    private final AuthorRepository repository;

    AuthorController(AuthorRepository repository){
        this.repository = repository;
    }

    @GetMapping("/authors")
    List<Author> all(){
        return repository.findAll();
    }

    @PostMapping("/authors")
    Author newAuthor(@RequestBody Author newAuthor){
        return repository.save(newAuthor);
    }

    @GetMapping("/authors/{id}")
    Author one(@PathVariable Long id){
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));
    }

    @PutMapping("/authors/{id}")
    Author replaceAuthor(@RequestBody Author newAuthor, @PathVariable Long id){
        return repository.findById(id)
                .map(author -> {
                    author.setName(newAuthor.getName());
                    author.setDescription(newAuthor.getDescription());
                    return repository.save(author);
                })
                .orElseGet(() -> {
                    newAuthor.setId(id);
                    return repository.save(newAuthor);
                });
    }

    @DeleteMapping("/authors/{id}")
    void deleteAuthor(@PathVariable Long id){
        repository.deleteById(id);
    }

}
