package com.sctech.emailrequestreceiver.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailRequestBatchDto extends EmailRequestBaseDto {

    @Size(max = 255, message = "subject exceeded character limit")
    private String subject;
    private Integer templateId;
    private String senderName;

}
