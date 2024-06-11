package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.EmailTemplates;
import com.sctech.emailrequestreceiver.repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    public EmailTemplates getTemplate(Integer templateId){
        System.out.println("Template ID : " + templateId);
        return emailTemplateRepository.findByTemplateId(templateId);
    }
}
