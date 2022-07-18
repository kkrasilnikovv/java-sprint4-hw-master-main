package com.yandex.taskmanager.api;

public class StatusCodeException extends RuntimeException {
    StatusCodeException(final String message) {
        super(message);
    }

}
