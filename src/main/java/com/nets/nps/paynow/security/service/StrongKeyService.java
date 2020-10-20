package com.nets.nps.paynow.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.nets.nps.paynow.security.DukptDecryptResponse;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.sacrypt.hsm.TerminalCryptoHSMClient;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class StrongKeyService implements HSMService {

    private static final ApsLogger logger = new ApsLogger(StrongKeyService.class);

    @Value("${hsm.strongkey.dukpt.algorithm}")
    private String algorithm;

    @Value("${hsm.strongkey.dukpt.decrypt:false}")
    boolean enableDecrypt;

    @Value("${hsm.strongkey.mac.validation:false}")
    boolean enableMacValidation;

    @Autowired
    TerminalCryptoHSMClient strongKeyClient;

    @Autowired
    ObjectMapper jacksonObjectMapper;

    @Override
    public boolean verify(String ksn, String payload, String mac) throws Exception {
        return this.verify(ksn, payload, mac, false);
    }

    @Override
    public boolean verify(String ksn, String payload, String mac, boolean defaultEnabled) throws Exception {
        if (defaultEnabled || enableMacValidation) {
            logger.info("Start MAC verification");
            if (mac != null) {
                String hsmMAC = strongKeyClient.doDukptCBCMac(null, ksn, Strings.padEnd(payload, 8, '0'), algorithm);
                logger.debug("MAC calculated by HSM: " + hsmMAC);
                return mac.equalsIgnoreCase(hsmMAC);
            } else {
                logger.info("Invalid diversified MAC key.");
                return false;
            }
        } else {
            logger.info("Skipped MAC Verification!!!!!");
            return true;
        }
    }

    @Override
    public String decrypt(String ksn, InputStream input) throws Exception {
        byte[] raw = ByteStreams.toByteArray(input);
        String payload = Hex.encodeHexString(raw);
        String sha256 = DigestUtils.sha256Hex(payload);
        logger.info("Raw byte array in hex: " + payload + ", SHA: " + sha256);
        return decrypt(ksn, payload, false);
    }

    @Override
    public String decrypt(String ksn, String payload) throws Exception {
        return this.decrypt(ksn, payload, false);
    }

    @Override
    public String decrypt(String ksn, String payload, boolean defaultEnabled) throws Exception {
        if (defaultEnabled || enableDecrypt) {
            logger.info("Start dukpt decryption");
            DukptDecryptResponse response =
                    jacksonObjectMapper.readValue(strongKeyClient.doDukptCrypto(null, ksn, payload, algorithm),
                            DukptDecryptResponse.class);
            String hex = response.getPlaintext();
            logger.info("Decrypted hex string: " + hex);
            return new String(Hex.decodeHex(hex), "UTF-8");
        } else {
            logger.info("Skipped dukpt decryption!!!!!");
            return payload;
        }
    }
}
