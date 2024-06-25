package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.exceptions.NotExistsException;
import com.sctech.emailrequestreceiver.model.Company;
import com.sctech.emailrequestreceiver.repository.CompanyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CompanyService {
    private static final Logger logger = LogManager.getLogger(CompanyService.class);
    @Autowired
    private CompanyRepository companyRepository;


    public Company getDetail(String id){
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            logger.warn("Company with id {} not found", id);
            throw new NotExistsException("Company");
        }
        return company.get();
    }

    public Long getCredits(String id){
        Optional<Company> company = companyRepository.findById(id);
        if (company.isEmpty()) {
            logger.warn("Company with id {} not found", id);
            throw new NotExistsException("Company");
        }
        return company.get().getCredits();
    }

    public Company getApiKeyDetailsByKey(String apiKey){
        return companyRepository.findByApiKeyKey(apiKey);
    }

    public String deductCredit(String companyId, Long usedCredits){
        Optional<Company> OptionalCompanyDetailEntity = companyRepository.findById(companyId);

        if (OptionalCompanyDetailEntity.isEmpty()){
            logger.warn("Company with id {} not found", companyId);
            throw new NotExistsException("Company");
        }
        Company companyDetailEntity = OptionalCompanyDetailEntity.get();
        companyDetailEntity.setCredits(companyDetailEntity.getCredits() - usedCredits);
        companyRepository.save(companyDetailEntity);
        return "updated";
    }
}
