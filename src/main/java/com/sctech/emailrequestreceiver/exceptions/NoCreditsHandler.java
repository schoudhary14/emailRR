package com.sctech.emailrequestreceiver.exceptions;

public class NoCreditsHandler extends RuntimeException {
    public NoCreditsHandler(String message) {
        super(message);
    }
}