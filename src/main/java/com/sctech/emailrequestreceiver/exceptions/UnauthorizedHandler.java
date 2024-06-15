package com.sctech.emailrequestreceiver.exceptions;

import com.sctech.emailrequestreceiver.constant.ErrorMessages;

public class UnauthorizedHandler extends RuntimeException {

    public UnauthorizedHandler() {
        super(ErrorMessages.FORBIDDEN);
    }
}