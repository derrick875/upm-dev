package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.entity.JsonNpxData;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class PaymentTransactionDomainData implements Loggable {

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

	@JsonProperty("additional_bank_reference")
	private String additionalBankReference;

	@JsonProperty("payment_amount")
	private String paymentAmount;

	@JsonProperty("payment_currency")
	private String paymentCurrency;

	@JsonProperty("transaction_time")
	private String transactionTime;

	@JsonProperty("transaction_date")
	private String transactionDate;

	@JsonProperty("validity_time")
	private String validityTime;

	@JsonProperty("entry_mode")
	private String entryMode;

	@JsonProperty("condition_code")
	private String conditionCode;

	@JsonProperty("host_mid")
	private String hostMid;

	@JsonProperty("host_tid")
	private String hostTid;

	@JsonProperty("nets_paynow_mpan")
	private String netsPaynowMpan;

	@JsonProperty("bank_merchant_proxy")
	private String bankMerchantProxy;

	@JsonProperty("bank_merchant_proxy_type")
	private String bankMerchantProxyType;

	@JsonProperty("invoice_ref")
	private String invoiceRef;

	@JsonProperty("npx_data")
	private JsonNpxData npxData;

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

	public String getAdditionalBankReference() {
		return additionalBankReference;
	}

	public void setAdditionalBankReference(String additionalBankReference) {
		this.additionalBankReference = additionalBankReference;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getPaymentCurrency() {
		return paymentCurrency;
	}

	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getValidityTime() {
		return validityTime;
	}

	public void setValidityTime(String validityTime) {
		this.validityTime = validityTime;
	}

	public String getEntryMode() {
		return entryMode;
	}

	public void setEntryMode(String entryMode) {
		this.entryMode = entryMode;
	}

	public String getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(String conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getHostMid() {
		return hostMid;
	}

	public void setHostMid(String hostMid) {
		this.hostMid = hostMid;
	}

	public String getHostTid() {
		return hostTid;
	}

	public void setHostTid(String hostTid) {
		this.hostTid = hostTid;
	}

	public String getNetsPaynowMpan() {
		return netsPaynowMpan;
	}

	public void setNetsPaynowMpan(String netsPaynowMpan) {
		this.netsPaynowMpan = netsPaynowMpan;
	}

	public String getBankMerchantProxy() {
		return bankMerchantProxy;
	}

	public void setBankMerchantProxy(String bankMerchantProxy) {
		this.bankMerchantProxy = bankMerchantProxy;
	}

	public String getBankMerchantProxyType() {
		return bankMerchantProxyType;
	}

	public void setBankMerchantProxyType(String bankMerchantProxyType) {
		this.bankMerchantProxyType = bankMerchantProxyType;
	}

	public String getInvoiceRef() {
		return invoiceRef;
	}

	public void setInvoiceRef(String invoiceRef) {
		this.invoiceRef = invoiceRef;
	}

	public JsonNpxData getNpxData() {
		return npxData;
	}

	public void setNpxData(JsonNpxData npxData) {
		this.npxData = npxData;
	}

	@Override
	public String log() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("creditAmount=" + creditAmount);
		buffer.append(", creditCurrency=" + creditCurrency);
		buffer.append(", creditingTime=" + creditingTime);
		buffer.append(", receivingAccountNumber=" + receivingAccountNumber);
		buffer.append(", receivingAccountType=" + receivingAccountType);
		buffer.append(", merchantReferenceNumber=" + merchantReferenceNumber);
		buffer.append(", sendingAccountBank=" + sendingAccountBank);
		buffer.append(", sendingAccountNumber=" + sendingAccountNumber);
		buffer.append(", sendingAccountName=" + sendingAccountName);
		buffer.append(", additionalBankReference=" + additionalBankReference);
		buffer.append(", paymentAmount=" + paymentAmount);
		buffer.append(", paymentCurrency=" + paymentCurrency);
		buffer.append(", transactionTime=" + transactionTime);
		buffer.append(", transactionDate=" + transactionDate);
		buffer.append(", validityTime=" + validityTime);
		buffer.append(", entryMode=" + entryMode);
		buffer.append(", conditionCode=" + conditionCode);
		buffer.append(", hostMid=" + hostMid);
		buffer.append(", hostTid=" + hostTid);
		buffer.append(", netsPaynowMpan=" + netsPaynowMpan);
		buffer.append(", bankMerchantProxy=" + bankMerchantProxy);
		buffer.append(", bankMerchantProxyType=" + bankMerchantProxyType);
		buffer.append(", invoiceRef=" + invoiceRef);
		buffer.append(", npxData=" + npxData);
		
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return merchantReferenceNumber;
	}

}
