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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailSingleRequestReceiverService extends AbstractEmailRequestReceiverService{

    private static final Logger logger = LogManager.getLogger(EmailSingleRequestReceiverService.class);

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{.*?}}");

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
            emailDataEntity.setAttachment(createAttachmentFromContent(emailRequestPayload, emailDataEntity.getRequestId()));
        }

        String htmlBody = emailDataEntity.getContent();
        String subject = emailDataEntity.getSubject();

        List<EmailData> emailDataList = new ArrayList<>();
        for(EmailRequestSingleDto.Recipient singleTo : emailRequestPayload.getTo()) {
            EmailData tmpEmailData = new EmailData(emailDataEntity);
            //To
            tmpEmailData.setTo(singleTo.getEmail());

            //Personalized
            ////Body
            tmpEmailData.setContent(emailDynamicVariableReplace.replace(htmlBody,singleTo.getDynamicHTMLBody()));
            ////Subject
            tmpEmailData.setSubject(emailDynamicVariableReplace.replace(subject,singleTo.getDynamicSubject()));


            //if (tmpEmailData.getContent().contains("{{") && tmpEmailData.getContent().contains("}}")){
            Matcher matcherContent = PLACEHOLDER_PATTERN.matcher(tmpEmailData.getContent());
            if(matcherContent.find()){
                logger.error("Dynamica variable for content");
                throw new InvalidRequestException("Dynamica variable for content");
            }

            Matcher matcherSubject = PLACEHOLDER_PATTERN.matcher(tmpEmailData.getSubject());
            if(matcherSubject.find()){
                logger.error("Dynamica variable for subject");
                throw new InvalidRequestException("Dynamica variable for subject");
            }

            tmpEmailData.setCreatedAt(LocalDateTime.now());
            emailDataList.add(tmpEmailData);
        }
        return queueEmail(requestTopic, emailDataList);
    }

}
