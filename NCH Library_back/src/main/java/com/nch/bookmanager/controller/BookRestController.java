package com.nch.bookmanager.controller;

import com.nch.bookmanager.entity.Book;
import com.nch.bookmanager.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookRestController {

    private final BookService bookService;

    @Autowired
    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    // 전체 조회
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    // 개별 조회
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.findBookById(id);
    }

    // 등록
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    // 수정
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    //통합 검색
    @GetMapping("/search/integrated")
    public List<Book> integratedSearch(@RequestParam String keyword) {
        return bookService.integratedSearch(keyword);
    }

    // 상세 검색
    @PostMapping("/search/detailed")
    public List<Book> detailedSearch(@RequestBody Book searchedBook) {
        return bookService.detailedSearch(searchedBook);
    }

    // 랜덤 책 조회
    @GetMapping("/random")
    public Book getRandomBook() {
        return bookService.getRandomBook();
    }
}
