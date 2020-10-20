package com.nets.nps.client.impl;

import java.beans.Transient;
import java.util.function.BiFunction;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.upos.commons.logger.Loggable;


@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class TransactionStateMsg implements Loggable {
	
	@JsonProperty("SOF_uri")
	private String sofUri;
	
	@JsonProperty("retrieval_ref")
	private String retrievalRef;
	
	@JsonProperty("sof_account_id")
	private String sofAccountId;
	
	@JsonProperty("amount")
	private String amount;
	
	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("stan")
	private String stan;
	
	@JsonProperty("transaction_time")
	private String transactionTime;
	
	@JsonProperty("transaction_date")
	private String transactionDate;
	
	@JsonProperty("settlement_date")
	private String settlementDate;
	
	@JsonProperty("institution_code")
	private String institutionCode;
	
	@JsonProperty("condition_code")
	private String conditionCode;
	
	@JsonProperty("approval_code")
	private String approvalCode;
	
	@JsonProperty("response_code")
	private String responseCode;
	
	@JsonProperty("host_tid")
	private String hostTid;
	
	@JsonProperty("host_mid")
	private String hostMid;
	
	@JsonProperty("acceptor_name")
	private String acceptorName;
	
	@JsonProperty("acceptor_location")
	private String acceptorLocation;
	
	@JsonProperty("acquirer_institution_code")
	private String acquirerInstitutionCode;
	
	@JsonProperty("user_data")
	private String userData;
	
	@JsonProperty("transaction_id")
	private String transactionId;
	
	@JsonProperty("txn_identifier")
	private String txnIdentifier;
	
	@JsonProperty("transaction_type")
	private String transactionType;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("payment_type")
	private String paymentType;
	
	@JsonProperty("invoice_ref")
	private String invoice;
	
	@JsonProperty("loyalty_data")
	private String loyaltyData;
	
	@JsonProperty("fx_qualification_data")
	private String fxQualificationData;
	
	@JsonProperty("foreign_acquirer_data")
	private String foreignAcquirerData;

	public String getSofUri() {
		return sofUri;
	}

	public void setSofUri(String sofUri) {
		this.sofUri = sofUri;
	}

	public String getSofAccountId() {
		return sofAccountId;
	}

	public String getRetrievalRef() {
		return retrievalRef;
	}

	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
	}

	public void setSofAccountId(String sofAccountId) {
		this.sofAccountId = sofAccountId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
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

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(String conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getHostTid() {
		return hostTid;
	}

	public void setHostTid(String hostTid) {
		this.hostTid = hostTid;
	}

	public String getHostMid() {
		return hostMid;
	}

	public void setHostMid(String hostMid) {
		this.hostMid = hostMid;
	}

	public String getAcceptorName() {
		return acceptorName;
	}

	public void setAcceptorName(String acceptorName) {
		this.acceptorName = acceptorName;
	}

	public String getAcceptorLocation() {
		return acceptorLocation;
	}

	public void setAcceptorLocation(String acceptorLocation) {
		this.acceptorLocation = acceptorLocation;
	}

	public String getAcquirerInstitutionCode() {
		return acquirerInstitutionCode;
	}

	public void setAcquirerInstitutionCode(String acquirerInstitutionCode) {
		this.acquirerInstitutionCode = acquirerInstitutionCode;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTxnIdentifier() {
		return txnIdentifier;
	}

	public void setTxnIdentifier(String txnIdentifier) {
		this.txnIdentifier = txnIdentifier;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getLoyaltyData() {
		return loyaltyData;
	}

	public void setLoyaltyData(String loyaltyData) {
		this.loyaltyData = loyaltyData;
	}

	public String getFxQualificationData() {
		return fxQualificationData;
	}

	public void setFxQualificationData(String fxQualificationData) {
		this.fxQualificationData = fxQualificationData;
	}

	public String getForeignAcquirerData() {
		return foreignAcquirerData;
	}

	public void setForeignAcquirerData(String foreignAcquirerData) {
		this.foreignAcquirerData = foreignAcquirerData;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append("sofUri=" + sofUri);
		buffer.append(", amount=" + amount);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", stan=" + stan);
		buffer.append(", transactionTime=" + transactionTime);
		buffer.append(", transactionDate=" + transactionDate);
		buffer.append(", settlementDate=" + settlementDate);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", conditionCode=" + conditionCode);
		buffer.append(", responseCode=" + responseCode);
		buffer.append(", hostTid=" + hostTid);
		buffer.append(", hostMid=" + hostMid);
		buffer.append(", transactionType=" + transactionType);
		buffer.append(", status=" + status);
		buffer.append(", paymentType=" + paymentType);
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public String log() {
		return toString();
	}

	@Override
	public String logKey() {
		return stan;
	}
	
	@Transient
	public String getTopicSuffix() {
		StringBuffer topicSuffix = new StringBuffer();
		appendNilIfEmpty.apply(this.getTransactionType(), topicSuffix);
		appendNilIfEmpty.apply(this.getInstitutionCode(), topicSuffix);
		appendNilIfEmpty.apply(this.getStatus(), topicSuffix);
		appendNilIfEmpty.apply(this.getResponseCode(), topicSuffix);
		appendNilIfEmpty.apply(this.getAcquirerInstitutionCode(), topicSuffix);
		appendNilIfEmpty.apply(this.getTransactionId(), topicSuffix);

		return topicSuffix.toString();
	}

	private static BiFunction<String, StringBuffer, StringBuffer> appendNilIfEmpty = (s, sb) -> {
		if (StringUtils.isEmpty(s)) {
			return sb.append('/' + "NIL");
		} else {
			return sb.append('/' + s);
		}
	};
}
