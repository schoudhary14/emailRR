package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.exceptions.NoCreditsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditService {
    private static final Logger logger = LogManager.getLogger(CreditService.class);
    @Autowired
    private CompanyService companyService;

    public void isBalanceAvailable(Long countOfRecipients){
        Long currentBalance = Long.valueOf(MDC.get(AppHeaders.ENTITY_CREDITS));
        if(currentBalance <= 0 || currentBalance < countOfRecipients){
            logger.warn("requirement did not matched : Company has no credits");
            throw new NoCreditsHandler("No Credits");
        }
    }

}
