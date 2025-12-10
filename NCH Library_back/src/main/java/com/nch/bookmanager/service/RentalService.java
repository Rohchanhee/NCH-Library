package com.nch.bookmanager.service;

import com.nch.bookmanager.dto.RentalRecordDto;
import com.nch.bookmanager.entity.Book;
import com.nch.bookmanager.entity.RentalRecord;
import com.nch.bookmanager.entity.User;
import com.nch.bookmanager.repository.BookRepository;
import com.nch.bookmanager.repository.RentalRecordRepository;
import com.nch.bookmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RentalService {

    private final RentalRecordRepository rentalRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRecordRepository rentalRecordRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.rentalRecordRepository = rentalRecordRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // 대출 기능
    public RentalRecordDto rentBook(Long bookId, String username) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다."));


        if (book.getBookCount() <= 0) {
            throw new IllegalStateException("남은 재고가 없어 대출할 수 없습니다.");
        }


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        book.setBookCount(book.getBookCount() - 1);

        // 대출 기록 생성 (반납 예정일은 7일 뒤로 설정 예시)
        RentalRecord rentalRecord = new RentalRecord();
        rentalRecord.setBook(book);
        rentalRecord.setUser(user);
        rentalRecord.setLoanDate(LocalDateTime.now());
        rentalRecord.setDueDate(LocalDateTime.now().plusDays(7)); // 7일 대여


        RentalRecord saved =  rentalRecordRepository.save(rentalRecord);

        return convertToDto(saved);
    }


    // 반납 기능
    public void returnBook(Long rentalId) {

        RentalRecord record = rentalRecordRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다."));


        if (record.getReturnDate() != null) {
            throw new IllegalStateException("이미 반납된 도서입니다.");
        }

        // 반납 처리 (현재 시간 입력)
        record.setReturnDate(LocalDateTime.now());


        Book book = record.getBook();
        book.setBookCount(book.getBookCount() + 1);

    }

    // 내 대출 목록 조회
    public List<RentalRecordDto> getMyRentals(String username) {
        return rentalRecordRepository.findRentalRecordsByUsername(username)
                .stream()
                .map(this::convertToDto)
                .toList();
    }


    private RentalRecordDto convertToDto(RentalRecord record) {
        return RentalRecordDto.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .username(record.getUser().getUsername())
                .loanDate(record.getLoanDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .build();
    }

}