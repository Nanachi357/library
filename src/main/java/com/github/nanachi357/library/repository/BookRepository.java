package com.github.nanachi357.library.repository;

import com.github.nanachi357.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            select b from Book b
            join b.authors a
            where a.id = :authorId
            """)
    Page<Book> findAllByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("""
            select b from Book b
            join b.readers r
            where r.id = :readerId
            """)
    Page<Book> findAllByReaderId(@Param("readerId") Long readerId, Pageable pageable);
}
