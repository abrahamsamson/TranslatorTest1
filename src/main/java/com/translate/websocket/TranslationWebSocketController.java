package com.translate.websocket;

import com.translate.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class TranslationWebSocketController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Presenter sends text via WebSocket.
     * Message body: { "sessionCode": "ABC123", "text": "Hello", "targetLang": "es" }
     */
    @MessageMapping("/translate")
    public void handleTranslation(Map<String, String> message) {
        String sessionCode = message.get("sessionCode");
        String text = message.get("text");
        String targetLang = message.get("targetLang");

        String translatedText = translationService.translate(text, targetLang);

        messagingTemplate.convertAndSend("/topic/session/" + sessionCode,
                Map.of("originalText", text, "translatedText", translatedText));
    }
}
