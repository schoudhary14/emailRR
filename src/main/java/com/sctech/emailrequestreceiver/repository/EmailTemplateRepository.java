package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface EmailTemplateRepository extends MongoRepository<Template, String> {
    Template findByCompanyIdAndTemplateId(String companyId, Integer templateId);

}

