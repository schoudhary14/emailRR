package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.enums.CompanyType;
import com.sctech.emailrequestreceiver.model.EmailData;
import com.sctech.emailrequestreceiver.util.EmailDynamicVariableReplace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSingleRequestReceiverService {

    private static final Logger logger = LogManager.getLogger(EmailSingleRequestReceiverService.class);
    @Value("${email.api.queue.singleTopic}")
    private String requestTopic;

    @Value("${email.api.queue.sandBox}")
    private String sandboxRequestTopic;

    @Autowired
    private EmailDynamicVariableReplace emailDynamicVariableReplace;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private CreditService creditService;

    public void process(EmailRequestSingleDto emailRequestPayload, String apiKey){

        EmailData emailDataEntity = new EmailData();
        //Request Meta
        emailDataEntity.setCompanyId(MDC.get(AppHeaders.ENTITY_ID));
        emailDataEntity.setClientChannelId(MDC.get(AppHeaders.ENTITY_CHANNEL_NAME));
        emailDataEntity.setRequestMode("API");

        //From
        emailDataEntity.setFrom(emailRequestPayload.getFrom());

        //Subject
        String subject = emailRequestPayload.getSubject();
        //Html Body
        String htmlBody = emailRequestPayload.getHtmlBody();

        //Tracking FLags
        EmailData.TrackingFlags trackingFlags = new EmailData.TrackingFlags();
        emailDataEntity.setTrackingFlags(trackingFlags);
        trackingFlags.setLinks(emailRequestPayload.getIsTrackOpens());
        trackingFlags.setLinks(emailRequestPayload.getIsTrackLinks());

        //Global
        ////Subject
        subject = emailDynamicVariableReplace.replace(subject,emailRequestPayload.getGlobalDynamicSubject());
        ////Html Body
        htmlBody = emailDynamicVariableReplace.replace(htmlBody,emailRequestPayload.getGlobalDynamicHTMLBody());
        emailDataEntity.setType("html");

        //Reply To (Optional)
        if(emailRequestPayload.getReplyTo() != null && !emailRequestPayload.getReplyTo().isEmpty()){
            emailDataEntity.setReplyTo(emailRequestPayload.getReplyTo());
        }

        //Attachments
        if (emailRequestPayload.getAttachments() != null) {
            List<EmailData.Attachment> emailDataAttachments = new ArrayList<>();
            for (EmailRequestSingleDto.Attachment attachment : emailRequestPayload.getAttachments()){
                EmailData.Attachment emailDataAttachment = new EmailData.Attachment();
                emailDataAttachment.setFileName(attachment.getFilename());
                emailDataAttachment.setContentType(attachment.getContentType());
                emailDataAttachment.setContent(attachment.getContent());
                emailDataAttachments.add(emailDataAttachment);
            }
            emailDataEntity.setAttachment(emailDataAttachments);
        }

        for(EmailRequestSingleDto.Recipient singleTo : emailRequestPayload.getTo()) {
            //To
            emailDataEntity.setTo(singleTo.getEmail());

            //Personalized
            ////Subject
            subject = emailDynamicVariableReplace.replace(subject,singleTo.getDynamicSubject());
            emailDataEntity.setSubject(subject);
            ////Html Body
            htmlBody = emailDynamicVariableReplace.replace(htmlBody,singleTo.getDynamicHTMLBody());
            emailDataEntity.setContent(htmlBody);
            emailDataEntity.setCreatedAt(LocalDateTime.now());

            //CompanyType.SANDBOX
            if(MDC.get(AppHeaders.ENTITY_TYPE).equals(CompanyType.SANDBOX.name())){
                System.out.println("sandbox");
                kafkaService.queueRequest(sandboxRequestTopic, emailDataEntity);
            }else{
                System.out.println("sent");
                kafkaService.queueRequest(requestTopic, emailDataEntity);
            }

        }
    }

}
