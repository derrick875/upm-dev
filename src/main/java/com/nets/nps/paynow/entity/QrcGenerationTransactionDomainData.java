package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class QrcGenerationTransactionDomainData implements Loggable {

	@JsonProperty("txn_limit_amount")
	private String txnLimitAmount;
	
	@JsonProperty("cvm_limit_amount")
	private String cvmLimitAmount;
	
	@JsonProperty("amount_limit_currency")
	private String amountLimitCurrency;
	
	@JsonProperty("transaction_type")
	private String transactionType;
	
	@JsonProperty("discount_coupon_info")
	private String discountCouponInfo;
	
	public String getTxnLimitAmount() {
		return txnLimitAmount;
	}

	public void setTxnLimitAmount(String txnLimitAmount) {
		this.txnLimitAmount = txnLimitAmount;
	}

	public String getCvmLimitAmount() {
		return cvmLimitAmount;
	}

	public void setCvmLimitAmount(String cvmLimitAmount) {
		this.cvmLimitAmount = cvmLimitAmount;
	}

	public String getAmountLimitCurrency() {
		return amountLimitCurrency;
	}

	public void setAmountLimitCurrency(String amountLimitCurrency) {
		this.amountLimitCurrency = amountLimitCurrency;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getDiscountCouponInfo() {
		return discountCouponInfo;
	}

	public void setDiscountCouponInfo(String discountCouponInfo) {
		this.discountCouponInfo = discountCouponInfo;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("txnLimitAmount=" + txnLimitAmount);
		buffer.append(", cvmLimitAmount=" + cvmLimitAmount);
		buffer.append(", amountLimitCurrency=" + amountLimitCurrency);
		buffer.append(", transactionType=" + transactionType);
		buffer.append(", discountCouponInfo=" + discountCouponInfo);
		
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return transactionType;
	}

}
