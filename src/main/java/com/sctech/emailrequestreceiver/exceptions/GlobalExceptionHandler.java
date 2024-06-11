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

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(NotExistsException.class)
    public ExceptionResponseDto handleUserNotExistsException(NotExistsException ex, WebRequest request) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(HttpStatus.CONFLICT.value());
        exceptionResponseDto.setErrorCode("UserNotExists");
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


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleSecurityException(Exception exception) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        if (exception instanceof AccessDeniedException) {
            exceptionResponseDto.setStatusCode(403);
            exceptionResponseDto.setMessage(ErrorMessages.USER_ACCESS_RESTRICT);
            exceptionResponseDto.setErrorCode(ErrorCodes.USER_ACCESS_RESTRICT);
        }

        if (exceptionResponseDto.getErrorCode() == null || exceptionResponseDto.getErrorCode().isEmpty()) {
            exceptionResponseDto.setStatusCode(403);
            exceptionResponseDto.setMessage(ErrorMessages.UNKNOWN);
            exceptionResponseDto.setErrorCode(ErrorCodes.UNKNOWN);
        }

        return new ResponseEntity<>(exceptionResponseDto, HttpStatus.valueOf(exceptionResponseDto.getStatusCode()));
    }
}