package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.GlobalLimitCounter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalLimitCounterRepository extends MongoRepository<GlobalLimitCounter, String> {
    GlobalLimitCounter findByCompanyId(String companyId);
}
