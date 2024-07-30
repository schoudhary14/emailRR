package com.sctech.emailrequestreceiver.model;

import com.sctech.emailrequestreceiver.enums.EmailContentType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "emailTemplates")
public class Template {
    @Id
    private String id;
    private String companyId;
    private Long templateId;
    private String subject;
    private String name;
    private String content;
    private EmailContentType contentType;
    private Boolean attachmentRequired;
    private String[] tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
