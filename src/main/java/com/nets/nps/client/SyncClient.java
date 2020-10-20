package com.nets.nps.client;

public interface SyncClient<I, O> {
    O send(I request);
}
