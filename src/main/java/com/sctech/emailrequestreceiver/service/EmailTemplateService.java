package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.Template;
import com.sctech.emailrequestreceiver.repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    public Template getTemplate(String companyId, Integer templateId){
        return emailTemplateRepository.findByCompanyIdAndTemplateId(companyId, templateId);
    }
}
