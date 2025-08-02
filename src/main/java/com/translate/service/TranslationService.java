package com.translate.service;

import com.translate.client.AwsTranslateClient;

public class TranslationService {
    private final AwsTranslateClient awsClient;

    public TranslationService() {
        this.awsClient = new AwsTranslateClient();
    }

    public String translate(String text, String targetLang) {
        return awsClient.translate(text, targetLang);
    }
}
