package com.sctech.emailrequestreceiver.exceptions;

import com.sctech.emailrequestreceiver.constant.ErrorCodes;
import com.sctech.emailrequestreceiver.constant.ErrorMessages;
import com.sctech.emailrequestreceiver.dto.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        List<String> invalidFieldList = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = null;
            try {
                fieldName = ((FieldError) error).getField();

            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }
            if(!invalidFieldList.contains(fieldName)) {
                invalidFieldList.add(fieldName);
            }
        });

        exceptionResponseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        exceptionResponseDto.setMessage(ErrorMessages.INVALID + " : " + invalidFieldList);
        exceptionResponseDto.setErrorCode(ErrorCodes.INVALID);

        return new ResponseEntity<>(exceptionResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotExistsException.class)
    public ExceptionResponseDto handleUserNotExistsException(NotExistsException ex, WebRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(HttpStatus.CONFLICT.value());
        exceptionResponseDto.setErrorCode("NotExists");
        exceptionResponseDto.setMessage(ex.getMessage());
        return exceptionResponseDto;
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ExceptionResponseDto handleUserNotExistsException(InvalidRequestException ex, WebRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        exceptionResponseDto.setErrorCode("InvalidRequest");
        exceptionResponseDto.setMessage(ex.getMessage());
        return exceptionResponseDto;
    }

    @ExceptionHandler(UnauthorizedHandler.class)
    public ExceptionResponseDto handleUnauthorizedHandler(UnauthorizedHandler ex, WebRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(HttpStatus.FORBIDDEN.value());
        exceptionResponseDto.setErrorCode(ErrorCodes.FORBIDDEN);
        exceptionResponseDto.setMessage(ErrorMessages.FORBIDDEN);
        return exceptionResponseDto;
    }

    @ExceptionHandler(NoCreditsHandler.class)
    public ExceptionResponseDto handleUserNotExistsException(NoCreditsHandler ex, WebRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        exceptionResponseDto.setErrorCode(ErrorCodes.NO_CREDITS);
        exceptionResponseDto.setMessage(ErrorMessages.NO_CREDITS);
        return exceptionResponseDto;
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleSecurityException(Exception exception) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        if (exception instanceof AccessDeniedException) {
            exceptionResponseDto.setStatusCode(403);
            exceptionResponseDto.setMessage(ErrorMessages.ACCESS_RESTRICT);
            exceptionResponseDto.setErrorCode(ErrorCodes.ACCESS_RESTRICT);
        }

        if (exceptionResponseDto.getErrorCode() == null || exceptionResponseDto.getErrorCode().isEmpty()) {
            exceptionResponseDto.setStatusCode(403);
            exceptionResponseDto.setMessage(ErrorMessages.UNKNOWN);
            exceptionResponseDto.setErrorCode(ErrorCodes.UNKNOWN);
        }

        return new ResponseEntity<>(exceptionResponseDto, HttpStatus.valueOf(exceptionResponseDto.getStatusCode()));
    }
}