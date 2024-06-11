package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.Company;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditService {
    private static final Logger logger = LogManager.getLogger(CreditService.class);
    @Autowired
    private CompanyService companyService;

    public boolean isBalanceAvailable(String entityId){
        Long currentBalance = companyService.getCredits(entityId);
        return currentBalance > 0;
    }
}
