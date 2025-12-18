package com.nch.bookmanager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Map; // List 대신 Map 사용

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceDto {
    private String username;


    private Map<String, Long> authorCounts;
    private Map<String, Long> publisherCounts;
    private Map<String, Long> kdcCounts;
}