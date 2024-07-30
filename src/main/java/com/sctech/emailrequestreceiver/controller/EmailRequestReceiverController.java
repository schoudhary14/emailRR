package com.sctech.emailrequestreceiver.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
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
    private RedisService redisService;

    @Autowired
    private CreditService creditService;

    @Value("${json.object.size.limit}")
    private int jsonObjectSizeLimit;

    @PostMapping("/send")
    public ResponseEntity<EmailResponseDto> emailRequest(@Valid @RequestBody String emailRequest,
                                         @Valid @RequestHeader("x-apikey") String apiKey,
                                         BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        EmailRequestSingleDto emailRequestPayload = new EmailRequestSingleDto();
        try{
            StreamReadConstraints streamReadConstraints = StreamReadConstraints.builder()
                    .maxStringLength(jsonObjectSizeLimit) // Set your desired maximum string length
                    .build();
            JsonFactory jsonFactory = new JsonFactory();
            jsonFactory.setStreamReadConstraints(streamReadConstraints);

            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            emailRequestPayload = objectMapper.readValue(emailRequest, EmailRequestSingleDto.class);
        }catch (Exception e){
            logger.error("Failed to parse request : " + e.getMessage());
            throw new InvalidRequestException("Invalid body");
        }

        if(emailRequestPayload.getTo().size() > emailApiRequestLimit){
            throw new InvalidRequestException("Request Size should lower than " + emailApiRequestLimit);
        }
        Long countOfRecipients = (long) emailRequestPayload.getTo().size();
        creditService.isBalanceAvailable(countOfRecipients);

        EmailResponseDto emailResponseDto = emailSingleRequestReceiverService.process(emailRequestPayload, apiKey);
        return new ResponseEntity<>(emailResponseDto, HttpStatus.OK);
    }

    @PostMapping(value = "/batch/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmailResponseDto> emailBatchRequest(@RequestPart("body") String emailRequestBody,
                                                              @RequestPart(value = "zipFile", required = false) MultipartFile zipFile,
                                                              @RequestHeader("x-apikey") String apiKey, BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

            if(emailRequestBody == null){
                throw new InvalidRequestException("Invalid body");
            }

            EmailResponseDto emailResponseDto = new EmailResponseDto();
            EmailRequestBatchDto batchEmailRequestDto = null;
            try {
                StreamReadConstraints streamReadConstraints = StreamReadConstraints.builder()
                        .maxStringLength(jsonObjectSizeLimit) // Set your desired maximum string length
                        .build();

                // Create a JsonFactory and set the StreamReadConstraints
                JsonFactory jsonFactory = new JsonFactory();
                jsonFactory.setStreamReadConstraints(streamReadConstraints);
                ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

                batchEmailRequestDto = objectMapper.readValue(emailRequestBody, EmailRequestBatchDto.class);
                if(batchEmailRequestDto.getTo().size() > emailApiRequestLimit){
                    throw new InvalidRequestException("Request Size should lower than " + emailApiRequestLimit);
                }
                Long countOfRecipients = (long) batchEmailRequestDto.getTo().size();
                creditService.isBalanceAvailable(countOfRecipients);
            }catch (Exception e){
                logger.error("Failed to parse request : " + e.getMessage());
                throw new InvalidRequestException("Invalid body");
            }
            //Email Content
            Template template =  redisService.getTemplateFromCustomId(MDC.get(AppHeaders.COMPANY_ID), batchEmailRequestDto.getTemplateId());
            if (template == null){
                emailResponseDto.setStatusCode(400);
                emailResponseDto.setMessage("Invalid TemplateId");
                return new ResponseEntity<>(emailResponseDto, HttpStatus.BAD_REQUEST);
            }

            if (zipFile == null || zipFile.isEmpty()) {
                for(EmailRequestBatchDto.Recipient singleTo : batchEmailRequestDto.getTo()) {
                    if (singleTo.getAttachmentFilenames() != null && !singleTo.getAttachmentFilenames().isEmpty()){
                        for(String fileName : singleTo.getAttachmentFilenames()) {
                            if(fileName != null && !fileName.isEmpty()) {
                                throw new InvalidRequestException("filename is present in request but zipFile is missing.");
                            }
                        }
                    }
                }
            }
        emailResponseDto = emailBatchRequestReceiverService.process(batchEmailRequestDto, template, zipFile);
            return new ResponseEntity<>(emailResponseDto, HttpStatus.OK);
    }

    @PostMapping("/multircpt/send")
    public ResponseEntity<EmailResponseDto>  emailMultiRcptRequest(@Valid @RequestBody String emailRequest,
                                                   @RequestHeader("x-apikey") String apiKey,
                                                   BindingResult bindingResult) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        EmailRequestMultiRcptDto emailRequestPayload = new EmailRequestMultiRcptDto();
        try{
            StreamReadConstraints streamReadConstraints = StreamReadConstraints.builder()
                    .maxStringLength(jsonObjectSizeLimit) // Set your desired maximum string length
                    .build();
            JsonFactory jsonFactory = new JsonFactory();
            jsonFactory.setStreamReadConstraints(streamReadConstraints);

            ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
            emailRequestPayload = objectMapper.readValue(emailRequest, EmailRequestMultiRcptDto.class);
        }catch (Exception e){
            logger.error("Failed to parse request : " + e.getMessage());
            throw new InvalidRequestException("Invalid body");
        }

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
    }

}
