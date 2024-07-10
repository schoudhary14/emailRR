package com.sctech.emailrequestreceiver.service;

import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.dto.EmailRequestBatchDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestMultiRcptDto;
import com.sctech.emailrequestreceiver.dto.EmailRequestSingleDto;
import com.sctech.emailrequestreceiver.dto.EmailResponseDto;
import com.sctech.emailrequestreceiver.enums.CompanyType;
import com.sctech.emailrequestreceiver.enums.EmailContentType;
import com.sctech.emailrequestreceiver.exceptions.NotExistsException;
import com.sctech.emailrequestreceiver.exceptions.WarmupRequestException;
import com.sctech.emailrequestreceiver.model.EmailData;
import com.sctech.emailrequestreceiver.util.EmailDynamicVariableReplace;
import com.sctech.emailrequestreceiver.util.ZipFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AbstractEmailRequestReceiverService {

    private static final Logger logger = LogManager.getLogger(AbstractEmailRequestReceiverService.class);

    @Value("${email.api.queue.sandBox}")
    private String sandboxRequestTopic;

    @Autowired
    protected EmailDynamicVariableReplace emailDynamicVariableReplace;

    @Autowired
    protected KafkaService kafkaService;

    @Autowired
    protected ZipFileHelper zipFileHelper;

    @Autowired
    private DomainService domainService;

    @Autowired
    private RedisService redisService;

    protected EmailResponseDto queueEmail(String requestTopic, List<EmailData> emailDataList) {
        try {
            String companyBillType = MDC.get(AppHeaders.COMPANY_BILL_TYPE);
            String topic = companyBillType.equals(CompanyType.SANDBOX.name()) ? sandboxRequestTopic : requestTopic;
            for (EmailData emailDataEntity : emailDataList) {
                kafkaService.queueRequest(topic, emailDataEntity);
            }
            return createSuccessResponse();
        } catch (Exception e) {
            logger.error("Error while queuing : " + e.getMessage());
            return createErrorResponse(500, "Internal Server Error");
        }
    }

    private EmailResponseDto createSuccessResponse() {
        EmailResponseDto response = new EmailResponseDto();
        response.setStatusCode(200);
        response.setMessage("Mail sent successfully");

        EmailResponseDto.EmailResponseData responseData = new EmailResponseDto.EmailResponseData();
        responseData.setSubmittedTime(LocalDateTime.now());
        responseData.setTransactionID(MDC.get(AppHeaders.REQUEST_ID));
        response.setData(responseData);
        return response;
    }

    private EmailResponseDto createErrorResponse(Integer statusCode, String message) {
        EmailResponseDto response = new EmailResponseDto();
        response.setStatusCode(statusCode);
        response.setMessage(message);
        return response;
    }

    protected List<EmailData.Attachment> createAttachmentFromZip(MultipartFile zipFile, EmailRequestBatchDto.Recipient singleTo) {
        List<EmailData.Attachment> emailDataAttachmentList = new ArrayList<>();
        for (String fileName : singleTo.getAttachmentFilenames()) {
            String fileContent = zipFileHelper.fileContentFromZip(fileName,zipFile);
            EmailData.Attachment emailDataAttachment = new EmailData.Attachment();
            emailDataAttachment.setFileName(fileName);
            emailDataAttachment.setContentType(zipFileHelper.getFileContentType(fileName));
            emailDataAttachment.setContent(fileContent);
            emailDataAttachmentList.add(emailDataAttachment);
        }
        return emailDataAttachmentList;
    }

    protected List<EmailData.Attachment> createAttachmentFromContent(EmailRequestSingleDto emailRequestPayload) {
        List<EmailData.Attachment> emailDataAttachmentList = new ArrayList<>();
        for (EmailRequestSingleDto.Attachment attachment : emailRequestPayload.getAttachments()){
            EmailData.Attachment emailDataAttachment = new EmailData.Attachment();
            emailDataAttachment.setFileName(attachment.getFilename());
            emailDataAttachment.setContentType(attachment.getContentType());
            emailDataAttachment.setContent(attachment.getContent());
            emailDataAttachmentList.add(emailDataAttachment);
        }
        return emailDataAttachmentList;
    }

    protected List<EmailData.Attachment> createAttachmentFromContent(EmailRequestMultiRcptDto emailRequestPayload) {
        List<EmailData.Attachment> emailDataAttachmentList = new ArrayList<>();
        for (EmailRequestMultiRcptDto.Attachment attachment : emailRequestPayload.getAttachments()){
            EmailData.Attachment emailDataAttachment = new EmailData.Attachment();
            emailDataAttachment.setFileName(attachment.getFilename());
            emailDataAttachment.setContent(attachment.getContent());
            emailDataAttachmentList.add(emailDataAttachment);
        }
        return emailDataAttachmentList;
    }


    private void isDomainVerified(String companyId, String domain) {
        if (!domainService.isDomainVerified(companyId, domain)){
            throw new NotExistsException("From Domain");
        }
    }

    protected EmailData createEmailDataEntity(String from, String replyTo
            , String subject, String htmlBody, String contentType
            , Boolean trackOpens, Boolean trackLinks
            , Map<String, String> globalDynamicSubject, Map<String, String> globalDynamicHTMLBody) {

        String companyId = MDC.get(AppHeaders.COMPANY_ID);
        isDomainVerified(companyId, from.toString().split("@")[1]);

        if (redisService.isWarmupLimitReached(companyId)) {
            logger.warn("requirement did not matched : Global limit reached");
            throw new WarmupRequestException("Warmup Limit Reached");
        }


        EmailData emailDataEntity = new EmailData();
        String clientChannelId = MDC.get(AppHeaders.COMPANY_CHANNEL_NAME);
        String requestId = MDC.get(AppHeaders.REQUEST_ID);
        emailDataEntity.setCompanyId(companyId);
        emailDataEntity.setClientChannelId(clientChannelId);
        emailDataEntity.setRequestMode("API");
        emailDataEntity.setRequestId(requestId);
        emailDataEntity.setType(EmailContentType.HTML);
        emailDataEntity.setFrom(from);
        emailDataEntity.setReplyTo(replyTo);
        //Tracking Flags
        EmailData.TrackingFlags trackingFlags = new EmailData.TrackingFlags();
        emailDataEntity.setTrackingFlags(trackingFlags);
        trackingFlags.setOpens(trackOpens);
        trackingFlags.setLinks(trackLinks);
        emailDataEntity.setTrackingFlags(trackingFlags);

        if(replyTo != null && !replyTo.isEmpty()){
            emailDataEntity.setReplyTo(replyTo);
        }

        //Global
        ////Subject
        subject = emailDynamicVariableReplace.replace(subject, globalDynamicSubject);
        ////Html Body
        htmlBody = emailDynamicVariableReplace.replace(htmlBody, globalDynamicHTMLBody);

        emailDataEntity.setSubject(subject);
        emailDataEntity.setContent(htmlBody);

        emailDataEntity.setCreatedAt(LocalDateTime.now());
        return emailDataEntity;
    }

}
