package com.sctech.emailrequestreceiver.model;

import com.sctech.emailrequestreceiver.enums.EmailContentType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Document(collection = "emailData")
public class EmailData {

    @Id
    private String id;
    private String companyId;
    private String fromName;
    private String from;
    private String replyTo;
    private String to;
    private String bcc;
    private String cc;
    private String subject;
    private String content;
    private EmailContentType type;
    private List<Attachment> attachment;
    private String status;
    private TrackingFlags trackingFlags;
    private String clientChannelId;
    private String requestMode;
    private String requestId;
    private String requestSource;
    private EmailVendorResponse vendorResponse;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Copy constructor
    public EmailData(){}

    public EmailData(EmailData other) {
        this.id = other.id;
        this.companyId = other.companyId;
        this.fromName = other.fromName;
        this.from = other.from;
        this.replyTo = other.replyTo;
        this.to = other.to;
        this.bcc = other.bcc != null ? other.bcc : null;
        this.cc = other.cc != null ? other.cc : null;
        this.subject = other.subject;
        this.content = other.content;
        this.type = other.type;
        this.attachment = other.attachment != null ? new ArrayList<>(other.attachment) : null;
        this.status = other.status;
        this.trackingFlags = other.trackingFlags != null ? new TrackingFlags(other.trackingFlags) : null;
        this.clientChannelId = other.clientChannelId;
        this.requestMode = other.requestMode;
        this.requestId = other.requestId;
        this.vendorResponse = other.vendorResponse;
        this.createdAt = other.createdAt;
        this.createdBy = other.createdBy;
        this.updatedAt = other.updatedAt;
        this.updatedBy = other.updatedBy;
        this.requestSource = other.requestSource;
    }

    @Data
    public static class TrackingFlags {
        private Boolean opens;
        private Boolean links;

        public TrackingFlags() {}

        public TrackingFlags(TrackingFlags other) {
            this.opens = other.opens;
            this.links = other.links;
        }
    }

    @Data
    public static class Attachment {
        private String fileName;
        private String content;
        private String contentType;

        public  Attachment(){}

        public Attachment(Attachment other) {
            this.fileName = other.fileName;
            this.content = other.content;
            this.contentType = other.contentType;
        }
    }
}