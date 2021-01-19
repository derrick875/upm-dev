package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

public class CpmTransactionStatusNotificationRequestTransactionDomainData implements Loggable{
	
	@JsonProperty("payment_receipt_data")
	private String paymentReceiptData;
	
	@JsonProperty("cpmqrpayment_token")
	private String cpmqrpaymentToken;

	public String getPaymentReceiptData() {
		return paymentReceiptData;
	}

	public void setPaymentReceiptData(String paymentReceiptData) {
		this.paymentReceiptData = paymentReceiptData;
	}

	public String getCpmqrpaymentToken() {
		return cpmqrpaymentToken;
	}

	public void setCpmqrpaymentToken(String cpmqrpaymentToken) {
		this.cpmqrpaymentToken = cpmqrpaymentToken;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("paymentReceiptData=" + paymentReceiptData);
		buffer.append("cpmqrpaymentToken=" + cpmqrpaymentToken);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
