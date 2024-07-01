package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.dto.EmailResponseDto;
import com.sctech.emailrequestreceiver.exceptions.InvalidRequestException;
import com.sctech.emailrequestreceiver.model.EmailData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSingleRequestReceiverService extends AbstractEmailRequestReceiverService{

    private static final Logger logger = LogManager.getLogger(EmailSingleRequestReceiverService.class);
    @Value("${email.api.queue.singleTopic}")
    private String requestTopic;


    public EmailResponseDto process(EmailRequestSingleDto emailRequestPayload, String apiKey){

        EmailResponseDto emailResponseDto = new EmailResponseDto();

        EmailData emailDataEntity = createEmailDataEntity(emailRequestPayload.getFrom(), emailRequestPayload.getReplyTo()
                , emailRequestPayload.getSubject(), emailRequestPayload.getHtmlBody(), "HTML"
                , emailRequestPayload.getTrackOpens(), emailRequestPayload.getTrackLinks()
                , emailRequestPayload.getGlobalDynamicSubject(), emailRequestPayload.getGlobalDynamicHTMLBody());

        emailDataEntity.setRequestSource("single");
        //Attachments
        if (emailRequestPayload.getAttachments() != null) {
            emailDataEntity.setAttachment(createAttachmentFromContent(emailRequestPayload));
        }

        String htmlBody = emailDataEntity.getContent();
        String subject = emailDataEntity.getSubject();

        List<EmailData> emailDataList = new ArrayList<>();
        for(EmailRequestSingleDto.Recipient singleTo : emailRequestPayload.getTo()) {
            EmailData tmpEmailData = new EmailData(emailDataEntity);
            //To
            System.out.println("New To : " + singleTo);
            tmpEmailData.setTo(singleTo.getEmail());
            System.out.println("After Setting : " + tmpEmailData.getTo());

            //Personalized
            ////Body
            tmpEmailData.setContent(emailDynamicVariableReplace.replace(htmlBody,singleTo.getDynamicHTMLBody()));
            ////Subject
            tmpEmailData.setSubject(emailDynamicVariableReplace.replace(subject,singleTo.getDynamicSubject()));

            if (tmpEmailData.getContent().contains("{{") && tmpEmailData.getContent().contains("}}")){
                logger.error("Dynamica variable is missing or invalid");
                throw new InvalidRequestException("Dynamica variable is missing or invalid");
            }


            tmpEmailData.setCreatedAt(LocalDateTime.now());
            emailDataList.add(tmpEmailData);
            System.out.println("EmailData List : " + emailDataList);
        }
        return queueEmail(requestTopic, emailDataList);
    }

}
