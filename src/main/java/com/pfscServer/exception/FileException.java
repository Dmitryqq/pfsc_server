package com.pfscServer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "File exception!")
public class FileException extends Exception {
    public FileException(String message) {
        super(message);
    }
}
