package com.nets.nps.paynow.security.service;

import java.io.InputStream;

public interface HSMService {

    boolean verify(String ksn, String payload, String mac) throws Exception;
    boolean verify(String ksn, String payload, String mac, boolean defaultEnabled) throws Exception;

    String decrypt(String ksn, String payload) throws Exception;
    String decrypt(String ksn, InputStream input) throws Exception;
    String decrypt(String ksn, String payload, boolean defaultEnabled) throws Exception;

}
