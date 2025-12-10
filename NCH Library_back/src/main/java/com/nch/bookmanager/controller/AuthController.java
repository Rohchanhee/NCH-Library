package com.nch.bookmanager.controller;

import com.nch.bookmanager.dto.UserLoginRequestDto;
import com.nch.bookmanager.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequestDto loginDto) {

        try {
            /**
             * CustomUserDetailsService.loadUserByUsername() 자동 호출됨
             * Spring Security의 AuthenticationManager가 userDetailsService 인증 필터 + passwordEncoder 인증 필터 적용
             *
             */
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            // 2. 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. JWT 토큰 생성
            String jwt = tokenProvider.generateToken(authentication);

            // 4. 클라이언트에게 토큰을 JSON 객체 형태로 반환
            return ResponseEntity.ok(Map.of(
                    "accessToken", jwt,
                    "tokenType", "Bearer"
            ));

        } catch (Exception e) {
            System.err.println("인증 실패: " + e.getMessage());
            return new ResponseEntity<>(Map.of("error", "아이디 또는 비밀번호가 일치하지 않습니다."),
                    HttpStatus.UNAUTHORIZED);
        }
    }
}