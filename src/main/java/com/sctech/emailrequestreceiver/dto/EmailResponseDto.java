package com.sctech.emailrequestreceiver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmailResponseDto {
    private Integer statusCode;
    private String message;
    private EmailResponseData data;

    @Data
    public static class EmailResponseData{
        private LocalDateTime submittedTime;
        private String transactionID;
    }
}
