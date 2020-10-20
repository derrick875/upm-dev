package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

public class TransactionEnquiryResults implements Loggable {

	@JsonProperty("credit_amount")
	private String creditAmount;
	
	@JsonProperty("credit_currency")
	private String creditCurrency;
	
	@JsonProperty("crediting_time")
	private String creditingTime;
	
	@JsonProperty("receiving_account_number")
	private String receivingAccountNumber;
	
	@JsonProperty("receiving_account_type")
	private String receivingAccountType;
	
	@JsonProperty("merchant_reference_number")
	private String merchantReferenceNumber;
	
	@JsonProperty("sending_account_bank")
	private String sendingAccountBank;
	
	@JsonProperty("sending_account_number")
	private String sendingAccountNumber;
	
	@JsonProperty("sending_account_name")
	private String sendingAccountName;
	
	@JsonProperty("original_retrieval_reference")
	private String originalRetrievalReference;
	
	@JsonProperty("additional_bank_reference")
	private String additionalBankReference;
	
	public String getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getCreditCurrency() {
		return creditCurrency;
	}

	public void setCreditCurrency(String creditCurrency) {
		this.creditCurrency = creditCurrency;
	}

	public String getCreditingTime() {
		return creditingTime;
	}

	public void setCreditingTime(String creditingTime) {
		this.creditingTime = creditingTime;
	}

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

	public String getSendingAccountBank() {
		return sendingAccountBank;
	}

	public void setSendingAccountBank(String sendingAccountBank) {
		this.sendingAccountBank = sendingAccountBank;
	}

	public String getSendingAccountNumber() {
		return sendingAccountNumber;
	}

	public void setSendingAccountNumber(String sendingAccountNumber) {
		this.sendingAccountNumber = sendingAccountNumber;
	}

	public String getSendingAccountName() {
		return sendingAccountName;
	}

	public void setSendingAccountName(String sendingAccountName) {
		this.sendingAccountName = sendingAccountName;
	}

	public String getOriginalRetrievalReference() {
		return originalRetrievalReference;
	}

	public void setOriginalRetrievalReference(String originalRetrievalReference) {
		this.originalRetrievalReference = originalRetrievalReference;
	}

	public String getAdditionalBankReference() {
		return additionalBankReference;
	}

	public void setAdditionalBankReference(String additionalBankReference) {
		this.additionalBankReference = additionalBankReference;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(", creditAmount=" + creditAmount);
		buffer.append(", creditCurrency=" + creditCurrency);
		buffer.append(", creditingTime=" + creditingTime);
		buffer.append(", receivingAccountNumber=" + receivingAccountNumber);
		buffer.append(", receivingAccountType=" + receivingAccountType);
		buffer.append(", merchantReferenceNumber=" + merchantReferenceNumber);
		buffer.append(", sendingAccountBank=" + sendingAccountBank);
		buffer.append(", sendingAccountNumber=" + sendingAccountNumber);
		buffer.append(", sendingAccountName=" + sendingAccountName);
		buffer.append(", originalRetrievalReference=" + originalRetrievalReference);
		buffer.append(", additionalBankReference=" + additionalBankReference);
		return buffer.toString(); 
	}

	@Override
	public String logKey() {
		return merchantReferenceNumber;
	}

	
	
}
