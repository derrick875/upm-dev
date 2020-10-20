package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.nps.core.entity.Response;

@JsonInclude(Include.NON_NULL)
public class RefundReversalResponse extends Response {

	@JsonProperty("refund_reversal_response_data")
	private RefundReversalResponseData responseData;

	public RefundReversalResponseData getRefundReversalResponseData() {
		return responseData;
	}

	public void setRefundReversalResponseData(RefundReversalResponseData responseData) {
		this.responseData = responseData;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RefundReversalResponse [");
		buffer.append("mti=" + mti);
		buffer.append(", processCode=" + processCode);
		buffer.append(", responseCode=" + responseCode);
		buffer.append(", retrievalRef=" + retrievalRef);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", institutionCode=" + institutionCode);
		String responseDataStr = (responseData != null) ? responseData.log() : null;
		buffer.append(", refundReversalResponseData[" + responseDataStr + "]");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public String log() {
		return this.toString();
	}

	@Override
	public String logKey() {
		return retrievalRef;
	}

}
