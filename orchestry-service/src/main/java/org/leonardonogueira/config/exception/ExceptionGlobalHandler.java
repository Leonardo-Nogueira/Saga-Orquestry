package org.leonardonogueira.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleException(ValidationException e) {
        var detail= new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return new ResponseEntity<>(detail, HttpStatus.BAD_REQUEST);
    }

}
