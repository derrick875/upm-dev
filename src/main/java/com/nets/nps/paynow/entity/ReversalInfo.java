package com.nets.nps.paynow.entity;

import java.sql.Timestamp;

public class ReversalInfo {
	
	private Integer transactionId;
//	private Integer orderState;
	private String retrievalRef;
	private String institutionCode;
	private Timestamp bankSettleDate;
	private String creditAmount;
	private String creditCurrency;
	private String creditingTime;
	private String receivingAccountNumber;
	private String receivingAccountType;
	private String sendingAccountBank;
	private String sendingAccountName;
	private String sendingAccountNumber;
	private String merchantReferenceNumber;
	private String additionalBankReference;
	private String paymentType;
	private String tid;
	private String mid;
	private String stan;
	private String approvalCode;
	private String originalRetrievalReference;
	private String transactionDate;
	private String transactionTime;
		
	
	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getOriginalRetrievalReference() {
		return originalRetrievalReference;
	}

	public void setOriginalRetrievalReference(String originalRetrievalReference) {
		this.originalRetrievalReference = originalRetrievalReference;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getCreditingTime() {
		return creditingTime;
	}

	public void setCreditingTime(String creditingTime) {
		this.creditingTime = creditingTime;
	}

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

	public String getSendingAccountBank() {
		return sendingAccountBank;
	}

	public void setSendingAccountBank(String sendingAccountBank) {
		this.sendingAccountBank = sendingAccountBank;
	}

	public String getSendingAccountName() {
		return sendingAccountName;
	}

	public void setSendingAccountName(String sendingAccountName) {
		this.sendingAccountName = sendingAccountName;
	}

	public String getSendingAccountNumber() {
		return sendingAccountNumber;
	}

	public void setSendingAccountNumber(String sendingAccountNumber) {
		this.sendingAccountNumber = sendingAccountNumber;
	}

	public String getMerchantReferenceNumber() {
		return merchantReferenceNumber;
	}

	public void setMerchantReferenceNumber(String merchantReferenceNumber) {
		this.merchantReferenceNumber = merchantReferenceNumber;
	}

	public String getAdditionalBankReference() {
		return additionalBankReference;
	}

	public void setAdditionalBankReference(String additionalBankReference) {
		this.additionalBankReference = additionalBankReference;
	}

	public Timestamp getBankSettleDate() {
		return bankSettleDate;
	}

	public void setBankSettleDate(Timestamp bankSettleDate) {
		this.bankSettleDate = bankSettleDate;
	}

//	public Integer getOrderState() {
//		return orderState;
//	}
//
//	public void setOrderState(Integer orderState) {
//		this.orderState = orderState;
//	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getRetrievalRef() {
		return retrievalRef;
	}

	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

}
