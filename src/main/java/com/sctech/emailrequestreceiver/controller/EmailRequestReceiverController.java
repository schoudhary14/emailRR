package com.sctech.emailrequestreceiver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.dto.EmailRequestBatchDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.dto.EmailResponseDto;
import com.sctech.emailrequestreceiver.model.EmailTemplates;
import com.sctech.emailrequestreceiver.service.EmailBatchRequestReceiverService;
import com.sctech.emailrequestreceiver.service.EmailSingleRequestReceiverService;
import com.sctech.emailrequestreceiver.service.EmailTemplateService;
import com.sctech.emailrequestreceiver.util.EmailDynamicVariableReplace;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/email")
@Validated
@Tag(name = "Email Service", description = "Email API details for single and batch mode")
public class EmailRequestReceiverController {
    private static final Logger logger = LogManager.getLogger(EmailRequestReceiverController.class);

    @Autowired
    private EmailSingleRequestReceiverService emailSingleRequestReceiverService;

    @Autowired
    private EmailBatchRequestReceiverService emailBatchRequestReceiverService;

    @Autowired
    private EmailDynamicVariableReplace emailDynamicVariableReplace;

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/send")
    public EmailResponseDto emailRequest(@Valid @RequestBody EmailRequestSingleDto emailRequestPayload,
                                         @RequestHeader("x-apikey") String apiKey,
                                         BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        System.out.println("Email Request");
        EmailResponseDto emailResponseDto = new EmailResponseDto();

        if (bindingResult.hasErrors()) {
            emailResponseDto.setStatusCode(400);
            emailResponseDto.setMessage("Invalid request. " + bindingResult.getAllErrors());
            // If validation fails, return error messages
            return emailResponseDto;
        }

        if (!emailRequestPayload.getFrom().toString().split("@")[1].equals("hosterhero.com")){
            emailResponseDto.setStatusCode(400);
            emailResponseDto.setMessage("Invalid Domain");
            return emailResponseDto;
        }

        emailSingleRequestReceiverService.process(emailRequestPayload, apiKey);
        emailResponseDto.setStatusCode(200);
        emailResponseDto.setMessage("Mail sent successfully");

        EmailResponseDto.EmailResponseData emailResponseData = new EmailResponseDto.EmailResponseData();
        emailResponseData.setSubmittedTime(LocalDateTime.now());
        emailResponseData.setTransactionID(MDC.get(AppHeaders.REQUEST_ID));
        emailResponseDto.setData(emailResponseData);

        return emailResponseDto;
    }

    @PostMapping(value = "/batch/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmailResponseDto emailBatchRequest(@RequestPart("body") String emailRequestBody,
                                              @RequestPart("zipFile") MultipartFile zipFile,
                                              @RequestHeader("x-apikey") String apiKey, BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        if (zipFile.isEmpty()) {
            emailResponseDto.setStatusCode(400);
            emailResponseDto.setMessage("zipFile is required.");
            return emailResponseDto;
        }

        EmailRequestBatchDto batchEmailRequestDto = null;
        try {
            batchEmailRequestDto = objectMapper.readValue(emailRequestBody, EmailRequestBatchDto.class);
        }catch (Exception e){
            emailResponseDto.setStatusCode(400);
            emailResponseDto.setMessage("Invalid body");
            System.out.println("Failed to parse EmailBatchRequestDto");
            System.out.println("Error : " + e.getMessage());
            return emailResponseDto;
        }

        if (!batchEmailRequestDto.getFrom().toString().split("@")[1].equals("hosterhero.com")){
            emailResponseDto.setStatusCode(400);
            emailResponseDto.setMessage("Invalid Domain");
            return emailResponseDto;
        }

        //Email Content
        EmailTemplates emailTemplates =  emailTemplateService.getTemplate(batchEmailRequestDto.getTemplateId());
        if (emailTemplates == null){
            emailResponseDto.setStatusCode(400);
            emailResponseDto.setMessage("Invalid TemplateId");
            return emailResponseDto;
        }

        emailBatchRequestReceiverService.process(batchEmailRequestDto, emailTemplates, zipFile);
        emailResponseDto.setStatusCode(200);
        emailResponseDto.setMessage("Mail sent successfully");

        EmailResponseDto.EmailResponseData emailResponseData = new EmailResponseDto.EmailResponseData();
        emailResponseData.setSubmittedTime(LocalDateTime.now());
        emailResponseData.setTransactionID(MDC.get(AppHeaders.REQUEST_ID));
        emailResponseDto.setData(emailResponseData);

        return emailResponseDto;
    }

    }
