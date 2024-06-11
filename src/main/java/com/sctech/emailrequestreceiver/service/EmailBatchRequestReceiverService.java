package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.dto.EmailRequestBatchDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.enums.CompanyType;
import com.sctech.emailrequestreceiver.model.EmailData;
import com.sctech.emailrequestreceiver.model.EmailTemplates;
import com.sctech.emailrequestreceiver.util.EmailDynamicVariableReplace;
import com.sctech.emailrequestreceiver.util.ZipFileHelper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailBatchRequestReceiverService {

    @Value("${email.api.queue.batchTopic}")
    private String requestTopic;

    @Value("${email.api.queue.sandBox}")
    private String sandboxRequestTopic;

    @Autowired
    private EmailDynamicVariableReplace emailDynamicVariableReplace;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private ZipFileHelper zipFileHelper;

    public void process(EmailRequestBatchDto batchEmailRequestDto, EmailTemplates emailTemplates, MultipartFile zipFile){

        String htmlBody = emailTemplates.getContent();

        EmailData emailDataEntity = new EmailData();
        //Request Meta
        emailDataEntity.setCompanyId(MDC.get(AppHeaders.ENTITY_ID));
        emailDataEntity.setClientChannelId(MDC.get(AppHeaders.ENTITY_CHANNEL_NAME));
        emailDataEntity.setRequestMode("API");
        emailDataEntity.setType(emailTemplates.getContentType());

        //From
        emailDataEntity.setFrom(batchEmailRequestDto.getFrom());

        //Subject
        String subject = batchEmailRequestDto.getSubject();

        //Tracking Flags
        EmailData.TrackingFlags trackingFlags = new EmailData.TrackingFlags();
        emailDataEntity.setTrackingFlags(trackingFlags);
        trackingFlags.setOpens(batchEmailRequestDto.getTrackOpens());
        trackingFlags.setLinks(batchEmailRequestDto.getTrackLinks());

        //Global
        ////Subject
        subject = emailDynamicVariableReplace.replace(subject, batchEmailRequestDto.getGlobalDynamicSubject());
        ////Html Body
        htmlBody = emailDynamicVariableReplace.replace(htmlBody, batchEmailRequestDto.getGlobalDynamicHTMLBody());

        //Reply To (Optional)
        if(batchEmailRequestDto.getReplyTo() != null && !batchEmailRequestDto.getReplyTo().isEmpty()){
            emailDataEntity.setReplyTo(batchEmailRequestDto.getReplyTo());
        }

        for(EmailRequestSingleDto.Recipient singleTo : batchEmailRequestDto.getTo()) {
            //To
            emailDataEntity.setTo(singleTo.getEmail());

            //Personalized
            ////Subject
            subject = emailDynamicVariableReplace.replace(subject,singleTo.getDynamicSubject());
            htmlBody = emailDynamicVariableReplace.replace(htmlBody,singleTo.getDynamicHTMLBody());
            emailDataEntity.setContent(htmlBody);
            emailDataEntity.setSubject(subject);

            //Attachment
            List<EmailData.Attachment> emailDataAttachments = new ArrayList<>();
            for (String fileName : singleTo.getAttachmentFilenames()) {
                String fileContent = zipFileHelper.fileContentFromZip(fileName,zipFile);
                EmailData.Attachment emailDataAttachment = new EmailData.Attachment();
                emailDataAttachment.setFileName(fileName);
                emailDataAttachment.setContentType(zipFileHelper.getFileContentType(fileName));
                emailDataAttachment.setContent(fileContent);
                emailDataAttachments.add(emailDataAttachment);
            }
            emailDataEntity.setAttachment(emailDataAttachments);
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
