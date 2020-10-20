package com.nets.nps.paynow.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CreditNotificationInfo implements Serializable {
	private Integer posId;
	private int acqMerAccountId;
	private int acquirerId;
	private int cardTypeId;
	private int acqMerMappingId;
	private String paymentType;
	private String tid;
	private String mid;
	private String approvalCode;
	private String ecMid;
	private BigDecimal amount;
	private String bankTid;
	private int transactionId;
	private String stan;
	private Timestamp bankSettleDate;
	private boolean acquirerActiveStatus;
	private boolean amaActiveStatus;
	private boolean posActiveStatus;
	private boolean merchantActiveStatus;
	private boolean timedOutStatus;
	private Integer originalOrderState;
	private String merchantRefNumber;
	private String transactionDate;
	private String transactionTime;
	
	public Integer getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public int getAcqMerAccountId() {
		return acqMerAccountId;
	}

	public void setAcqMerAccountId(int acqMerAccountId) {
		this.acqMerAccountId = acqMerAccountId;
	}

	public int getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(int acquirerId) {
		this.acquirerId = acquirerId;
	}

	public int getCardTypeId() {
		return cardTypeId;
	}

	public void setCardTypeId(int cardTypeId) {
		this.cardTypeId = cardTypeId;
	}

	public int getAcqMerMappingId() {
		return acqMerMappingId;
	}

	public void setAcqMerMappingId(int acqMerMappingId) {
		this.acqMerMappingId = acqMerMappingId;
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

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getEcMid() {
		return ecMid;
	}

	public void setEcMid(String ecMid) {
		this.ecMid = ecMid;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getBankTid() {
		return bankTid;
	}

	public void setBankTid(String bankTid) {
		this.bankTid = bankTid;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public Timestamp getBankSettleDate() {
		return bankSettleDate;
	}

	public void setBankSettleDate(Timestamp bankSettleDate) {
		this.bankSettleDate = bankSettleDate;
	}

	public boolean isAcquirerActiveStatus() {
		return acquirerActiveStatus;
	}

	public void setAcquirerActiveStatus(boolean acquirerActiveStatus) {
		this.acquirerActiveStatus = acquirerActiveStatus;
	}

	public boolean isAmaActiveStatus() {
		return amaActiveStatus;
	}

	public void setAmaActiveStatus(boolean amaActiveStatus) {
		this.amaActiveStatus = amaActiveStatus;
	}

	public boolean isPosActiveStatus() {
		return posActiveStatus;
	}

	public void setPosActiveStatus(boolean posActiveStatus) {
		this.posActiveStatus = posActiveStatus;
	}

	public boolean isMerchantActiveStatus() {
		return merchantActiveStatus;
	}

	public void setMerchantActiveStatus(boolean merchantActiveStatus) {
		this.merchantActiveStatus = merchantActiveStatus;
	}
	
	public boolean isTimedOutStatus() {
		return timedOutStatus;
	}

	public void setTimedOutStatus(boolean timedOutStatus) {
		this.timedOutStatus = timedOutStatus;
	}

	public Integer getOriginalOrderState() {
		return originalOrderState;
	}

	public void setOriginalOrderState(Integer originalOrderState) {
		this.originalOrderState = originalOrderState;
	}

	public String getMerchantRefNumber() {
		return merchantRefNumber;
	}

	public void setMerchantRefNumber(String merchantRefNumber) {
		this.merchantRefNumber = merchantRefNumber;
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
