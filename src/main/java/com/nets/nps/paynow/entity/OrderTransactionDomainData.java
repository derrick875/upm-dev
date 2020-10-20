package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.entity.JsonNpxData;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class OrderTransactionDomainData implements Loggable {

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

	@JsonProperty("merchant_reference_number")
	private String merchantReferenceNumber;

	@JsonProperty("invoice_ref")
	private String invoiceRef;

	@JsonProperty("npx_data")
	private JsonNpxData npxData;

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

	public String getMerchantReferenceNumber() {
		return merchantReferenceNumber;
	}

	public void setMerchantReferenceNumber(String merchantReferenceNumber) {
		this.merchantReferenceNumber = merchantReferenceNumber;
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
		buffer.append(", merchantReferenceNumber=" + merchantReferenceNumber);
		buffer.append(", invoiceRef=" + invoiceRef);
		buffer.append(", npxData=" + npxData);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return netsPaynowMpan;
	}

}
