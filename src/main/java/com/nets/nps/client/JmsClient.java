package com.nets.nps.client;

import org.springframework.jms.JmsException;

public interface JmsClient<I, M> {

    void put(I request) throws JmsException;

    void onMessage(M message);
}
