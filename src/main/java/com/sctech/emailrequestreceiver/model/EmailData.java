package com.sctech.emailrequestreceiver.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Document(collection = "emailData")
@Schema(hidden = true)
public class EmailData {

    @Id
    private String id;
    private String companyId;
    private String fromName;
    private String from;
    private String replyTo;
    private String to;
    private String[] bcc;
    private String[] cc;
    private String subject;
    private String content;
    private String type;
    private List<Attachment> attachment;
    private String status;
    private TrackingFlags trackingFlags;
    private String clientChannelId;
    private String requestMode;
    private LocalDateTime createdAt;

    @Data
    public static class TrackingFlags {
        private Boolean open;
        private Boolean links;
    }

    @Data
    public static class Attachment{
        private String fileName;
        private String content;
        private String contentType;
    }

}
