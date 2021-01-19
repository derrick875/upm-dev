package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class UpiQrcGenerationRequestTransactionRequest implements Loggable {

	@JsonProperty("deviceID")
	private String deviceID;
	
	@JsonProperty("userID")
	private String userID;
	
	@JsonProperty("token")
	private String token;
	
	@JsonProperty("trxLimit")
	private String trxLimit;
	
	@JsonProperty("cvmLimit")
	private String cvmLimit;
	
	@JsonProperty("limitCurrency")
	private String limitCurrency;
	
	@JsonProperty("cpqrcNo")
	private String cpqrcNo;
	
	@JsonProperty("couponInfo")
	private String couponInfo;
	
//	@JsonProperty("riskInfo")
//	private RiskInfo riskInfo; 

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

	public String getTrxLimit() {
		return trxLimit;
	}

	public void setTrxLimit(String trxLimit) {
		this.trxLimit = trxLimit;
	}

	public String getCvmLimit() {
		return cvmLimit;
	}

	public void setCvmLimit(String cvmLimit) {
		this.cvmLimit = cvmLimit;
	}

	public String getLimitCurrency() {
		return limitCurrency;
	}

	public void setLimitCurrency(String limitCurrency) {
		this.limitCurrency = limitCurrency;
	}

	public String getCpqrcNo() {
		return cpqrcNo;
	}

	public void setCpqrcNo(String cpqrcNo) {
		this.cpqrcNo = cpqrcNo;
	}

	public String getCouponInfo() {
		return couponInfo;
	}

	public void setCouponInfo(String couponInfo) {
		this.couponInfo = couponInfo;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("deviceID=" + deviceID);
		buffer.append(", userID=" + userID);
		buffer.append(", token=" + token);
		buffer.append(", trxLimit=" + trxLimit);
		buffer.append(", cvmLimit=" + cvmLimit);
		buffer.append(", limitCurrency=" + limitCurrency);
		buffer.append(", cpqrcNo=" + cpqrcNo);
		return buffer.toString();
	}
	
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
