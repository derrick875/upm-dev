package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RefundReversalResponseData implements Loggable {

	@JsonProperty("stan")
	private String stan;

	@JsonProperty("approval_code")
	private String approvalCode;

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

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("stan=" + stan);
		buffer.append(", approvalCode=" + approvalCode);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return stan;
	}

	
	
}
