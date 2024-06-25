package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.AppConfig;
import com.sctech.emailrequestreceiver.model.GlobalLimitCounter;
import com.sctech.emailrequestreceiver.repository.AppConfigRepository;
import com.sctech.emailrequestreceiver.repository.GlobalLimitCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Autowired
    private GlobalLimitCounterRepository globalLimitCounterRepository;

    public AppConfig getByKey(String key) {
        return appConfigRepository.findByKey(key);
    }

    public Long globalLimitCounter(String companyId) {
        GlobalLimitCounter globalLimitCounter = globalLimitCounterRepository.findByCompanyId(companyId);
        return globalLimitCounter != null ? globalLimitCounter.getCounter() : 0L;
    }

}
