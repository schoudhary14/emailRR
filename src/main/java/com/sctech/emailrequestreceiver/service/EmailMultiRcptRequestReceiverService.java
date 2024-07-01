package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.dto.EmailRequestMultiRcptDto;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmailMultiRcptRequestReceiverService extends AbstractEmailRequestReceiverService{

    private static final Logger logger = LogManager.getLogger(EmailMultiRcptRequestReceiverService.class);
    @Value("${email.api.queue.multiRcptTopic}")
    private String requestTopic;


    public EmailResponseDto process(EmailRequestMultiRcptDto emailRequestPayload){

        EmailResponseDto emailResponseDto = new EmailResponseDto();

        EmailData emailDataEntity = createEmailDataEntity(emailRequestPayload.getFrom(), emailRequestPayload.getReplyTo()
                , emailRequestPayload.getSubject(), emailRequestPayload.getHtmlBody(), "HTML"
                , false, false
                , emailRequestPayload.getSubjectPersonalization(), emailRequestPayload.getBodyPersonalization());

        emailDataEntity.setRequestSource("multircpt");
        emailDataEntity.setFromName(emailRequestPayload.getSenderName());

        //Attachments
        if (emailRequestPayload.getAttachments() != null) {
            emailDataEntity.setAttachment(createAttachmentFromContent(emailRequestPayload));
        }

        // CC
        if(emailRequestPayload.getCc() != null && !emailRequestPayload.getCc().isEmpty()){
            String cc = emailRequestPayload.getCc().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            emailDataEntity.setCc(cc);
        }

        // BCC
        if(emailRequestPayload.getBcc() != null && !emailRequestPayload.getBcc().isEmpty()) {
            String bcc = emailRequestPayload.getBcc().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            emailDataEntity.setBcc(bcc);

        }

        List<EmailData> emailDataList = new ArrayList<>();
        for(String singleTo : emailRequestPayload.getTo()) {
            EmailData tmpEmailData = new EmailData(emailDataEntity);

            //To
            tmpEmailData.setTo(singleTo);

            if (tmpEmailData.getContent().contains("{{") && tmpEmailData.getContent().contains("}}")){
                logger.error("Dynamic variable is missing or invalid");
                throw new InvalidRequestException("Dynamic variable is missing or invalid");
            }
            tmpEmailData.setCreatedAt(LocalDateTime.now());
            emailDataList.add(tmpEmailData);
        }
        return queueEmail(requestTopic, emailDataList);
    }

}
