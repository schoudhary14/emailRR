package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.exceptions.NotExistsException;
import com.sctech.emailrequestreceiver.model.Company;
import com.sctech.emailrequestreceiver.repository.CompanyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private static final Logger logger = LogManager.getLogger(CompanyService.class);
    @Autowired
    private CompanyRepository companyRepository;


    public Company getDetail(String id){
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            throw new NotExistsException();
        }
        return company.get();
    }

    public Long getCredits(String id){
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            throw new NotExistsException();
        }
        return company.get().getCredits();
    }

    public Company getApiKeyDetailsByKey(String apiKey){
        return companyRepository.findByApiKeyKey(apiKey);
    }

    public String deductCredit(String companyId, Long usedCredits){
        Optional<Company> OptionalCompanyDetailEntity = companyRepository.findById(companyId);

        if (OptionalCompanyDetailEntity.isEmpty()){
            return "notFound";
        }
        Company companyDetailEntity = OptionalCompanyDetailEntity.get();
        companyDetailEntity.setCredits(companyDetailEntity.getCredits() - usedCredits);
        companyRepository.save(companyDetailEntity);
        return "updated";
    }
}
