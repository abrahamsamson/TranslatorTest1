package com.translate.model;

public class TranslationRequest {
    private String originalText;
    private String sourceLang;
    private String targetLang;

    public TranslationRequest(String originalText, String sourceLang, String targetLang) {
        this.originalText = originalText;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }
}