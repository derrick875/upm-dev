package com.nets.nps.upi.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UpiProxyRequest implements Serializable {

	private static final long serialVersionUID = -6076742451203677042L;

	private String bankType;
	private Integer bankId;
	private String instCode;
	private String transactionType;
	private String upiProxyRequestJsonData;
	private String upiProxyResponseJsonData;
	private String correlationId;

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getUpiProxyRequestJsonData() {
		return upiProxyRequestJsonData;
	}

	public void setUpiProxyRequestJsonData(String upiProxyRequestJsonData) {
		this.upiProxyRequestJsonData = upiProxyRequestJsonData;
	}

	public String getUpiProxyResponseJsonData() {
		return upiProxyResponseJsonData;
	}

	public void setUpiProxyResponseJsonData(String upiProxyResponseJsonData) {
		this.upiProxyResponseJsonData = upiProxyResponseJsonData;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getInstCode() {
		return instCode;
	}

	public void setInstCode(String instCode) {
		this.instCode = instCode;
	}

	@Override
	public String toString() {
		return "UpiProxyRequest [bankType=" + bankType + ", bankId=" + bankId + ", instCode=" + instCode
				+ ", transactionType=" + transactionType + ", upiProxyRequestJsonData=" + upiProxyRequestJsonData
				+ ", upiProxyResponseJsonData=" + upiProxyResponseJsonData + ", correlationId=" + correlationId + "]";
	}
}
