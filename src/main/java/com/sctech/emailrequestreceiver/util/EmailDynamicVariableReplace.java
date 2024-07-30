package com.sctech.emailrequestreceiver.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class EmailDynamicVariableReplace {

    private static final Logger logger = LogManager.getLogger(EmailDynamicVariableReplace.class);

    public String replace(String originalString ,Map<String,String> dynamicValues){
        if (dynamicValues != null && !dynamicValues.isEmpty()){
            for (Map.Entry<String, String> dynamicValue : dynamicValues.entrySet()) {
                originalString = originalString.replace("{{" + dynamicValue.getKey() + "}}", dynamicValue.getValue());
            }
        }

        return originalString;
    }
}
