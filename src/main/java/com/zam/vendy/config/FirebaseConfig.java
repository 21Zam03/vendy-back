package com.zam.vendy.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializa Firebase (para subir imágenes de productos a Firebase Storage) usando
 * las credenciales de la cuenta de servicio guardadas en resources/.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final ResourceLoader resourceLoader;

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @Bean
    public GoogleCredentials firebaseCredentials() throws IOException {
        Resource resource = resourceLoader.getResource(credentialsPath);
        try (InputStream in = resource.getInputStream()) {
            return GoogleCredentials.fromStream(in)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        }
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials credentials) {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setStorageBucket(storageBucket)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Storage firebaseStorage(GoogleCredentials credentials) {
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logStorageBucket() {
        log.info("Firebase Storage listo. Bucket: {}", storageBucket);
    }
}
