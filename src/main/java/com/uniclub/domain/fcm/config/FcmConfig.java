package com.uniclub.domain.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FcmConfig {

    private final ClassPathResource firebaseResource;
    private final String projectId;

    public FcmConfig(@Value("${fcm.file_path}") String firebaseFilePath,
                     @Value("${fcm.project_id}") String projectId) {
        this.firebaseResource = new ClassPathResource(firebaseFilePath);
        this.projectId = projectId;
    }

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = firebaseResource.getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK 초기화 완료 - Project ID: {}", projectId);
            }

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FIREBASE_INITIALIZATION_FAILED);
        }
    }
}
