package com.nch.bookmanager.controller;
import com.nch.bookmanager.service.GeminiService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "http://localhost:3000")
public class RecommendationController {

    private final GeminiService geminiService;

    public RecommendationController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }


    @PostMapping
    public Map<String, String> getRecommendation(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");


        String geminiResponse = geminiService.getRecommendation(query);


        return Map.of("result", geminiResponse);
    }
}