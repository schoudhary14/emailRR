package com.sctech.emailrequestreceiver.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileUploadResponse {
    private String filePath;
    private LocalDateTime dateTime;
}
