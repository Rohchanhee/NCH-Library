package com.nch.bookmanager.config;

import com.nch.bookmanager.service.CustomUserDetailsService;
import com.nch.bookmanager.utils.JwtAuthenticationFilter;
import com.nch.bookmanager.utils.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity /** Spring Security 활성화를 위한 어노테이션 */
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
                          CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     *
     * 스프링 시큐리티에서 제공하는 비밀번호 암호화(해싱) 객체
     * 사용자 비밀번호를 DB에 저장하기 전에 BCrypt 방식으로 암호화해줌
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     *
     * AuthenticationManager: Username + Password 검증해서 인증 성공/실패를 결정하는 객체
     *
     *
     * AuthenticationManager의 내부 구조
     *     ↓ authenticate()
     * ProviderManager
     *     ↓ 사용 가능한 AuthenticationProvider들 중 DaoAuthenticationProvider 선택
     * DaoAuthenticationProvider
     *     ↓ loadUserByUsername() 호출
     * UserDetailsService (네가 만든 CustomUserDetailsService)
     *     ↓ DB에서 유저 찾아서 UserDetails 반환
     * DaoAuthenticationProvider
     *     ↓ 비밀번호 매칭(BCrypt)
     * 결과 Authentication 객체 생성
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        /** AuthenticationManagerBuilder 객체 찾기*/
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        /**  AuthenticationManager = userDetailsService 인증 필터 + passwordEncoder 인증 필터 */
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        return builder.build();
    }

    // 전역 CORS 설정 Bean 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // React 앱 주소 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // 모든 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 인증 헤더(토큰) 허용
        configuration.setAllowCredentials(true);
        // 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/books/random").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .requestMatchers("/api/books/search/**").permitAll()


                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")


                        .requestMatchers("/api/recommend/**").permitAll()


                        .requestMatchers(
                                "/",                // 메인 페이지
                                "/index.html",      // 리액트 진입점
                                "/static/**",       // JS, CSS 파일들
                                "/assets/**",       // 이미지 등 에셋
                                "/*.ico",           // 파비콘
                                "/*.json",          // manifest 파일 등
                                "/*.png",           // 이미지

                                // 리액트 라우터 경로들도 허용해야 새로고침했을 때 403이 안 뜹니다.
                                "/login", "/register", "/my", "/books", "/admin/**", "/guide"
                        ).permitAll()


                        .anyRequest().authenticated()
                )

                /**
                 * 스프링의 기존 인증 필터(UsernamePasswordAuthenticationFilter)가 실행되기 전에
                 * 모든 요청이 JwtAuthenticationFilter를 먼저 필터링
                 */
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                /**
                 * JWT사용에 따른 STATELESS
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}