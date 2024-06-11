package com.sctech.emailrequestreceiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(hidden = true)
@Data
public class EmailRequestBatchDto extends EmailRequestBaseDto {

    @Size(max = 255, message = "subject exceeded character limit")
    private String subject;
    private Integer templateId;
    private String senderName;

}
