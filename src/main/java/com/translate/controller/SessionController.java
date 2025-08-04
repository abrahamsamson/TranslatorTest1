package com.translate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class SessionController {

    @PostMapping("/create-session")
    public Map<String, String> createSession() {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return Map.of("sessionCode", code);
    }
}
