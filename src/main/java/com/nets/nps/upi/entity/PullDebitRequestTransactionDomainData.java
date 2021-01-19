package com.nets.nps.upi.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

public class PullDebitRequestTransactionDomainData implements Loggable{

	@JsonProperty("amount")
	private String amount;
	
	@JsonProperty("amount_currency")
	private String amountCurrency;
	
	@JsonProperty("discount")
	private String discount;
	
	@JsonProperty("fee")
	private String fee;
	
	@JsonProperty("converted_amount")
	private String convertedAmount;
	
	@JsonProperty("convert_currency")
	private String convertCurrency;
	
	@JsonProperty("conversion_rate")
	private String conversionRate;
	
	@JsonProperty("transaction_type")
	private String transactionType;
	
	@JsonProperty("cpmqrpayment_token")
	private String cpmqrpaymentToken;
	
	@JsonProperty("additional_pulldebit_data")
	private Map additionalPulldebitData;
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAmountCurrency() {
		return amountCurrency;
	}

	public void setAmountCurrency(String amountCurrency) {
		this.amountCurrency = amountCurrency;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getConvertedAmount() {
		return convertedAmount;
	}

	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}

	public String getConvertCurrency() {
		return convertCurrency;
	}

	public void setConvertCurrency(String convertCurrency) {
		this.convertCurrency = convertCurrency;
	}

	public String getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(String conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCpmqrpaymentToken() {
		return cpmqrpaymentToken;
	}

	public void setCpmqrpaymentToken(String cpmqrpaymentToken) {
		this.cpmqrpaymentToken = cpmqrpaymentToken;
	}

	public Map getAdditionalPulldebitData() {
		return additionalPulldebitData;
	}

	public void setAdditionalPulldebitData(Map additionalPulldebitData) {
		this.additionalPulldebitData = additionalPulldebitData;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("amount=" + amount);
		buffer.append(", amountCurrency=" + amountCurrency);
		buffer.append(", discount=" + discount);
		buffer.append(", fee=" + fee);
		buffer.append(", convertedAmount=" + convertedAmount);
		buffer.append(", convertCurrency=" + convertCurrency);
		buffer.append(", conversionRate=" + conversionRate);
		buffer.append(", transactionType" + transactionType);
		buffer.append(", cpmqrpaymentToken" + cpmqrpaymentToken);
		buffer.append(", additionalPulldebitData" + additionalPulldebitData);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
