package com.nets.nps;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.nets.nps.paynow.entity.OrderRequest;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.OrderQueryRequest;
import com.nets.nps.paynow.entity.ReversalRequest;
import com.nets.nps.paynow.entity.RefundReversalRequest;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.core.entity.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Optional;

public enum MtiRequestMapper {

    CREDIT_NOTIFICATION("8200", "420000", "CreditNotification", CreditNotificationRequest.class),
	ORDER("0200","995000","Order",OrderRequest.class),
	ORDER_QUERY("0100","335000","OrderQuery",OrderQueryRequest.class),
	ORDER_REVERSAL("0400","995000","Reversal",ReversalRequest.class),
	REFUNDREVERSAL("8400","440000","RefundReversal",RefundReversalRequest.class),
	QR_GENERATION("0300", "700000", "QRGeneration", QrcGenerationRequest.class);
    
	// TODO consider mti to be number, present as 4 bit String
    private final int MTI_LENGTH = 4;

    private final String mti;
    private final String processCode;
    private final String requestType;
    private final Class<? extends Request> clzRequest;
    private final String responseMti;

    MtiRequestMapper(final String mti, final String processCode, final String requestType, final Class<? extends Request> clzRequest) {

        assert (mti.length() == this.MTI_LENGTH);
        assert (NumberUtils.isParsable(mti));

        this.mti = mti;
        this.processCode = processCode;
        this.requestType = requestType;
        this.clzRequest = clzRequest;
        this.responseMti = this.defaultReponseMti(mti);
    }

    private final static Table<String, String, MtiRequestMapper> table = HashBasedTable.create();

    static {
        for (MtiRequestMapper request : MtiRequestMapper.values()) {
            table.put(request.getMti(), request.getProcessCode(), request);
        }
    }

    public static Optional<MtiRequestMapper> find(final String mti, final String processCode) {
        return Optional.ofNullable(table.get(mti, processCode));
    }

    public static String defaultReponseMti(final String mti) {
        final String respMti = String.valueOf(Integer.parseInt(mti) + 10);

        return StringUtils.leftPad(respMti, 4, "0");
    }

    public String getMti() {
        return mti;
    }

    public String getProcessCode() {
        return processCode;
    }
    
    public String getRequestType() {
        return requestType;
    }

    public String getResponseMti() {
        return responseMti;
    }

    public Class<? extends Request> getClzRequest() {
        return clzRequest;
    }
}
