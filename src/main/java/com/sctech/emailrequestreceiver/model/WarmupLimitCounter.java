package com.sctech.emailrequestreceiver.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "emailAppLimitCounter")
public class WarmupLimitCounter {
    private String id;
    private String companyId;
    private LocalDate date;
    private String domain;
    private Integer counter;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
}
