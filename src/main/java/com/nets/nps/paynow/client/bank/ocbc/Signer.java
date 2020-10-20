package com.nets.nps.paynow.client.bank.ocbc;

import com.nets.upos.commons.logger.ApsLogger;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Objects;

@Component
public class Signer {

    private static final ApsLogger logger = new ApsLogger(Signer.class);

    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    @Autowired
    KeyStore keyStore;

    @Autowired
    KeyStore trustStore;

    @Value("${bank.ocbc.api.signkey}")
    String signKey;
    @Value("${client.ssl.key-store.key.password}")
    String keyPassword;
    @Value("${bank.ocbc.api.signcert}")
    String signCert;

    public Base64 sign(String input)  throws Exception {
        assert !Objects.isNull(keyStore);

        try {
            logger.info("SignKey: " + signKey);
            PrivateKey privateSignKey =
                    (PrivateKey) keyStore.getKey(signKey, keyPassword.toCharArray());
            logger.info("Sign input: " + input);

            Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(privateSignKey);
            privateSignature.update(input.getBytes("UTF-8"));
            byte[] s = privateSignature.sign();

            return Base64.encode(s);
        } catch (Exception e) {
            logger.error("Exception when sign the input.", e);
            throw e;
        }
    }

    public boolean verify(String input, String signature) {
        assert !Objects.isNull(trustStore);

        try {
            Certificate certificate = trustStore.getCertificate(signCert);
            PublicKey publicSignKey = certificate.getPublicKey();

            Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            publicSignature.initVerify(publicSignKey);
            publicSignature.update(input.getBytes("UTF-8"));
            return publicSignature.verify(Base64URL.from(signature).decode());
        } catch (Exception e) {
            logger.error("Exception when veriry the signature.", e);
            return false;
        }
    }
}
