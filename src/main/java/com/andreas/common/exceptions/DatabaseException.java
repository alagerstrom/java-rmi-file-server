package com.andreas.common.exceptions;

public class DatabaseException extends Exception{
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
