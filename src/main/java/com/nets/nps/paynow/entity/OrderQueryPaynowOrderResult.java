package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class OrderQueryPaynowOrderResult implements Loggable {
	
	@JsonProperty("stan")
	private String stan;
	
	@JsonProperty("approval_code")
	private String approvalCode;
	
	@JsonProperty("merchant_reference_number")
	private String merchantReferenceNumber;
	
	@JsonProperty("receiving_bank_reference")
	private String receivingBankReference;

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getMerchantReferenceNumber() {
		return merchantReferenceNumber;
	}

	public void setMerchantReferenceNumber(String merchantReferenceNumber) {
		this.merchantReferenceNumber = merchantReferenceNumber;
	}

	public String getReceivingBankReference() {
		return receivingBankReference;
	}

	public void setReceivingBankReference(String receivingBankReference) {
		this.receivingBankReference = receivingBankReference;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(", stan=" + stan);
		buffer.append(", approvalCode=" + approvalCode);
		buffer.append(", merchantReferenceNumber=" + merchantReferenceNumber);
		buffer.append(", receivingBankReference=" + receivingBankReference);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return merchantReferenceNumber;
	}
	
	
}
