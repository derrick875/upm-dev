package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.nps.core.entity.Response;

@JsonInclude(Include.NON_NULL)
public class CreditNotificationResponse extends Response {

	@JsonProperty("transaction_response_data")
	private TransactionResponseData transactionResponseData;

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CreditNotificationResponse [");
		buffer.append("mti=" + mti);
		buffer.append(", processCode=" + processCode);
		buffer.append(", responseCode=" + responseCode);
		buffer.append(", retrievalRef=" + retrievalRef);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", institutionCode=" + institutionCode);
		String transactionResponseDataStr = (transactionResponseData != null) ? transactionResponseData.log() : null;
		buffer.append(", transactionResponseData[" + transactionResponseDataStr + "]");
		buffer.append("]");
		return buffer.toString();

	}

	public TransactionResponseData getTransactionResponseData() {
		return transactionResponseData;
	}

	public void setTransactionResponseData(TransactionResponseData transactionResponseData) {
		this.transactionResponseData = transactionResponseData;
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
