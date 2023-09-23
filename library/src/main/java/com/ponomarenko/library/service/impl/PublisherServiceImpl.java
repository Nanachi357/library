package com.ponomarenko.library.service.impl;

import com.ponomarenko.library.entity.Publisher;
import com.ponomarenko.library.exception.NotFoundException;
import com.ponomarenko.library.repository.PublisherRepository;
import com.ponomarenko.library.service.PublisherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {
    private final PublisherRepository publisherRepository;
    private final String exceptionName = "publisher";


    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Publisher> findAllPublishers() {
        return publisherRepository.findAll();
    }

    @Override
    public Publisher findPublisherById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));
    }

    @Override
    public void createPublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Override
    public void updatePublisher(Publisher publisher) {
        publisherRepository.save(publisher);
    }

    @Override
    public void deletePublisher(Long id) {
        final Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionName, id));

        publisherRepository.deleteById(publisher.getId());
    }

}
