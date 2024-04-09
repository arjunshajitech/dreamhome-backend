package com.dreamhome.exception;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomUnauthorizedException extends BadRequestException {
    public CustomUnauthorizedException(String message) {
        super(message);
    }
}
