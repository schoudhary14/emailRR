package com.sctech.emailrequestreceiver.exceptions;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.sctech.emailrequestreceiver.constant.ErrorCodes;
import com.sctech.emailrequestreceiver.constant.ErrorMessages;
import com.sctech.emailrequestreceiver.dto.ExceptionResponseDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> invalidFieldList = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            if (!invalidFieldList.contains(fieldName)) {
                invalidFieldList.add(fieldName);
            }
        });
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCodes.INVALID, ErrorMessages.INVALID + " : " + invalidFieldList);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                                 WebRequest request) {

        // Customize error message based on the specific exception details
        String fieldName = ex.getName();
        String invalidValue = ex.getValue().toString();
        String errorDetail = "Invalid value " + invalidValue + " for parameter " + fieldName + ".";
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCodes.INVALID, ErrorMessages.INVALID + " : " + errorDetail);
    }

    @ExceptionHandler(ValueInstantiationException.class)
    public ResponseEntity<ExceptionResponseDto> handleValueInstantiationException(ValueInstantiationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCodes.INVALID, ErrorMessages.INVALID + " : " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorMessages.INVALID, ErrorMessages.INVALID + " : " + details);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNoResourceFoundException(NoResourceFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND.value(), ErrorCodes.NOT_EXISTS, ErrorMessages.NOT_EXISTS + " : " + ex.getMessage());
    }


    @ExceptionHandler(NoCreditsHandler.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotExistsException(NoCreditsHandler ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), ErrorCodes.NO_CREDITS, ErrorMessages.NO_CREDITS);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ExceptionResponseDto> handleMultipartException(MultipartException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), ErrorCodes.INVALID, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleSecurityException(Exception ex) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();

        // TODO send this stack trace to an observability tool
        ex.printStackTrace();

        Integer status = null;
        String errorCode = null;
        String message = null;
        // Handle specific exception types
        if (ex instanceof BadCredentialsException) {
            status = HttpStatus.FORBIDDEN.value();
            errorCode = ErrorMessages.INVALID;
            message = ErrorCodes.INVALID;
        } else if (ex instanceof AccountStatusException) {
            status = HttpStatus.FORBIDDEN.value();
            errorCode = ErrorMessages.LOCKED;
            message = ErrorCodes.LOCKED;
        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN.value();
            errorCode = ErrorMessages.ACCESS_RESTRICT;
            message = ErrorCodes.ACCESS_RESTRICT;
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST.value();
            errorCode = ErrorMessages.INVALID + " : " + ex.getMessage();
            message = ErrorCodes.INVALID;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            errorCode = ErrorMessages.UNKNOWN;
            message = ErrorCodes.UNKNOWN;
        }
        return buildErrorResponse(status, errorCode, message);
    }

    // Helper method to construct error response
    private ResponseEntity<ExceptionResponseDto> buildErrorResponse(Integer status, String errorCode, String message) {
        ExceptionResponseDto errorResponse = new ExceptionResponseDto();
        errorResponse.setStatusCode(status);
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        return ResponseEntity.status(status).body(errorResponse);
    }

}