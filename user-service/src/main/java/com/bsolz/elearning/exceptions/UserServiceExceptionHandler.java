package com.bsolz.elearning.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ExpiredCodeException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler({UsernameExistsException.class})
    public ResponseEntity<UserErrorResponse> handleUsernameExistsException(final UsernameExistsException usernameExistsException) {
        return exceptionResponse(usernameExistsException);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<UserErrorResponse> handleSQLException(final SQLException sqlException) {
        return exceptionResponse(sqlException);
    }

    @ExceptionHandler({ExpiredCodeException.class})
    public ResponseEntity<UserErrorResponse> handleExpiredCodeExceptionException(final ExpiredCodeException expiredCodeException) {
        return exceptionResponse(expiredCodeException);
    }

    private ResponseEntity<UserErrorResponse> exceptionResponse(Exception e) {
        switch (e) {
            case UsernameExistsException usernameExistsException -> {
                return ResponseEntity.status(usernameExistsException.statusCode())
                        .body(new UserErrorResponse(usernameExistsException.statusCode(), "User already exists"));
            }
            case SQLException sqlException -> {
                return ResponseEntity.status(sqlException.getErrorCode())
                        .body(new UserErrorResponse(sqlException.getErrorCode(), sqlException.getMessage()));
            }
            case ExpiredCodeException expiredCodeException -> {
                return ResponseEntity.status(expiredCodeException.statusCode())
                        .body(new UserErrorResponse(expiredCodeException.statusCode(), "Invalid code provided, please request a code again"));
            }
            default -> {
                return ResponseEntity.status(500)
                        .body(new UserErrorResponse(500, "Internal server error"));
            }
        }
    }
    public record UserErrorResponse(int statusCode, String message
    ) {}
}
