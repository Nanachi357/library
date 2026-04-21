package com.github.nanachi357.library.repository;

import com.github.nanachi357.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
