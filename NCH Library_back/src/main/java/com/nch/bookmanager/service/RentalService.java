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
import com.nch.bookmanager.dto.UserPreferenceDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RentalService {

    private final RentalRecordRepository rentalRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    private static final int PREFERENCE_LIMIT = 5;


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




    // ===============================================
    //           [수정] 사용자 선호 정보 조회 (빈도수 Map 반환)
    // ===============================================

    /**
     * 사용자의 과거 대출 기록을 분석하여 선호하는 저자, 출판사, KDC 빈도수 정보를 반환
     */
    @Transactional(readOnly = true)
    public UserPreferenceDto getUserPreferences(String username) {

        // Repository에서 [특징, 빈도수] List<Object[]> 형태로 데이터를 조회
        List<Object[]> authorCountsList = rentalRecordRepository.findAuthorCountsByUsername(username);
        List<Object[]> publisherCountsList = rentalRecordRepository.findPublisherCountsByUsername(username);
        List<Object[]> kdcCountsList = rentalRecordRepository.findKDCCountsByUsername(username);

        // Map<String, Long>으로 변환
        Map<String, Long> authorCounts = convertCountsListToMap(authorCountsList);
        Map<String, Long> publisherCounts = convertCountsListToMap(publisherCountsList);
        Map<String, Long> kdcCounts = convertCountsListToMap(kdcCountsList);


        return UserPreferenceDto.builder()
                .username(username)
                .authorCounts(authorCounts)
                .publisherCounts(publisherCounts)
                .kdcCounts(kdcCounts)
                .build();
    }

    /**
     * List<Object[]> 형태의 쿼리 결과를 Map<String, Long>으로 변환하는 헬퍼 메서드
     * Object[0] = String (특징), Object[1] = Long (빈도수)
     */
    private Map<String, Long> convertCountsListToMap(List<Object[]> countsList) {
        return countsList.stream()
                // Object[]를 <String, Long>으로 매핑
                .collect(Collectors.toMap(
                        arr -> (String) arr[0], // Key: 특징 (String)
                        arr -> (Long) arr[1]    // Value: 빈도수 (Long)
                ));
    }

}