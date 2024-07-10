package com.sctech.emailrequestreceiver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sctech.emailrequestreceiver.enums.CompanyType;
import com.sctech.emailrequestreceiver.enums.EntityStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "emailCompanyDetails")
public class Company {
    @Id
    private String id;
    private String name;
    private String email;
    private EntityStatus status;
    private CompanyType billType;
    private Long credits;
    private Integer alertLevel;
    private Boolean warmupEnabled;
    private Integer warmupLimit;
    private String warmupLimitUnit;
    private List<ApiKey> apiKeys;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Data
    public static class ApiKey {
        private String apiId;
        private String name;
        private String key;
        private EntityStatus status;
        private String[] ipAddress;
        private String createdBy;
        private LocalDateTime createdAt;
        private String updatedBy;
        private LocalDateTime updatedAt;
    }
}
