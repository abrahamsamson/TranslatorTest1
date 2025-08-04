package com.translate.websocket;

import com.translate.service.TranslationService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TranslationWebSocketHandler extends TextWebSocketHandler {

    private final TranslationService translationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TranslationWebSocketHandler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, String> data = objectMapper.readValue(payload, Map.class);

        String text = data.get("text");
        String sourceLang = data.getOrDefault("sourceLang", "auto");
        String targetLang = data.get("targetLang");

        String translatedText = translationService.translate(text, sourceLang, targetLang);
        session.sendMessage(new TextMessage(translatedText));
    }
}
