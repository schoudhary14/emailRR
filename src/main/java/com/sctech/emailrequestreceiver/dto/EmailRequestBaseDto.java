package com.sctech.emailrequestreceiver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EmailRequestBaseDto {
    @NotEmpty(message = "from is Empty")
    @NotNull(message = "from is NULL")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "from is invalid")
    private String from;

    @Valid
    @NotEmpty(message = "to is Empty")
    @NotNull(message = "to is NULL")
    private List<Recipient> to;
    private Boolean trackOpens  = true;
    private Boolean trackLinks  = true;
    private Map<String, String> globalDynamicSubject;
    private Map<String, String> globalDynamicHTMLBody;
    private String replyTo;

    @JsonProperty("email-sent-status")
    private String emailSentStatus;

    @Data
    public static class Recipient {

        @NotEmpty(message = "to.email is Empty")
        @NotNull(message = "to.email is NULL")
        @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "to.email is invalid")
        private String email;
        private Map<String, String> dynamicSubject;
        private Map<String, String> dynamicHTMLBody;
        private List<String> attachmentFilenames;

    }
}
