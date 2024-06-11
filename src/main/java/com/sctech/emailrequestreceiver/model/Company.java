package com.sctech.emailrequestreceiver.model;

import com.sctech.emailrequestreceiver.enums.CompanyType;
import com.sctech.emailrequestreceiver.enums.EntityStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private List<ApiKey> apiKeys;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
