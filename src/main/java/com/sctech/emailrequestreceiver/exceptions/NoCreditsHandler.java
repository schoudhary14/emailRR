package com.sctech.emailrequestreceiver.exceptions;

import com.sctech.emailrequestreceiver.constant.ErrorMessages;

public class NoCreditsHandler extends RuntimeException {

    public NoCreditsHandler() {
        super(ErrorMessages.FORBIDDEN);
    }
}