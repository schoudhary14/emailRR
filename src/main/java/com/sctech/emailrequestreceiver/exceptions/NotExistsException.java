package com.sctech.emailrequestreceiver.exceptions;


import com.sctech.emailrequestreceiver.constant.ErrorMessages;

public class NotExistsException extends RuntimeException{
    public NotExistsException() {
        super(ErrorMessages.NOT_EXISTS);
    }
}
