package com.nets.nps.paynow.client.bank;

import java.sql.Timestamp;

import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.RefundReversalRequest;
import com.nets.nps.paynow.entity.RefundReversalResponse;
import com.nets.nps.paynow.entity.TransactionEnquiryRequest;
import com.nets.nps.paynow.entity.TransactionEnquiryResponse;
import com.nets.upos.commons.exception.BaseBusinessException;

public interface PaynowBankAdapter {

	public RefundReversalResponse processRefundReversal(RefundReversalRequest refundReversalRequest)
			throws BaseBusinessException;
	
	public RefundReversalResponse processRefundReversalDuetoException(CreditNotificationRequest creditNotificationRequest)
			throws BaseBusinessException;
	
	public Timestamp getSettlementDate();
}
