package com.nch.bookmanager.repository;

import com.nch.bookmanager.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookRepository extends JpaRepository<Book, Long> {

    // 통합 검색을 위한 JPQL 쿼리
    @Query("""
        SELECT b FROM Book b
        WHERE 
            LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR 
            LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR 
            LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR 
            LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Book> findIntegratedSearch(@Param("keyword") String keyword);


    //상세 검색을 위한 JPQL 쿼리
    @Query("""
        SELECT b FROM Book b
        WHERE   
            (:title IS NULL OR :title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND       
            (:author IS NULL OR :author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
        AND 
            (:publisher IS NULL OR :publisher = '' OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%')))
        AND 
            (:pubYear IS NULL OR :pubYear = '' OR b.pubYear LIKE CONCAT('%', :pubYear, '%'))
        """)
    List<Book> findByDetailedSearchedBook(
            @Param("title") String title,
            @Param("author") String author,
            @Param("publisher") String publisher,
            @Param("pubYear") String pubYear
    );

    //랜덤 책
    @Query(value = "SELECT * FROM book ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Book findRandomBook();
}