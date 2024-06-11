package com.sctech.emailrequestreceiver.dto;

import lombok.Data;

@Data
public class ExceptionResponseDto {
    private Integer statusCode;
    private String message;
    private String errorCode;
}
