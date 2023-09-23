package com.ponomarenko.library.controller;

import com.ponomarenko.library.entity.Publisher;
import com.ponomarenko.library.exception.NotFoundException;
import com.ponomarenko.library.repository.PublisherRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PublisherController {
    private final String exceptionName = "publisher";
    private final PublisherRepository repository;

    PublisherController(PublisherRepository repository){
        this.repository = repository;
    }

    @GetMapping("/publishers")
    List<Publisher> all(){
        return repository.findAll();
    }

    @PostMapping("/publishers")
    Publisher newPublisher(@RequestBody Publisher newPublisher){
        return repository.save(newPublisher);
    }

    @GetMapping("/publishers/{id}")
    Publisher one(@PathVariable Long id){
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));
    }

    @PutMapping("/publishers/{id}")
    Publisher replacePublisher(@RequestBody Publisher newPublisher, @PathVariable Long id){
        return repository.findById(id)
                .map(publisher -> {
                    publisher.setName(newPublisher.getName());
                    return repository.save(publisher);
                })
                .orElseGet(() -> {
                    newPublisher.setId(id);
                    return repository.save(newPublisher);
                });
    }

    @DeleteMapping("/publishers/{id}")
    void deleteCategory(@PathVariable Long id){
        repository.deleteById(id);
    }

}