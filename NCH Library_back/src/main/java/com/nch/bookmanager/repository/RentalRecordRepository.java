package com.nch.bookmanager.repository;

import com.nch.bookmanager.entity.RentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long> {

    // JPQL을 사용한 내 대출 목록 조회 (최신순 정렬)
    @Query("SELECT r FROM RentalRecord r WHERE r.user.username = :username ORDER BY r.loanDate DESC")
    List<RentalRecord> findRentalRecordsByUsername(@Param("username") String username);

    void deleteByBookId(Long bookId);
}