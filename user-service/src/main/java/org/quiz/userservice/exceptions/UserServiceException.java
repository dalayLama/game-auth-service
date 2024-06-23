package org.quiz.userservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;

@Getter
public class UserServiceException extends RuntimeException implements ErrorResponse {

    private final HttpStatus statusCode;

    public UserServiceException(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public UserServiceException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public UserServiceException(String message, Throwable cause, HttpStatus statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public UserServiceException(Throwable cause, HttpStatus statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public UserServiceException(String message,
                                Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace,
                                HttpStatus statusCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(statusCode, getMessage());
    }

}
