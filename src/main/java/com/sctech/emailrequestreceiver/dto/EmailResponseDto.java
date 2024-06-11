package com.sctech.emailrequestreceiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(hidden = true)
public class EmailResponseDto {
    private Integer statusCode;
    private String message;
    private EmailResponseData data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EmailResponseData getData() {
        return data;
    }

    public void setData(EmailResponseData data) {
        this.data = data;
    }

    public static class EmailResponseData{
        private LocalDateTime submittedTime;
        private String transactionID;

        public LocalDateTime getSubmittedTime() {
            return submittedTime;
        }

        public void setSubmittedTime(LocalDateTime submittedTime) {
            this.submittedTime = submittedTime;
        }

        public String getTransactionID() {
            return transactionID;
        }

        public void setTransactionID(String transactionID) {
            this.transactionID = transactionID;
        }
    }
}
