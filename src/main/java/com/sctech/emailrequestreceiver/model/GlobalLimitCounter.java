package com.sctech.emailrequestreceiver.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "emailAppLimitCounter")
public class GlobalLimitCounter {
    private String id;
    private String companyId;
    private String domain;
    private Long counter;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
}
