package com.pfscServer.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(FileException.class)
    protected ResponseEntity<AwesomeException> handleFileException(FileException ex) {
        return new ResponseEntity<>(new AwesomeException(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Data
    private static class AwesomeException {
        private String message;

        public AwesomeException(String message) {
            this.message = message;
        }
    }

    //other exception handlers below

}