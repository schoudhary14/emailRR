package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.enums.DomainStatus;
import com.sctech.emailrequestreceiver.enums.DomainType;
import com.sctech.emailrequestreceiver.model.Domain;
import com.sctech.emailrequestreceiver.repository.DomainRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class DomainService {

    private static final Logger logger = LogManager.getLogger(DomainService.class);

    @Autowired
    private DomainRepository domainRepository;

    public Boolean isDomainVerified(String companyId, String domainName){
        Optional<Domain> optionalDomain = domainRepository.findByCompanyIdAndName(companyId, domainName);
        if(optionalDomain.isPresent() && optionalDomain.get().getType().equals(DomainType.SENDING) && optionalDomain.get().getStatus().equals(DomainStatus.VERIFIED)){
            return true;
        }
        logger.error("Domain is not verified for company: {} and domain: {}", companyId, domainName);
        return false;
    }

}
