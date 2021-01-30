package com.nets.nps.upi.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalProcessingTransactionInfo implements Serializable {
	
	private static final long serialVersionUID = 6544557100030700265L;

	private String deviceID;
	private String token;
	private String emvCpqrcPayload;
	private String barcodeCpqrcPayload;
	private String trxAmt;
	private String trxCurrency;
	private String merchantName;

	@Override
	public String toString() {
		return "AdditionalProcessingTransactionInfo [deviceID=" + deviceID + ", token=" + token + ", emvCpqrcPayload="
				+ emvCpqrcPayload + ", barcodeCpqrcPayload=" + barcodeCpqrcPayload + ", trxCurrency=" + trxCurrency +", trxAmt=" + trxAmt + ", trxCurrency=" + trxCurrency + ", merchantName="
				+ merchantName + "]";
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
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

	public String getBarcodeCpqrcPayload() {
		return barcodeCpqrcPayload;
	}

	public void setBarcodeCpqrcPayload(String barcodeCpqrcPayload) {
		this.barcodeCpqrcPayload = barcodeCpqrcPayload;
	}

}
