package com.nch.bookmanager.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDto {

    private String nickname;

    // User 엔티티의 username 필드와 매칭됩니다.
    private String username;

    // User 엔티티의 password 필드와 매칭됩니다.
    private String password;
}