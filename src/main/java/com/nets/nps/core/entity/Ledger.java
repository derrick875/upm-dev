package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ledger implements Loggable {
	
	@JsonProperty("source_amount")
	private String sourceAmount;
	
	@JsonProperty("source_currency")
	private String sourceCurrency;
	
	@JsonProperty("target_amount")
	private String targetAmount;
	
	@JsonProperty("target_currency")
	private String targetCurrency;
	
	@JsonProperty("rebate_amount")
	private String rebateAmount;
	
	@JsonProperty("rebate_currency")
	private String rebateCurrency;
	
	public String getSourceAmount() {
		return sourceAmount;
	}
	public void setSourceAmount(String sourceAmount) {
		this.sourceAmount = sourceAmount;
	}
	public String getSourceCurrency() {
		return sourceCurrency;
	}
	public void setSourceCurrency(String sourceCurrency) {
		this.sourceCurrency = sourceCurrency;
	}
	public String getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public String getRebateCurrency() {
		return rebateCurrency;
	}
	public void setRebateCurrency(String rebateCurrency) {
		this.rebateCurrency = rebateCurrency;
	}
	public String getTargetAmount() {
		return targetAmount;
	}
	public void setTargetAmount(String targetAmount) {
		this.targetAmount = targetAmount;
	}
	public String getTargetCurrency() {
		return targetCurrency;
	}
	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("sourceAmount=" + sourceAmount);
		buffer.append(", rebateAmount=" + rebateAmount);
		return buffer.toString();
	}
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
