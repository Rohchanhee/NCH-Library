package com.nch.bookmanager.controller;

import com.nch.bookmanager.dto.UserRegisterRequestDto;
import com.nch.bookmanager.entity.User;
import com.nch.bookmanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map; // 👈 Map 사용을 위해 필요

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequestDto requestDto) {
        try {
            User newUser = userService.registerNewUser(requestDto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("회원가입 오류: " + e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }







    // [테스트용] 관리자 승격 API
    @PostMapping("/{username}/promote")
    public ResponseEntity<?> promoteUser(@PathVariable String username) {
        try {
            userService.promoteToAdmin(username);
            return ResponseEntity.ok(Map.of("message", username + " 계정이 관리자(ADMIN)로 변경되었습니다. 다시 로그인하세요!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}