package com.sctech.emailrequestreceiver.exceptions;


import com.sctech.emailrequestreceiver.constant.ErrorMessages;

public class NotExistsException extends RuntimeException{
    public NotExistsException(String message) {
        super(message);
    }
}
