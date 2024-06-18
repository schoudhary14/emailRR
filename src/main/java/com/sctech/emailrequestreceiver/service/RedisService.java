package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.model.Company;
import com.sctech.emailrequestreceiver.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmailTemplateService emailTemplateService;


    private void save(String key, Object value) {
        try{
            redisTemplate.opsForValue().set(key, value);
        }catch (Exception e){
            System.out.println("Error in save : " + e.getMessage());
        }
    }

    private Object get(String key) {
        try{
            return redisTemplate.opsForValue().get(key);
        }catch (Exception e){
            System.out.println("Error in get : " + e.getMessage());
            return null;
        }

    }

    private void hsave(String parentKey, String key, Object value) {
        try {
            System.out.println("Saving to redis : " + value.toString());
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(parentKey, key, value);
        } catch (Exception e) {
            System.out.println("Error in hsave : " + e.getMessage());
        }
    }

    private Object hget(String parentKey, String key) {
        try {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            return hashOperations.get(parentKey, key);
        } catch (Exception e) {
            System.out.println("Error in hget : " + e.getMessage());
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

}