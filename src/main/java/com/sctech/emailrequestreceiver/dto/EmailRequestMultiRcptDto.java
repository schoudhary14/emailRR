package com.sctech.emailrequestreceiver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailRequestMultiRcptDto {

    @NotEmpty(message = "from is Empty")
    @NotNull(message = "from is NULL")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "from is invalid")
    private String from;

    @NotEmpty(message = "to is Empty")
    @NotNull(message = "to is NULL")
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;

    @NotEmpty(message = "subject is Empty")
    @NotNull(message = "subject is NULL")
    @Size(max = 988, message = "subject exceeded character limit")
    private String subject;

    @NotEmpty(message = "htmlBody is Empty")
    @NotNull(message = "htmlBody is NULL")
    private String htmlBody;

    private Map<String, String> subjectPersonalization;
    private Map<String, String> bodyPersonalization;
    private String replyTo;
    private String senderName;
    private List<Attachment> attachments;

    @JsonProperty("email-sent-status")
    private String emailSentStatus;


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attachment {

        @NotEmpty(message = "attachments.filename is Empty")
        @NotNull(message = "attachments.filename is NULL")
        private String filename;

        @NotEmpty(message = "attachments.content is Empty")
        @NotNull(message = "attachments.content is NULL")
        private String content;

        private String contentType;

    }


}
