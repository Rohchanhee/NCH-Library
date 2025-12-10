//package com.nch.bookmanager.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//import org.springframework.http.MediaType;
//import jakarta.annotation.PostConstruct; // Spring Boot 시작 시 초기화용
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class GeminiService {
//
//    private final RestClient restClient;
//    private final ResourceLoader resourceLoader;
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.url}")
//    private String apiUrl;
//
//    @Value("${book.data.path}")
//    private String bookDataPath;
//
//    // 4만 토큰짜리 JSON 데이터를 저장할 변수
//    private String bookDataJson;
//
//    public GeminiService(RestClient.Builder builder, ResourceLoader resourceLoader) {
//        this.restClient = builder.build();
//        this.resourceLoader = resourceLoader;
//    }
//
//    /**
//     * 서버 시작 시 JSON 데이터 파일을 메모리에 로드
//     */
//    @PostConstruct
//    public void init() throws IOException {
//        Resource resource = resourceLoader.getResource(bookDataPath);
//        this.bookDataJson = resource.getContentAsString(StandardCharsets.UTF_8);
//        System.out.println("✅ 도서 데이터 로드 완료! (크기: " + this.bookDataJson.length() + " bytes)");
//    }
//
//    /**
//     * 사용자 질문과 도서 데이터를 결합하여 Gemini API 호출
//     */
//    public String getRecommendation(String userQuery) {
//
//        // 1. 전체 프롬프트 구성 (4만 토큰 JSON 데이터 포함)
//        String fullPrompt = String.format(
//                "당신은 도서 추천 전문가입니다. 다음 JSON 데이터는 저희가 가진 도서 목록입니다:\n\n" +
//                        "--- 데이터 ---\n%s\n--- 데이터 끝 ---\n\n" +
//                        "위 데이터만 기반으로 사용자 질문에 대한 가장 적절한 도서를 추천해 주세요. 추천 이유도 함께 설명해야 합니다.\n" +
//                        "사용자 질문: %s",
//                this.bookDataJson, userQuery
//        );
//
//        // 2. API 요청 본문 구성
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of("parts", List.of(Map.of("text", fullPrompt)))
//                )
//        );
//
//        // 3. API 호출 및 결과 반환
//        String response = restClient.post()
//                .uri(apiUrl + "?key=" + apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(requestBody)
//                .retrieve()
//                .body(String.class);
//
//        // 실제로는 여기서 JSON 파싱을 통해 응답 텍스트만 추출해야 합니다.
//        return response;
//    }
//}


package com.nch.bookmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${book.data.path}")
    private String bookDataPath;

    private String bookDataJson;

    public GeminiService(RestClient.Builder builder, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.restClient = builder.build();
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource(bookDataPath);
        this.bookDataJson = resource.getContentAsString(StandardCharsets.UTF_8);
    }

    public String getRecommendation(String userQuery) {

        String fullPrompt = String.format(
                "당신은 도서 추천 전문가입니다. 다음 JSON 데이터는 저희가 가진 도서 목록입니다:\n\n" +
                        "--- 데이터 ---\n%s\n--- 데이터 끝 ---\n\n" +
                        "위 데이터만 기반으로 사용자 질문에 대해 가장 적절한 도서를 추천해 주세요. 추천 이유도 간락하게 한줄로.\n" +
                        "사용자 질문: %s",
                this.bookDataJson, userQuery
        );

        //API 요청 본문 구성
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", fullPrompt)))
                )
        );

        //API 호출 및 Raw JSON 수신
        String rawResponse = restClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        // JSON 파싱 및 텍스트 추출
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // 경로: candidates[0] -> content -> parts[0] -> text
            String recommendationText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            System.out.println(recommendationText);
            return recommendationText;

        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 발생: " + rawResponse);
            throw new RuntimeException("API 응답 처리 중 오류 발생: " + e.getMessage());
        }
    }
}