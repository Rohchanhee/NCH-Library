package com.nch.bookmanager.service;

import com.nch.bookmanager.dto.UserRegisterRequestDto;
import com.nch.bookmanager.entity.User;
import com.nch.bookmanager.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 로직
     * @param requestDto (회원가입 시 입력받은 DTO)
     * @return User (저장된 User 엔티티)
     */
    public User registerNewUser(UserRegisterRequestDto requestDto) {

        String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .password(encryptedPassword)
                .role("ROLE_USER")
                .build();


        return userRepository.save(user);
    }





    /**
     * 특정 유저를 관리자(ROLE_ADMIN)로 승격시키는 테스트용 메서드
     */
    public void promoteToAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + username));

        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
    }
}