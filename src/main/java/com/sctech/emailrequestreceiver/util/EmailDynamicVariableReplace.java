package com.sctech.emailrequestreceiver.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailDynamicVariableReplace {
    public String replace(String originalString ,Map<String,String> dynamicValues){
        if (dynamicValues != null && !dynamicValues.isEmpty()){
            for (Map.Entry<String, String> dynamicValue : dynamicValues.entrySet()) {
                originalString = originalString.replace("{{" + dynamicValue.getKey() + "}}", dynamicValue.getValue());
            }
        }

        if (originalString.contains("{{") || originalString.contains("}}")){
            System.out.println("Dynamica variable is missing or invalid");
        }

        return originalString;
    }
}
