package com.nets.upos.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.nets.upos.commons.logger.Loggable;


@Entity
@Table(name = "paynow_txn_data")
public class PaynowTransactionData implements Loggable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "RETRIEVAL_REF")
	private String retrievalRef;

	@Column(name = "INSTITUTION_CODE")
	private String institutionCode;

	@Column(name = "TID")
	private String tid;

	@Column(name = "TRANSACTION_ID")
	private int transactionId;

	@Column(name = "CREDIT_AMOUNT")
	private BigDecimal creditAmount;

	@Column(name = "CREDIT_CURRENCY")
	private String creditCurrency;

	@Column(name = "CREDITING_TIME")
	private String creditingTime;

	@Column(name = "RECEIVING_ACCOUNT_NUMBER")
	private String receivingAccountNumber;

	@Column(name = "RECEIVING_ACCOUNT_TYPE")
	private String receivingAccountType;

	@Column(name = "MERCHANT_REFERENCE_NUMBER")
	private String merchantReferenceNumber;

	@Column(name = "SENDING_ACCOUNT_BANK")
	private String sendingAccountBank;

	@Column(name = "SENDING_ACCOUNT_NUMBER")
	private String sendingAccountNumber;

	@Column(name = "SENDING_ACCOUNT_NAME")
	private String sendingAccountName;

	@Column(name = "ADDITIONAL_BANK_REFERENCE")
	private String additionalBankReference;
	
	@Column(name = "REVERSAL_REFUND_REFERENCE")
	private String reversalRefundReference;
	

	@Column(name = "CREATION_DATE")
	private LocalDateTime creationDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
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

	public String getReversalRefundReference() {
		return reversalRefundReference;
	}

	public void setReversalRefundReference(String reversalRefundReference) {
		this.reversalRefundReference = reversalRefundReference;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
