package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.nps.core.entity.Response;

@JsonInclude(Include.NON_NULL)
public class OrderQueryResponse extends Response{

	@JsonProperty("paynow_order_result")
	private OrderQueryPaynowOrderResult paynowOrderResult;
	
	public OrderQueryPaynowOrderResult getPaynowOrderResult() {
		return paynowOrderResult;
	}

	public void setPaynowOrderResult(OrderQueryPaynowOrderResult paynowOrderResult) {
		this.paynowOrderResult = paynowOrderResult;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("OrderResponse [");
		buffer.append("mti=" + mti);
		buffer.append(", processCode=" + processCode);
		buffer.append(", responseCode=" + responseCode);
		buffer.append(", retrievalRef=" + retrievalRef);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", institutionCode=" + institutionCode);
		String paynowOrderResultStr = (paynowOrderResult != null) ? paynowOrderResult.log() : null;
		buffer.append(", paynowOrderResult[" + paynowOrderResultStr + "]");
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public String log() {
		return toString();
	}

	@Override
	public String logKey() {
		return retrievalRef;
	}

}
