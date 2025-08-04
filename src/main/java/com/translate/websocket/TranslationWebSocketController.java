package com.translate.websocket;

import com.translate.dto.TranslateMessage;
import com.translate.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TranslationWebSocketController {

    private final TranslationService translationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TranslationWebSocketController(TranslationService translationService,
            SimpMessagingTemplate messagingTemplate) {
        this.translationService = translationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/translate")
    public void handleTranslateMessage(@Payload TranslateMessage message) {
        String text = message.getText();
        String sourceLang = message.getSourceLang() != null ? message.getSourceLang() : "auto";
        String targetLang = message.getTargetLang();

        String translatedText = translationService.translate(text, sourceLang, targetLang);
        messagingTemplate.convertAndSend("/topic/translated", translatedText);
    }
}
