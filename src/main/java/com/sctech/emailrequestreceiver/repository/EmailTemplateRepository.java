package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.EmailTemplates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface EmailTemplateRepository extends MongoRepository<EmailTemplates, String> {
    public EmailTemplates findByTemplateId(Integer templateId);

}

