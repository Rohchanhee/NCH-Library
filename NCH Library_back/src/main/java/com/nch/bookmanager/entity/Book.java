package com.nch.bookmanager.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;


    private String pubYear;


    private String volume;

    // 'ISBN' (고유 식별자. 대량의 숫자는 Long보다 String이 더 안전합니다.)
    // 실제 도서 프로그램에서는 UNIQUE 제약조건을 추가하는 것을 권장합니다.
    @Column(unique = true, nullable = false)
    private String isbn;


    private String isbnAddCode;


    private String kdc;


    @Column(nullable = false)
    private Integer bookCount = 3;

}