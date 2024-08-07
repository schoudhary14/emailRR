package com.sctech.emailrequestreceiver.constant;

import org.springframework.stereotype.Component;

@Component
public class AppHeaders {
    public static String API_HEADER = "x-apikey";
    public static String REQUEST_ID = "App-Request-Id";
    public static String COMPANY_ID = "companyId";
    public static String COMPANY_BILL_TYPE = "entityType";
    public static String ENTITY_CREDITS = "entityCredits";
    public static String COMPANY_CHANNEL_NAME = "entityChannelName";
    public static String COMPANY_NAME = "entityName";
    public static String WARMUP_ENABLED = "warmupEnabled";
    public static String WARMP_LIMIT = "warmupLimit";
    public static String WARMUP_LIMIT_UNIT = "warmupLimitUnit";
}
