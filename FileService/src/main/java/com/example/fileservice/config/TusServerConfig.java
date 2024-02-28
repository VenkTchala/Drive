package com.example.fileservice.config;

import me.desair.tus.server.TusFileUploadService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TusServerConfig {

    @Bean
    public TusFileUploadService tusFileUploadService() {
        return new TusFileUploadService()
                .withDownloadFeature()
                .withUploadUri("/file/upload");
//                .withStoragePath(appProperties.getTusUploadDirectory())
//                .withUploadURI("/upload");

    }

}
