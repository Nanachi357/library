package com.ponomarenko.library.repository;


import com.ponomarenko.library.entity.Book;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.name LIKE %?1%" + " OR b.isbn LIKE %?1%" + " OR b.serialName LIKE %?1%")
    public List<Book> search(String keyword);

    @Query("SELECT b FROM Book b WHERE b.name LIKE %?1%" + " OR b.isbn LIKE %?1%" + " OR b.serialName LIKE %?1%")
    public List<Book> searchPageable(String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.name LIKE %?1%" + " OR b.isbn LIKE %?1%" + " OR b.serialName LIKE %?1%")
    public List<Book> searchPageableSort(String keyword, Sort sort, Pageable pageable);

    @Query("SELECT b FROM Book b")
    List<Book> findAll(Sort sort, Pageable pageable);

    @Query("SELECT t FROM Book t WHERE t.publishers=?1")
    List<Book> findByPublished(boolean isPublished);

    @Query("SELECT t FROM Book t WHERE t.name LIKE %?1%")
    List<Book> findByTitleLike(String title);
}
