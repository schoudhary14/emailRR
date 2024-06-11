package com.sctech.emailrequestreceiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Schema(hidden = true)
public class EmailRequestSingleDto extends EmailRequestBaseDto {

    @NotEmpty(message = "subject is Empty")
    @NotNull(message = "subject is NULL")
    @Size(max = 50, message = "subject exceeded character limit")
    private String subject;

    @NotEmpty(message = "htmlBody is Empty")
    @NotNull(message = "htmlBody is NULL")
    private String htmlBody;

    @Valid
    private List<Attachment> attachments;

    // Getters and setters

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Data
    public static class Attachment {

        @NotEmpty(message = "attachments.filename is Empty")
        @NotNull(message = "attachments.filename is NULL")
        private String filename;

        @NotEmpty(message = "attachments.content is Empty")
        @NotNull(message = "attachments.content is NULL")
        private String content;

        @NotEmpty(message = "attachments.contentType is Empty")
        @NotNull(message = "attachments.contentType is NULL")
        private String contentType;

        // Getters and setters
        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
