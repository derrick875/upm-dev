package com.nets.nps.paynow.exception;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.nets.upos.commons.logger.ApsLogger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final ApsLogger logger = new ApsLogger(RestResponseEntityExceptionHandler.class);

    public static final String BAD_REQUEST_MESSAGE = "Invalid incoming request.";

    @Override
    @ExceptionHandler({InvalidTypeIdException.class})
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        logger.error("Not readable http message.", ex);

        return new ResponseEntity<Object>(new InvalidRequestError(BAD_REQUEST_MESSAGE, ex.getRootCause().getMessage()),
                                            headers,
                                            HttpStatus.BAD_REQUEST);
    }

}
