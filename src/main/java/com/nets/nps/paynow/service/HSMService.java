package com.nets.nps.paynow.service;

public interface HSMService {

    boolean verify(String payload, String mac) throws Exception;
    String decrypt(String payload, String ksm) throws Exception;

}
