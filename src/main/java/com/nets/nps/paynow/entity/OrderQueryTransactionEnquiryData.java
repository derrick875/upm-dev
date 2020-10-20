package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.entity.JsonNpxData;
import com.nets.upos.commons.logger.Loggable;

public class OrderQueryTransactionEnquiryData implements Loggable {
	
	@JsonProperty("original_retrieval_reference")
	private String originalRetrievalReference;
	
	@JsonProperty("retry_attempt")
	private String retryAttempt;
	
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
	
	public String getOriginalRetrievalReference() {
		return originalRetrievalReference;
	}

	public void setOriginalRetrievalReference(String originalRetrievalReference) {
		this.originalRetrievalReference = originalRetrievalReference;
	}

	public String getRetryAttempt() {
		return retryAttempt;
	}

	public void setRetryAttempt(String retryAttempt) {
		this.retryAttempt = retryAttempt;
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
		buffer.append(", originalRetrievalReference=" + originalRetrievalReference);
		buffer.append(", retryAttempt=" + retryAttempt);
		buffer.append(", hostMid=" + hostMid);
		buffer.append(", hostTid=" + hostTid);
		buffer.append(", netsPayNowMpan=" + netsPaynowMpan);
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
