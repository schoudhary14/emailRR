package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String> {
    @Query("{'apiKeys.key' : ?0 }")
    Company findByApiKeyKey(String apiKey);

}
