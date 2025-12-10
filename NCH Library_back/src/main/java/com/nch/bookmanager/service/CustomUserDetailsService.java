package com.nch.bookmanager.service;

import com.nch.bookmanager.entity.User;
import com.nch.bookmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * UserDetailsService: Spring Security가 로그인 시 호출하여
     * username 기반으로 DB에서 사용자 정보를 조회하기 위한 인터페이스.
     * Security는 DB를 직접 접근할 수 없으므로 Repository를 여기서 사용한다.
     */
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * 로그인 과정에서 Spring Security가 자동으로 실행함.
     * username을 기준으로 사용자 정보를 불러와
     * Spring Security 전용 UserDetails 형태로 변환해 반환한다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        /**
         * 권한(Role)을 Spring Security가 이해할 수 있는 형태로 변환
         * 스프링 시큐리티는 단순 문자열(role) 대신
         * GrantedAuthority 타입의 리스트로 권한을 저장해야 함.
         * 예: "ROLE_USER" -> new SimpleGrantedAuthority("ROLE_USER")
         */
        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        // UserDetails 객체 생성 (Spring Security용)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // 암호화된 비밀번호
                authorities
        );
    }
}