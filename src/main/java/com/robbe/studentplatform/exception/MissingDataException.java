package com.robbe.studentplatform.exception;

import java.util.function.Supplier;

public class MissingDataException extends RuntimeException {
    public MissingDataException() {
        super();
    }

    public MissingDataException(String message) {
        super(message);
    }
}
