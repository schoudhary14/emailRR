package com.sctech.emailrequestreceiver.model;

import lombok.Data;

import java.util.List;


@Data
public class EmailVendorResponse {
    private Results results;
    private List<Error> error;

    @Data
    public static class Results {
        private Long total_rejected_recipients;
        private Long total_accepted_recipients;
        private String id;
    }

    @Data
    public static class Error{
        private String description;
        private String code;
        private String message;
    }
}
