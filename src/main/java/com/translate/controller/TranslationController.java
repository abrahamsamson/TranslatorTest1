package com.translate.controller;

import com.translate.service.TranslationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/translate")
public class TranslationController {

    private final TranslationService translationService;

    // Inject TranslationService here — only what you use
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping
    public Map<String, String> translate(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String sourceLang = request.getOrDefault("sourceLang", "auto");
        String targetLang = request.get("targetLang");
        String translatedText = translationService.translate(text, sourceLang, targetLang);
        return Map.of("translatedText", translatedText);
    }
}
