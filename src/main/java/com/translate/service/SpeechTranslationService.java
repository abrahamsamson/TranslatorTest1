package com.translate.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class SpeechTranslationService {

    private final TranscribeClient transcribeClient;
    private final PollyClient pollyClient;

    public SpeechTranslationService() {
        this.transcribeClient = TranscribeClient.create();
        this.pollyClient = PollyClient.create();
    }

    public String transcribeAudio(String s3Uri, String jobName, String languageCode) {
        StartTranscriptionJobRequest startJobRequest = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .languageCode(languageCode)
                .media(Media.builder().mediaFileUri(s3Uri).build())
                .build();

        transcribeClient.startTranscriptionJob(startJobRequest);

        while (true) {
            GetTranscriptionJobResponse jobResponse = transcribeClient.getTranscriptionJob(
                    GetTranscriptionJobRequest.builder()
                            .transcriptionJobName(jobName)
                            .build());

            if (jobResponse.transcriptionJob().transcriptionJobStatus() == TranscriptionJobStatus.COMPLETED) {
                return jobResponse.transcriptionJob().transcript().transcriptFileUri();
            } else if (jobResponse.transcriptionJob().transcriptionJobStatus() == TranscriptionJobStatus.FAILED) {
                throw new RuntimeException("Transcription failed");
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void synthesizeSpeech(String text, String voice, String outputFile) {
        SynthesizeSpeechRequest speechRequest = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId(voice)
                .outputFormat(OutputFormat.MP3)
                .build();

        try (ResponseInputStream<SynthesizeSpeechResponse> synthRes = pollyClient.synthesizeSpeech(speechRequest);
                FileOutputStream outputStream = new FileOutputStream(new File(outputFile))) {

            byte[] buffer = new byte[2 * 1024];
            int readBytes;
            while ((readBytes = synthRes.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
