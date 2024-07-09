package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.WarmupLimitCounter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface WarmupLimitCounterRepository extends MongoRepository<WarmupLimitCounter, String> {
    WarmupLimitCounter findByCompanyIdAndDate(String companyId, LocalDate date);
}
