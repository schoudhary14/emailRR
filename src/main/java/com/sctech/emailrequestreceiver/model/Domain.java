package com.sctech.emailrequestreceiver.model;


import com.sctech.emailrequestreceiver.enums.DomainStatus;
import com.sctech.emailrequestreceiver.enums.DomainType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "emailDomainDetails")
public class Domain {
    @Id
    private String id;
    private String companyId;
    private String name;
    private DomainType type;
    private DomainStatus Status;
    private Boolean dkim;
    private String dkimPrivateKey;
    private String dkimPublicKey;
    private List<VerificationParam> verificationParam;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    @Data
    public static class VerificationParam{
        private String key;
        private String value;
    }

}
