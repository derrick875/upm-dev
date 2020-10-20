package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.nps.core.entity.Response;

@JsonInclude(Include.NON_NULL)
public class OrderResponse extends Response {
	
	@JsonProperty("order_payload_data")
	private OrderPayloadData orderPayloadData;
	
	
	public OrderPayloadData getOrderPayloadData() {
		return orderPayloadData;
	}

	public void setOrderPayloadData(OrderPayloadData orderPayloadData) {
		this.orderPayloadData = orderPayloadData;
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
		String orderPayloadDataStr = (orderPayloadData != null) ? orderPayloadData.log() : null;
		buffer.append(", orderPayloadData[" + orderPayloadDataStr + "]");
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
