package com.sctech.emailrequestreceiver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "emailAppConfig")
public class AppConfig {
    @Id
    private String id;
    private String key;
    private String value;
    private String isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
