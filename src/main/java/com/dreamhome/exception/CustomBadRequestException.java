package com.dreamhome.exception;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomBadRequestException extends BadRequestException {
    public CustomBadRequestException(String message) {
        super(message);
    }
}
