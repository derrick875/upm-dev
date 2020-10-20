package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.nps.core.entity.Response;

@JsonInclude(Include.NON_NULL) 
public class ReversalResponse extends Response {
	
	@JsonProperty
	private ReversalPaynowOrderResult paynowOrderResult;
	
	
	public ReversalPaynowOrderResult getPaynowOrderResult() {
		return paynowOrderResult;
	}


	public void setPaynowOrderResult(ReversalPaynowOrderResult paynowOrderResult) {
		this.paynowOrderResult = paynowOrderResult;
	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ReversalResponse [");
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
		return this.toString();
	}

	@Override
	public String logKey() {
		return retrievalRef;
	}

}
