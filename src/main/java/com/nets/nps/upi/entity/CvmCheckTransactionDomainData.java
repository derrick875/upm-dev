package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class CvmCheckTransactionDomainData  implements Loggable{

	@JsonProperty("selected_qrpayload")
	private String selectedQrPayLoad;

	@JsonProperty("transaction_amount")
	private String transactionAmount;

	@JsonProperty("amount_currency")
	private String amountCurrency;

	@JsonProperty("merchant_name")
	private String merchantName;

	@JsonProperty("cpmqrpayment_token")
	private String cpmQrPaymentToken;
	
	public String getSelectedQrPayLoad() {
		return selectedQrPayLoad;
	}

	public void setSelectedQrPayLoad(String selectedQrPayLoad) {
		this.selectedQrPayLoad = selectedQrPayLoad;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getAmountCurrency() {
		return amountCurrency;
	}

	public void setAmountCurrency(String amountCurrency) {
		this.amountCurrency = amountCurrency;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getCpmQrPaymentToken() {
		return cpmQrPaymentToken;
	}

	public void setCpmQrPaymentToken(String cpmQrPaymentToken) {
		this.cpmQrPaymentToken = cpmQrPaymentToken;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("selectedQrPayLoad=" + selectedQrPayLoad);
		buffer.append(", transactionAmount=" + transactionAmount);
		buffer.append(", amountCurrency=" + amountCurrency);
		buffer.append(", merchantName=" + merchantName);
		buffer.append(", cpmQrPaymentToken=" + cpmQrPaymentToken);
		return buffer.toString();	}

	@Override
	public String logKey() {
		return null;
	}

}
