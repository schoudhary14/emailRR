package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.dto.EmailRequestBatchDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.dto.EmailResponseDto;
import com.sctech.emailrequestreceiver.exceptions.InvalidRequestException;
import com.sctech.emailrequestreceiver.model.EmailData;
import com.sctech.emailrequestreceiver.model.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailBatchRequestReceiverService  extends AbstractEmailRequestReceiverService{

    private static final Logger logger = LogManager.getLogger(DomainService.class);

    @Value("${email.api.queue.batchTopic}")
    private String requestTopic;

    public EmailResponseDto process(EmailRequestBatchDto batchEmailRequestDto, Template template, MultipartFile zipFile){


        if(batchEmailRequestDto.getSubject() == null || batchEmailRequestDto.getSubject().isEmpty()){
            batchEmailRequestDto.setSubject(template.getSubject());
        }

        EmailData emailDataEntity = createEmailDataEntity(batchEmailRequestDto.getFrom(), batchEmailRequestDto.getReplyTo()
                , batchEmailRequestDto.getSubject(), template.getContent(), template.getContentType().toString()
                , batchEmailRequestDto.getTrackOpens(), batchEmailRequestDto.getTrackLinks()
                , batchEmailRequestDto.getGlobalDynamicSubject(), batchEmailRequestDto.getGlobalDynamicHTMLBody());

        emailDataEntity.setRequestSource("batch");
        emailDataEntity.setTemplateId(template.getTemplateId());
        emailDataEntity.setFromName(batchEmailRequestDto.getSenderName());

        String htmlBody = emailDataEntity.getContent();
        String subject = emailDataEntity.getSubject();
        List<EmailData> emailDataList = new ArrayList<>();
        for(EmailRequestSingleDto.Recipient singleTo : batchEmailRequestDto.getTo()) {
            EmailData tmpEmailData = new EmailData(emailDataEntity);
            //To
            tmpEmailData.setTo(singleTo.getEmail());

            //Personalized
            ////Body
            tmpEmailData.setContent(emailDynamicVariableReplace.replace(htmlBody,singleTo.getDynamicHTMLBody()));
            ////Subject
            tmpEmailData.setSubject(emailDynamicVariableReplace.replace(subject,singleTo.getDynamicSubject()));

            if (tmpEmailData.getContent().contains("{{") && tmpEmailData.getContent().contains("}}")){
                logger.error("Dynamic variables are missing or invalid");
                throw new InvalidRequestException("Dynamic variables are missing or invalid");
            }

            List<EmailData.Attachment> emailDataAttachmentList = new ArrayList<>();
            for(String fileName : singleTo.getAttachmentFilenames()) {
                if (fileName != null && !fileName.isEmpty()) {
                    emailDataAttachmentList.add(createAttachmentFromZip(zipFile, fileName, tmpEmailData.getRequestId()));
                }
            }

            if(emailDataAttachmentList != null && emailDataAttachmentList.size() > 0) {
                tmpEmailData.setAttachment(emailDataAttachmentList);
            }

            tmpEmailData.setCreatedAt(LocalDateTime.now());
            emailDataList.add(tmpEmailData);
        }
        return queueEmail(requestTopic, emailDataList);
    }
}
