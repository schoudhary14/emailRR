package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.enums.EntityStatus;
import com.sctech.emailrequestreceiver.exceptions.NoCreditsHandler;
import com.sctech.emailrequestreceiver.exceptions.UnauthorizedHandler;
import com.sctech.emailrequestreceiver.model.Company;
import com.sctech.emailrequestreceiver.security.ApiKeyAuth;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ApiKeyService {
    private static final Logger logger = LogManager.getLogger(ApiKeyService.class);
    @Autowired
    private CompanyService companyService;

    @Autowired
    private AppHeaders appHeaders;

    @Autowired
    private RedisService redisService;


    private static final int KEY_LENGTH = 32;
    private static final int MAX_API_KEY_LENGTH = 64;
    private static final String API_KEY_REGEX = "^[a-zA-Z0-9]*$";

    public Optional<Authentication> validateApiKey(HttpServletRequest request) throws IOException {
        String requestApiKey = request.getHeader(AppHeaders.API_HEADER);

        if(requestApiKey == null){
            return Optional.empty();
        } else if(requestApiKey.length() < KEY_LENGTH || requestApiKey.length() > MAX_API_KEY_LENGTH || !requestApiKey.matches(API_KEY_REGEX)){
            System.out.println("requirement did not matched : provide key : " + requestApiKey + " and length : " + requestApiKey.length());
            return Optional.empty();
        }

        String remoteAddr = request.getRemoteAddr();
        Company company = redisService.getCompanyFromApiKey(requestApiKey);

        if(company == null){
            System.out.println("API Key not matched");
            return Optional.empty();
        } else if(company.getStatus().equals(EntityStatus.INACTIVE)){
            return Optional.empty();
        } else if(company.getCredits() <= 0){
            throw new NoCreditsHandler();
        }

        List<Company.ApiKey> optionalApiKeyEntities = company.getApiKeys();
        Company.ApiKey apiKeyEntity = null;
        for(Company.ApiKey optionalApiKeyEntity : optionalApiKeyEntities){
            if(optionalApiKeyEntity.getKey().equals(requestApiKey)){
                apiKeyEntity = optionalApiKeyEntity;
                break;
            }
        }

        if (apiKeyEntity != null){
            if(apiKeyEntity.getStatus().equals(EntityStatus.INACTIVE) || (apiKeyEntity.getIpAddress() != null && !Arrays.asList(apiKeyEntity.getIpAddress()).contains(remoteAddr))){
                System.out.println("API Key is inactive or ip address mismatched");
                return Optional.empty();
            }

        }
        MDC.put(AppHeaders.COMPANY_ID,company.getId());
        MDC.put(AppHeaders.COMPANY_BILL_TYPE,company.getBillType().name());
        MDC.put(AppHeaders.COMPANY_CHANNEL_NAME,apiKeyEntity.getName());

        return Optional.of(new ApiKeyAuth(requestApiKey, AuthorityUtils.NO_AUTHORITIES));
    }


}
