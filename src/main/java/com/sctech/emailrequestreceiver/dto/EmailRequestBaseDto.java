package com.sctech.emailrequestreceiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

@Schema(hidden = true)
public class EmailRequestBaseDto {
    @NotEmpty(message = "from is Empty")
    @NotNull(message = "from is NULL")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "from is invalid")
    private String from;

    @Valid
    @NotEmpty(message = "to is Empty")
    @NotNull(message = "to is NULL")
    private List<Recipient> to;

    private boolean trackOpens  = true;

    private boolean trackLinks  = true;

    private Map<String, String> globalDynamicSubject;

    private Map<String, String> globalDynamicHTMLBody;

    private String replyTo;

    public String getFrom() {return from;}

    public void setFrom(String from) {this.from = from;}

    public List<Recipient> getTo() {return to;}

    public void setTo(List<Recipient> to) {this.to = to;}

    public boolean getIsTrackOpens() {
        return trackOpens;
    }

    public void setTrackOpens(boolean trackOpens) {
        this.trackOpens = trackOpens;
    }

    public boolean getIsTrackLinks() {
        return trackLinks;
    }

    public void setTrackLinks(boolean trackLinks) {
        this.trackLinks = trackLinks;
    }

    public Map<String, String> getGlobalDynamicSubject() {
        return globalDynamicSubject;
    }

    public void setGlobalDynamicSubject(Map<String, String> globalDynamicSubject) {
        this.globalDynamicSubject = globalDynamicSubject;
    }

    public Map<String, String> getGlobalDynamicHTMLBody() {
        return globalDynamicHTMLBody;
    }

    public void setGlobalDynamicHTMLBody(Map<String, String> globalDynamicHTMLBody) {
        this.globalDynamicHTMLBody = globalDynamicHTMLBody;
    }

    public String getReplyTo() {return replyTo;}

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public static class Recipient {

        @NotEmpty(message = "to.email is Empty")
        @NotNull(message = "to.email is NULL")
        @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "to.email is invalid")
        private String email;

        private Map<String, String> dynamicSubject;

        private Map<String, String> dynamicHTMLBody;

        private List<String> attachmentFilenames;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Map<String, String> getDynamicSubject() {
            return dynamicSubject;
        }

        public void setDynamicSubject(Map<String, String> dynamicSubject) {
            this.dynamicSubject = dynamicSubject;
        }

        public Map<String, String> getDynamicHTMLBody() {
            return dynamicHTMLBody;
        }

        public void setDynamicHTMLBody(Map<String, String> dynamicHTMLBody) {
            this.dynamicHTMLBody = dynamicHTMLBody;
        }

        public List<String> getAttachmentFilenames() {
            return attachmentFilenames;
        }

        public void setAttachmentFilenames(List<String> attachmentFilenames) {
            this.attachmentFilenames = attachmentFilenames;
        }
    }
}
