package com.sctech.emailrequestreceiver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "emailTemplates")
public class EmailTemplates {
    @Id
    private String id;
    private String name;
    private String content;
    private String contentType;
    private Boolean attachmentRequired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer templateId;
}
