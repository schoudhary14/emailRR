package com.sctech.emailrequestreceiver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.model.AppConfig;
import com.sctech.emailrequestreceiver.model.Company;
import com.sctech.emailrequestreceiver.model.Template;
import com.sctech.emailrequestreceiver.model.WarmupLimitCounter;
import com.sctech.emailrequestreceiver.repository.WarmupLimitCounterRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
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
    @Autowired
    private WarmupLimitCounterRepository warmupLimitCounterRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
            Object data = hashOperations.get(parentKey, key);
            if (data instanceof Map) {
                return objectMapper.convertValue(data, Company.class);
            }
            return null;
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

    public Boolean isWarmupLimitReached(String companyId) {
        if(MDC.get(AppHeaders.WARMUP_ENABLED) == null || !MDC.get(AppHeaders.WARMUP_ENABLED).equals("true")){
            System.out.println("warmup disabled : " + MDC.get(AppHeaders.WARMUP_ENABLED));
            return false;
        }

        Integer companyWarmupLimitCounter = (Integer) hget("warmupLimitCounter", companyId);
        Integer companyWarmupLimit = Integer.valueOf(MDC.get(AppHeaders.WARMP_LIMIT));

        if (companyWarmupLimitCounter == null) {
            WarmupLimitCounter warmupLimitCounter = warmupLimitCounterRepository.findByCompanyIdAndDate(companyId, LocalDate.now());
            if (warmupLimitCounter == null){
                companyWarmupLimitCounter = 0;
            }else {
                companyWarmupLimitCounter = warmupLimitCounter.getCounter();
                if(companyWarmupLimitCounter == null){
                    companyWarmupLimitCounter = 0;
                }
            }
            hsave("warmupLimitCounter", companyId, companyWarmupLimitCounter, 24L, TimeUnit.HOURS);
        }
        return companyWarmupLimitCounter >= companyWarmupLimit;
    }

}