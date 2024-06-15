package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.enums.DomainStatus;
import com.sctech.emailrequestreceiver.exceptions.NotExistsException;
import com.sctech.emailrequestreceiver.model.Domain;
import com.sctech.emailrequestreceiver.repository.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DomainService {

    @Autowired
    private DomainRepository domainRepository;


    public Boolean isDomainVerified(String companyId, String domainName){
        Optional<Domain> optionalDomain = domainRepository.findByCompanyIdAndName(companyId, domainName);
        if(optionalDomain.isPresent() && optionalDomain.get().getStatus().equals(DomainStatus.VERIFIED)){
            return true;
        }
        return false;
    }

}
