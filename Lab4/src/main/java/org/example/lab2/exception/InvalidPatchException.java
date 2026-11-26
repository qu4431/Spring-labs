package org.example.lab2.exception;

public class InvalidPatchException extends RuntimeException {
    public InvalidPatchException(String message) {
        super(message);
    }
}