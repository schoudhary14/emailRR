package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.AppConfig;
import com.sctech.emailrequestreceiver.model.Company;
import com.sctech.emailrequestreceiver.model.Template;
import com.sctech.emailrequestreceiver.repository.AppConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private static final Logger logger = LogManager.getLogger(RedisService.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private EmailTemplateService emailTemplateService;
    @Autowired
    private AppConfigService appConfigService;


    private void save(String key, Object value) {
        try{
            redisTemplate.opsForValue().set(key, value);
        }catch (Exception e){
            logger.error("Error in caching : " + e.getMessage());
        }
    }

    private Object get(String key) {
        try{
            return redisTemplate.opsForValue().get(key);
        }catch (Exception e){
            logger.error("Error while retrieving from cache  : " + e.getMessage());
            return null;
        }

    }

    private void hsave(String parentKey, String key, Object value) {
        try {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(parentKey, key, value);
        } catch (Exception e) {
            logger.error("Error in caching : " + e.getMessage());
        }
    }

    private void hsave(String parentKey, String key, Object value, Long time, TimeUnit timeUnit) {
        try {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(parentKey, key, value);
            redisTemplate.opsForValue().set(parentKey+":"+key, value, time, timeUnit);
        } catch (Exception e) {
            logger.error("Error in caching : " + e.getMessage());
        }
    }

    private Object hget(String parentKey, String key) {
        try {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            return hashOperations.get(parentKey, key);
        } catch (Exception e) {
            logger.error("Error while retrieving from cache  : " + e.getMessage());
            return null;
        }
    }

    private void delete(String key) {
        redisTemplate.delete(key);
    }

    public Company getCompanyFromApiKey(String apiKey) {
        Company company = null;
        company = (Company) hget("company",apiKey);
        if(company == null) {
            company = companyService.getApiKeyDetailsByKey(apiKey);
            if(company != null){
                hsave("company", apiKey, company);
            }
        }
        return company;
    }

    public Template getTemplateFromCustomId(String companyId, Integer templateId) {
        Template template = null;
        template = (Template) hget("template", companyId+templateId);

        if(template == null) {
            template =  emailTemplateService.getTemplate(companyId, templateId);
            if(template != null){
                hsave("template",companyId+templateId, template);
            }
        }
        return template;
    }

    public Boolean isGlobalLimitReached(String companyId) {
        Long companyGlobalLimitCounter = (Long) hget("globalLimitCounter", companyId);
        Long appGlobalLimit = (Long) hget("appConfig", "globalLimit");

        if (companyGlobalLimitCounter == null) {
            companyGlobalLimitCounter = appConfigService.globalLimitCounter(companyId);
            hsave("globalLimitCounter", companyId, companyGlobalLimitCounter, 24L, TimeUnit.HOURS);
        }

        if(appGlobalLimit == null) {
            AppConfig appConfig = appConfigService.getByKey("globalLimit");
            if (appConfig == null) {
                return true;
            }
            appGlobalLimit = Long.valueOf(appConfig.getValue());
            hsave("appConfig", "globalLimit", appConfig);
        }

        return companyGlobalLimitCounter >= appGlobalLimit;
    }

}