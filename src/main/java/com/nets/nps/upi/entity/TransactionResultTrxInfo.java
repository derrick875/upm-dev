package com.nets.nps.upi.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResultTrxInfo implements Serializable {
	
	private static final long serialVersionUID = 5295505157056819724L;
	
	private String deviceID;
	private String token;
	private String trxMsgType;
	private String origMsgId;
	private String origTrxType;
	private String accountInfo;
	private String trxAmt;
	private String trxCurrency;
	private String discountDetails;
	private String originalAmount;
	private String trxNote;
	private String merchantName;
	private String qrcVoucherNo;
	private String paymentStatus;
	private String rejectionReason;
	private String referNo;
	
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceId(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTrxMsgType() {
		return trxMsgType;
	}

	public void setTrxMsgType(String trxMsgType) {
		this.trxMsgType = trxMsgType;
	}

	public String getOrigMsgId() {
		return origMsgId;
	}

	public void setOrigMsgId(String origMsgId) {
		this.origMsgId = origMsgId;
	}

	public String getOrigTrxType() {
		return origTrxType;
	}

	public void setOrigTrxType(String origTrxType) {
		this.origTrxType = origTrxType;
	}

	public String getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(String accountInfo) {
		this.accountInfo = accountInfo;
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

	public String getDiscountDetails() {
		return discountDetails;
	}

	public void setDiscountDetails(String discountDetails) {
		this.discountDetails = discountDetails;
	}

	public String getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(String originalAmount) {
		this.originalAmount = originalAmount;
	}

	public String getTrxNote() {
		return trxNote;
	}

	public void setTrxNote(String trxNote) {
		this.trxNote = trxNote;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getQrcVoucherNo() {
		return qrcVoucherNo;
	}

	public void setQrcVoucherNo(String qrcVoucherNo) {
		this.qrcVoucherNo = qrcVoucherNo;
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

	public String getReferNo() {
		return referNo;
	}

	public void setReferNo(String referNo) {
		this.referNo = referNo;
	}

	@Override
	public String toString() {
		return "TransactionResultTrxInfo [deviceID=" + deviceID + ", token=" + token + ",trxMsgType=" + trxMsgType + ", origMsgId=" + origMsgId + ", origTrxType=" + origTrxType + ", accountInfo=" + accountInfo
				+ ", trxAmt=" + trxAmt + ", trxCurrency=" + trxCurrency + ",discountDetails=" + discountDetails + ", originalAmount=" + originalAmount + ", trxNote=" + trxNote + ", merchantName=" + merchantName
				+ ", qrcVoucherNo=" + qrcVoucherNo + ", paymentStatus=" + paymentStatus + ", rejectionReason=" + rejectionReason + ", referNo=" + referNo + "]";
	}
	
}
