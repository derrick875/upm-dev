package com.nets.nps.paynow.exception;

import com.nets.upos.commons.logger.ApsLogger;
import org.springframework.util.ErrorHandler;

public class JmsExceptionHandler implements ErrorHandler {

    private static final ApsLogger logger = new ApsLogger(JmsExceptionHandler.class);

    @Override
    public void handleError(Throwable t) {
        logger.error("Error when processing message from queue: ", t);
    }
}
