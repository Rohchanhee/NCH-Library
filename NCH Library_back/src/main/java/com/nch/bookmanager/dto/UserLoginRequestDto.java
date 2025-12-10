package com.nch.bookmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDto {
    private String nickname;
    private String username;
    private String password;
}