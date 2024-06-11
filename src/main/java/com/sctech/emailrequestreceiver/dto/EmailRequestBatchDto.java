package com.sctech.emailrequestreceiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(hidden = true)
public class EmailRequestBatchDto extends EmailRequestBaseDto {

    @Size(max = 255, message = "subject exceeded character limit")
    private String subject;

    private Integer templateId;

    private String senderName;

    public String getSubject() {return subject;}

    public void setSubject(String subject) {this.subject = subject;}

    public Integer getTemplateId() {return templateId;}

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
