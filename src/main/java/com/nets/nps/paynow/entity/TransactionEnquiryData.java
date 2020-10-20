package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

public class TransactionEnquiryData implements Loggable {

	@JsonProperty("receiving_account_number")
	private String receivingAccountNumber;
	
	@JsonProperty("receiving_account_type")
	private String receivingAccountType;
	
	@JsonProperty("merchant_reference_number")
	private String merchantReferenceNumber;
	
	@JsonProperty("crediting_start_time")
	private String creditingStartTime;
	
	@JsonProperty("crediting_end_time")
	private String creditingEndTime;
	
	public String getReceivingAccountNumber() {
		return receivingAccountNumber;
	}

	public void setReceivingAccountNumber(String receivingAccountNumber) {
		this.receivingAccountNumber = receivingAccountNumber;
	}

	public String getReceivingAccountType() {
		return receivingAccountType;
	}

	public void setReceivingAccountType(String receivingAccountType) {
		this.receivingAccountType = receivingAccountType;
	}

	public String getMerchantReferenceNumber() {
		return merchantReferenceNumber;
	}

	public void setMerchantReferenceNumber(String merchantReferenceNumber) {
		this.merchantReferenceNumber = merchantReferenceNumber;
	}

	public String getCreditingStartTime() {
		return creditingStartTime;
	}

	public void setCreditingStartTime(String creditingStartTime) {
		this.creditingStartTime = creditingStartTime;
	}

	public String getCreditingEndTime() {
		return creditingEndTime;
	}

	public void setCreditingEndTime(String creditingEndTime) {
		this.creditingEndTime = creditingEndTime;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(", receivingAccountNumber" + receivingAccountNumber);
		buffer.append(", receivingAccountType" + receivingAccountType);
		buffer.append(", merchantReferenceNumber" + merchantReferenceNumber);
		buffer.append(", creditingStartTime" + creditingStartTime);
		buffer.append(", creditingEndTime" + creditingEndTime);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return null;
	}
	
	
	
	
}
