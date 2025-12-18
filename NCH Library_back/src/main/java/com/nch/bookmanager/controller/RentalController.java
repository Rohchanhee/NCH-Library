package com.nch.bookmanager.controller;

import com.nch.bookmanager.dto.RentalRecordDto;
import com.nch.bookmanager.entity.RentalRecord;
import com.nch.bookmanager.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.nch.bookmanager.dto.UserPreferenceDto;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "http://localhost:3000")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // 대출 요청
    @PostMapping("/rent/{bookId}")
    public ResponseEntity<?> rentBook(@PathVariable Long bookId, Authentication authentication) {
        String username = authentication.getName();

        try {
            RentalRecordDto dto = rentalService.rentBook(bookId, username);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 반납 요청 (POST /api/rentals/return/{rentalId})
    @PostMapping("/return/{rentalId}")
    public ResponseEntity<?> returnBook(@PathVariable Long rentalId) {
        try {
            rentalService.returnBook(rentalId);
            return ResponseEntity.ok(Map.of("message", "반납이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 내 대출 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<RentalRecordDto>> getMyRentals(Authentication authentication) {
        String username = authentication.getName();
        List<RentalRecordDto> rentals = rentalService.getMyRentals(username);
        return ResponseEntity.ok(rentals);
    }





    // ===============================================
    //           [추가] 사용자 선호 정보 조회 API
    // ===============================================

    /**
     * 현재 로그인한 사용자의 선호 정보(Top Authors, Publishers, KDCs)를 조회
     */
    @GetMapping("/my/preferences")
    public ResponseEntity<UserPreferenceDto> getMyPreferences(Authentication authentication) {
        String username = authentication.getName();
        UserPreferenceDto preferences = rentalService.getUserPreferences(username);
        return ResponseEntity.ok(preferences);
    }
}