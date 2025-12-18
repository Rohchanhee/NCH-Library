package com.nch.bookmanager.controller;
import com.nch.bookmanager.entity.Book;
import com.nch.bookmanager.service.GeminiService;
import org.springframework.web.bind.annotation.*;
import com.nch.bookmanager.service.RecommendationCoreService;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "http://localhost:3000")
public class RecommendationController {

    private final GeminiService geminiService;
    private final RecommendationCoreService coreService;

    public RecommendationController(GeminiService geminiService, RecommendationCoreService coreService) {
        this.geminiService = geminiService;
        this.coreService = coreService;
    }


    @PostMapping
    public Map<String, String> getRecommendation(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");


        String geminiResponse = geminiService.getRecommendation(query);


        return Map.of("result", geminiResponse);
    }



    /**
     * 코사인 유사도와 자카드 유사도를 이용해 사용자의 선호와 도서의 특징을 비교하여 유사한 도서를 추천
     * (로그인 필수)
     */
    @GetMapping("/user-based")
    public List<Book> recommendByUserBased(Authentication authentication) {
        String currentUsername = authentication.getName();
        return coreService.recommendByUserBased(currentUsername);
    }

    @GetMapping("/item-based")
    public List<Book> recommendByItemBased(Authentication authentication) {
        String currentUsername = authentication.getName();
        return coreService.recommendByItemBased(currentUsername);
    }
}