package com.saburi.smartcoder.base.exceptions;

import com.saburi.smartcoder.base.ResponseObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObj> handleAllExceptions(Exception ex, WebRequest request) {
       log.error("Error: ", ex);
        return new ResponseEntity<>(ResponseObj
                .builder()
                .success(false)
                .details(request.getDescription(false))
                .message("Unknown error. Please try again later")
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Add more specific exception handlers as needed
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseObj> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Error: ", ex);
        return new ResponseEntity<>(ResponseObj
                .builder()
                .success(false)
                .details(ex.getMessage())
                .message("Unknown error. Please try again later")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KnownException.class)
    public ResponseEntity<ResponseObj> handleKnowException(KnownException ex, WebRequest request) {
        log.error("Error: ", ex);
        return new ResponseEntity<>(ResponseObj
                .builder()
                .success(false)
                .details(request.getDescription(false))
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseObj> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        log.error("Error: ", ex);
        return new ResponseEntity<>(ResponseObj
                .builder()
                .success(false)
                .details(request.getDescription(false))
                .message(ex.getMessage())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseObj> httpMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("Error: ", ex);
        return new ResponseEntity<>(ResponseObj
                .builder()
                .success(false)
                .details(ex.getMessage())
                .message(ex.getMessage())
                .build(), HttpStatus.METHOD_NOT_ALLOWED);
    }

}
