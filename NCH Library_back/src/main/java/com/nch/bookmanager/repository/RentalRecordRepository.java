package com.nch.bookmanager.repository;

import com.nch.bookmanager.entity.Book;
import com.nch.bookmanager.entity.RentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long> {

    @Query("SELECT r FROM RentalRecord r WHERE r.user.username = :username ORDER BY r.loanDate DESC")
    List<RentalRecord> findRentalRecordsByUsername(@Param("username") String username);

    void deleteByBookId(Long bookId);

    @Query(value = """
        SELECT r.book.id 
        FROM RentalRecord r
        GROUP BY r.book.id
        ORDER BY COUNT(r.book.id) DESC
        LIMIT :limit
    """)
    List<Long> findPopularBookIds(@Param("limit") int limit);

    @Query(value = """
        SELECT r.book.id 
        FROM RentalRecord r
        WHERE r.loanDate >= :startDate
        GROUP BY r.book.id
        ORDER BY COUNT(r.book.id) DESC
        LIMIT :limit
    """)
    List<Long> findTrendingBookIds(@Param("limit") int limit, @Param("startDate") LocalDateTime startDate);

    @Query("""
        SELECT r.book.author, COUNT(r.book.author) 
        FROM RentalRecord r 
        WHERE r.user.username = :username 
        GROUP BY r.book.author 
        ORDER BY COUNT(r.book.author) DESC
    """)
    List<Object[]> findAuthorCountsByUsername(@Param("username") String username);

    @Query("""
        SELECT r.book.publisher, COUNT(r.book.publisher) 
        FROM RentalRecord r 
        WHERE r.user.username = :username 
        GROUP BY r.book.publisher 
        ORDER BY COUNT(r.book.publisher) DESC
    """)
    List<Object[]> findPublisherCountsByUsername(@Param("username") String username);

    @Query("""
        SELECT r.book.kdc, COUNT(r.book.kdc) 
        FROM RentalRecord r 
        WHERE r.user.username = :username 
        GROUP BY r.book.kdc 
        ORDER BY COUNT(r.book.kdc) DESC
    """)
    List<Object[]> findKDCCountsByUsername(@Param("username") String username);

}