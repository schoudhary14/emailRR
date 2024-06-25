package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.AppConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppConfigRepository extends MongoRepository<AppConfig, String> {
    AppConfig findByKey(String key);
}
