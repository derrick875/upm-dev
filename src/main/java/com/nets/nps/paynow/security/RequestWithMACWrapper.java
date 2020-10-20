package com.nets.nps.paynow.security;

import java.util.Objects;

public class RequestWithMACWrapper {

    public static final String BAD_FORMAT = "Bad formmated json mac request.";

    static final String MAC_SEPARATOR = ":";

    private final String payload;
    private final String txnMac;

    public RequestWithMACWrapper(String request) throws IllegalArgumentException {
        int splitPosition = request.lastIndexOf(MAC_SEPARATOR);
        this.payload = request.substring(0, splitPosition);
        this.txnMac = request.substring(splitPosition + 1);

        if (Objects.isNull(payload) || Objects.isNull(txnMac)) {
            throw new IllegalArgumentException(BAD_FORMAT);
        }
    }

    public String getPayload() {
        return payload;
    }

    public String getTxnMac() {
        return txnMac;
    }

}
