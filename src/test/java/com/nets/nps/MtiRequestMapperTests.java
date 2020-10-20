package com.nets.nps;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class MtiRequestMapperTests {

    @Test
    void findCreditNotification() {
        final String mti = "8200";
        final String processingCode = "420000";
        Optional<MtiRequestMapper> request = MtiRequestMapper.find(mti, processingCode);
        assertTrue(request.isPresent());
        assertEquals(request.get(), MtiRequestMapper.CREDIT_NOTIFICATION);
    }

   

    @Test
    void findNothingWithNoMTI() {
        final String mti = null;
        final String processingCode = "420000";
        Optional<MtiRequestMapper> request = MtiRequestMapper.find(mti, processingCode);
        assertFalse(request.isPresent());
    }
    
    @Test
    void findNothingWithNoProcessingCode() {
        final String mti = "8200";
        final String processingCode = null;
        Optional<MtiRequestMapper> request = MtiRequestMapper.find(mti, processingCode);
        assertFalse(request.isPresent());
    }
    
    @Test
    void findNothingWithInvalidMTI() {
        final String mti = "Invalid";
        final String processingCode = "420000";
        Optional<MtiRequestMapper> request = MtiRequestMapper.find(mti, processingCode);
        assertFalse(request.isPresent());
    }
    @Test
    void findNothingWithInvalidProcessingCode() {
        final String mti = "8200";
        final String processingCode = "Invalid";
        Optional<MtiRequestMapper> request = MtiRequestMapper.find(mti, processingCode);
        assertFalse(request.isPresent());
    }
}
