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
            log.info("Firebase 초기화 시작 - file_path: {}, project_id: {}", firebaseResource.getPath(), projectId);
            log.info("Firebase 파일 존재 여부: {}", firebaseResource.exists());

            if (!firebaseResource.exists()) {
                log.error("Firebase 키 파일을 찾을 수 없습니다: {}", firebaseResource.getPath());
                throw new CustomException(ErrorCode.FIREBASE_INITIALIZATION_FAILED);
            }

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
            log.error("Firebase 초기화 중 IOException 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FIREBASE_INITIALIZATION_FAILED);
        }
    }
}
