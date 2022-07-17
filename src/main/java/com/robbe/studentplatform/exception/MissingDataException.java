package com.robbe.studentplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Supplier;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingDataException extends RuntimeException {
    public MissingDataException() {
        super();
    }

    public MissingDataException(String message) {
        super(message);
    }
}
