package org.quiz.userservice.exceptions;

import org.springframework.http.HttpStatus;

public class KeycloakException extends UserServiceException {
    public KeycloakException(HttpStatus httpStatus) {
        super(httpStatus);
    }

    public KeycloakException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public KeycloakException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause, httpStatus);
    }

    public KeycloakException(Throwable cause, HttpStatus httpStatus) {
        super(cause, httpStatus);
    }

    public KeycloakException(String message,
                             Throwable cause,
                             boolean enableSuppression,
                             boolean writableStackTrace,
                             HttpStatus httpStatus) {
        super(message, cause, enableSuppression, writableStackTrace, httpStatus);
    }

}
