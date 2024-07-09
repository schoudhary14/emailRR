package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.AppConfig;
import com.sctech.emailrequestreceiver.repository.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    @Autowired
    private AppConfigRepository appConfigRepository;

    public AppConfig getByKey(String key) {
        return appConfigRepository.findByKey(key);
    }

}
