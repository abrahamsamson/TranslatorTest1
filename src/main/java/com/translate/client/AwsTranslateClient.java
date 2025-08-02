package com.translate.client;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.*;

public class AwsTranslateClient {
    private final TranslateClient translateClient;

    public AwsTranslateClient() {
        this.translateClient = TranslateClient.builder()
                .region(Region.US_EAST_1) // or use your configured region
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    public String translate(String text, String targetLang) {
        TranslateTextRequest request = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode("en")
                .targetLanguageCode(targetLang)
                .build();

        TranslateTextResponse response = translateClient.translateText(request);
        return response.translatedText();
    }
}
