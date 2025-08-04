package com.translate.service;

import com.translate.client.AwsTranslateClient;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final AwsTranslateClient awsClient;

    public TranslationService(AwsTranslateClient awsClient) {
        this.awsClient = awsClient;
    }

    public String translate(String text, String sourceLang, String targetLang) {
        return awsClient.translate(text, sourceLang, targetLang);
    }
}
