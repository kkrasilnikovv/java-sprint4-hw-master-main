package com.yandex.taskmanager.service;

public class ValidationException extends RuntimeException {
    ValidationException(final String message) {
        super(message);
    }

}
