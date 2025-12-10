package com.nch.bookmanager.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalRecordDto {
    private Long id;            // 대출 기록 ID
    private Long bookId;        // 책 ID
    private String bookTitle;   // 책 제목
    private String username;    // 사용자 이름
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
}
