package com.nch.bookmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_record")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RentalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 엔티티와의 다대일 관계 설정 (User가 RentalRecord를 여러 개 가질 수 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 컬럼 지정
    private User user;

    // Book 엔티티와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 대여 날짜 (생성 시 자동으로 설정)
    @Column(nullable = false)
    private LocalDateTime loanDate;

    // 반납 예정 날짜
    @Column(nullable = false)
    private LocalDateTime dueDate;

    // 실제 반납 날짜 (반납 전에는 null 허용)
    private LocalDateTime returnDate;
}