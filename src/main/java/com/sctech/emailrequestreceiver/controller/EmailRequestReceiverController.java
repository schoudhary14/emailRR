package com.sctech.emailrequestreceiver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.dto.EmailRequestBatchDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestMultiRcptDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.dto.EmailResponseDto;
import com.sctech.emailrequestreceiver.exceptions.InvalidRequestException;
import com.sctech.emailrequestreceiver.model.Template;
import com.sctech.emailrequestreceiver.service.*;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/email")
@Validated
public class EmailRequestReceiverController {
    private static final Logger logger = LogManager.getLogger(EmailRequestReceiverController.class);

    @Autowired
    @Value("${EMAIL_API_REQUEST_MAX_Limit}")
    private Integer emailApiRequestLimit;

    @Autowired
    private EmailSingleRequestReceiverService emailSingleRequestReceiverService;

    @Autowired
    private EmailBatchRequestReceiverService emailBatchRequestReceiverService;

    @Autowired
    private EmailMultiRcptRequestReceiverService emailMultiRcptRequestReceiverService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CreditService creditService;

    @PostMapping("/send")
    public ResponseEntity<EmailResponseDto> emailRequest(@Valid @RequestBody EmailRequestSingleDto emailRequestPayload,
                                         @Valid @RequestHeader("x-apikey") String apiKey,
                                         BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        if(emailRequestPayload.getTo().size() > emailApiRequestLimit){
            throw new InvalidRequestException("Request Size should lower than " + emailApiRequestLimit);
        }
        Long countOfRecipients = (long) emailRequestPayload.getTo().size();
        creditService.isBalanceAvailable(countOfRecipients);

        try {
            EmailResponseDto emailResponseDto = emailSingleRequestReceiverService.process(emailRequestPayload, apiKey);
            return new ResponseEntity<>(emailResponseDto, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("Error : ", e);
            EmailResponseDto emailResponseDto = new EmailResponseDto();
            emailResponseDto.setStatusCode(500);
            emailResponseDto.setMessage("Internal Server Error");
            return new ResponseEntity<>(emailResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/batch/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmailResponseDto> emailBatchRequest(@RequestPart("body") String emailRequestBody,
                                                              @RequestPart("zipFile") MultipartFile zipFile,
                                                              @RequestHeader("x-apikey") String apiKey, BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        try{
            EmailResponseDto emailResponseDto = new EmailResponseDto();
            if (zipFile.isEmpty()) {
                emailResponseDto.setStatusCode(400);
                emailResponseDto.setMessage("zipFile is required.");
                return new ResponseEntity<>(emailResponseDto, HttpStatus.BAD_REQUEST);
            }

            EmailRequestBatchDto batchEmailRequestDto = null;
            try {
                batchEmailRequestDto = objectMapper.readValue(emailRequestBody, EmailRequestBatchDto.class);
                if(batchEmailRequestDto.getTo().size() > emailApiRequestLimit){
                    throw new InvalidRequestException("Request Size should lower than " + emailApiRequestLimit);
                }
                Long countOfRecipients = (long) batchEmailRequestDto.getTo().size();
                creditService.isBalanceAvailable(countOfRecipients);
            }catch (Exception e){
                emailResponseDto.setStatusCode(400);
                emailResponseDto.setMessage("Invalid body");
                logger.error("Failed to parse request : " + e.getMessage());
                return new ResponseEntity<>(emailResponseDto, HttpStatus.BAD_REQUEST);
            }
            //Email Content
            Template template =  redisService.getTemplateFromCustomId(MDC.get(AppHeaders.COMPANY_ID), batchEmailRequestDto.getTemplateId());
            if (template == null){
                emailResponseDto.setStatusCode(400);
                emailResponseDto.setMessage("Invalid TemplateId");
                return new ResponseEntity<>(emailResponseDto, HttpStatus.BAD_REQUEST);
            }
            emailResponseDto = emailBatchRequestReceiverService.process(batchEmailRequestDto, template, zipFile);
            return new ResponseEntity<>(emailResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error : ", e);
            EmailResponseDto emailResponseDto = new EmailResponseDto();
            emailResponseDto.setStatusCode(500);
            emailResponseDto.setMessage("Internal Server Error");
            return new ResponseEntity<>(emailResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/multircpt/send")
    public ResponseEntity<EmailResponseDto>  emailMultiRcptRequest(@Valid @RequestBody EmailRequestMultiRcptDto emailRequestPayload,
                                                   @RequestHeader("x-apikey") String apiKey,
                                                   BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        try{
            Long countOfRecipients = (long) emailRequestPayload.getTo().size();
            if(emailRequestPayload.getCc() != null){
                countOfRecipients = countOfRecipients + emailRequestPayload.getCc().size();
            }

            if (emailRequestPayload.getBcc() != null){
                countOfRecipients = countOfRecipients + emailRequestPayload.getBcc().size();
            }
            creditService.isBalanceAvailable(countOfRecipients);
            emailResponseDto = emailMultiRcptRequestReceiverService.process(emailRequestPayload);
            return new ResponseEntity<>(emailResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error : ", e);
            emailResponseDto.setStatusCode(500);
            emailResponseDto.setMessage("Internal Server Error");
            return new ResponseEntity<>(emailResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
