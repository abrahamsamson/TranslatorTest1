package com.translate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.util.Scanner;

@SpringBootApplication
public class MainApp {

    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            try (TranslateClient translateClient = TranslateClient.create();
                    Scanner scanner = new Scanner(System.in)) {

                System.out.print("Enter text to translate: ");
                String text = scanner.nextLine();

                System.out.print("Enter source language code (e.g., en): ");
                String sourceLang = scanner.nextLine();

                System.out.print("Enter target language code (e.g., es): ");
                String targetLang = scanner.nextLine();

                TranslateTextRequest request = TranslateTextRequest.builder()
                        .text(text)
                        .sourceLanguageCode(sourceLang)
                        .targetLanguageCode(targetLang)
                        .build();

                TranslateTextResponse response = translateClient.translateText(request);
                System.out.println("Translated Text: " + response.translatedText());
            }
        };
    }
}
