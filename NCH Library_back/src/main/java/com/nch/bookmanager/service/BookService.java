package com.nch.bookmanager.service;

import com.nch.bookmanager.entity.Book;
import com.nch.bookmanager.repository.BookRepository;
import com.nch.bookmanager.repository.RentalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final RentalRecordRepository rentalRecordRepository;

    private final int RECOMMENDATION_LIMIT = 5;

    @Autowired
    public BookService(BookRepository bookRepository, RentalRecordRepository rentalRecordRepository) {
        this.bookRepository = bookRepository;
        this.rentalRecordRepository = rentalRecordRepository;
    }

    // 전체 조회
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    // 개별 조회
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("책을 찾을 수 없습니다. id=" + id));
    }

    // 등록
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // 수정
    public Book updateBook(Long id, Book book) {
        Book existingBook = findBookById(id);
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setPubYear(book.getPubYear());
        existingBook.setVolume(book.getVolume());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setIsbnAddCode(book.getIsbnAddCode());
        existingBook.setKdc(book.getKdc());
        return bookRepository.save(existingBook);
    }

    // 삭제
    public void deleteBook(Long id) {
        rentalRecordRepository.deleteByBookId(id);

        Book book = findBookById(id);
        bookRepository.delete(book);
    }

    //통합 검색
    public List<Book> integratedSearch(String keyword) {
        return bookRepository.findIntegratedSearch(keyword);
    }

    // 상세 검색
    public List<Book> detailedSearch(Book searchedBook) {
        String title = StringUtils.hasText(searchedBook.getTitle()) ? searchedBook.getTitle() : null;
        String author = StringUtils.hasText(searchedBook.getAuthor()) ? searchedBook.getAuthor() : null;
        String publisher = StringUtils.hasText(searchedBook.getPublisher()) ? searchedBook.getPublisher() : null;
        String pubYear = StringUtils.hasText(searchedBook.getPubYear()) ? searchedBook.getPubYear() : null;

        return bookRepository.findByDetailedSearchedBook(
                title,
                author,
                publisher,
                pubYear
        );
    }

    // 랜덤 책 조회
    public Book getRandomBook() {
        List<Book> allBooks = bookRepository.findAll();
        if (allBooks.isEmpty()) {
            throw new NoSuchElementException("책이 존재하지 않습니다.");
        }
        int randomIndex = (int) (Math.random() * allBooks.size());
        return allBooks.get(randomIndex);
    }

    /**
     * 전체 기간 동안 가장 많이 대출된 도서 (인기 도서) 추천
     */
    public List<Book> getPopularBooks() {
        List<Long> bookIds = rentalRecordRepository.findPopularBookIds(RECOMMENDATION_LIMIT);

        // ID 리스트를 이용해 실제 Book 엔티티 리스트를 조회합니다.
        return bookRepository.findAllById(bookIds);
    }

    /**
     * 최근 1주일간 가장 많이 대출된 도서 (급상승 도서) 추천
     */
    public List<Book> getTrendingBooks() {
        // 1주일 전 시간 계산
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        List<Long> bookIds = rentalRecordRepository.findTrendingBookIds(RECOMMENDATION_LIMIT, oneWeekAgo);

        // ID 리스트를 이용해 실제 Book 엔티티 리스트를 조회합니다.
        return bookRepository.findAllById(bookIds);
    }

    /**
     * 최신 출판된 도서 (신간 도서) 추천
     * (pubYear 기준으로 내림차순 정렬)
     */
    public List<Book> getNewReleaseBooks() {
        // BookRepository에 신간 도서 조회 쿼리를 추가해야 합니다.
        // 임시로 전체 도서 중 최신 순 10개를 반환하도록 구현합니다.
        return bookRepository.findNewReleaseBooks(RECOMMENDATION_LIMIT);
    }

}
