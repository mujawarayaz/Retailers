package com.retailoffer.utility;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.retailoffer.exception.RetailerException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    private final Environment environment;

    private static final Log LOGGER = LogFactory.getLog(ExceptionControllerAdvice.class);

    @Autowired
    public ExceptionControllerAdvice(Environment environment) {
        this.environment = environment;
    }

    @ExceptionHandler(RetailerException.class)
    public ResponseEntity<ErrorInfo> handleRetailerException(RetailerException exception) {
        LOGGER.error("RetailerException occurred", exception);

        String errorMessage = environment.getProperty(exception.getMessage(), "Retailer error occurred");

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(errorMessage);
        errorInfo.setErrorCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        LOGGER.error("MethodArgumentNotValidException occurred", exception);

        String messageKey = exception.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("validation.default");

        String errorMessage = environment.getProperty(messageKey, "Validation error");

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(errorMessage);
        errorInfo.setErrorCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> handleConstraintViolationException(ConstraintViolationException exception) {
        LOGGER.error("ConstraintViolationException occurred", exception);

        String messageKey = exception.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("constraint.violation");

        String errorMessage = environment.getProperty(messageKey, "Constraint violation error");

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(errorMessage);
        errorInfo.setErrorCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGenericException(Exception exception) {
        LOGGER.error("Unhandled exception occurred", exception);

        String errorMessage = environment.getProperty("general.exception", "An unexpected error occurred. Please try again later.");

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorMessage(errorMessage);
        errorInfo.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
