package com.github.nanachi357.library.repository;

import com.github.nanachi357.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
