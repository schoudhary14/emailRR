package com.sctech.emailrequestreceiver.repository;

import com.sctech.emailrequestreceiver.model.Domain;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DomainRepository extends MongoRepository<Domain, String> {
    void deleteByIdAndCompanyId(String companyId, String domainId);

    Optional<Domain> findByCompanyIdAndName(String companyId, String name);
}
