package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AdditionalProcessingResultTransactionInfo {
	
	@JsonProperty("deviceID")
	private String deviceID;
	
	@JsonProperty("userID")
	private String userID;
	
	@JsonProperty("token")
	private String token;
	
	@JsonProperty("emvCpqrcPayload")
	private String emvCpqrcPayload;
	
	@JsonProperty("barcodeCpqrcPayload")
	private String barcodeCpqrcPayload;
	
	@JsonProperty("trxAmt")
	private String trxAmt;
	
	@JsonProperty("trxCurrency")
	private String trxCurrency;
	
	@JsonProperty("merchantName")
	private String merchantName;
	
	@JsonProperty("paymentStatus")
	private String paymentStatus;
	
	@JsonProperty("rejectionReason")
	private String rejectionReason;

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmvCpqrcPayload() {
		return emvCpqrcPayload;
	}

	public void setEmvCpqrcPayload(String emvCpqrcPayload) {
		this.emvCpqrcPayload = emvCpqrcPayload;
	}

	public String getBarcodeCpqrcPayload() {
		return barcodeCpqrcPayload;
	}

	public void setBarcodeCpqrcPayload(String barcodeCpqrcPayload) {
		this.barcodeCpqrcPayload = barcodeCpqrcPayload;
	}

	public String getTrxAmt() {
		return trxAmt;
	}

	public void setTrxAmt(String trxAmt) {
		this.trxAmt = trxAmt;
	}

	public String getTrxCurrency() {
		return trxCurrency;
	}

	public void setTrxCurrency(String trxCurrency) {
		this.trxCurrency = trxCurrency;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	@Override
	public String toString() {
		return "AdditionalProcessingResultTransactionInfo [deviceID=" + deviceID + ", userID=" + userID + ", token="
				+ token + ", emvCpqrcPayload=" + emvCpqrcPayload + ", barcodeCpqrcPayload=" + barcodeCpqrcPayload
				+ ", trxAmt=" + trxAmt + ", trxCurrency=" + trxCurrency + ", merchantName=" + merchantName
				+ ", paymentStatus=" + paymentStatus + ", rejectionReason=" + rejectionReason + "]";
	}
}
