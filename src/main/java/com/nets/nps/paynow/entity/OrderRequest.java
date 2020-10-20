package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class OrderRequest extends Request implements Loggable {
	
	@JsonProperty("retrieval_ref")
	private String retrievalRef;

	@JsonProperty("institution_code")
	private String institutionCode;

	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("channel_indicator")
	private String channelIndecator;
	
	@JsonProperty("qr_format")
	private String qrFormat;
	
	@JsonProperty("qr_version")
	private String qrVersion;
	
	@JsonProperty("transaction_domain_data")
	private OrderTransactionDomainData transactionDomainData;


	public String getRetrievalRef() {
		return retrievalRef;
	}

	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	public String getChannelIndecator() {
		return channelIndecator;
	}

	public void setChannelIndecator(String channelIndecator) {
		this.channelIndecator = channelIndecator;
	}

	public String getQrFormat() {
		return qrFormat;
	}

	public void setQrFormat(String qrFormat) {
		this.qrFormat = qrFormat;
	}

	public String getQrVersion() {
		return qrVersion;
	}

	public void setQrVersion(String qrVersion) {
		this.qrVersion = qrVersion;
	}

	public OrderTransactionDomainData getTransactionDomainData() {
		return transactionDomainData;
	}

	public void setTransactionDomainData(OrderTransactionDomainData transactionDomainData) {
		this.transactionDomainData = transactionDomainData;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("OrderRequest [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", channelIndecator=" + channelIndecator);
		buffer.append(", qrFormat=" + qrFormat);
		buffer.append(", qrVersion=" + qrVersion);
		String transactionDomainDataLog = (transactionDomainData != null) ? transactionDomainData.log() : null;
		buffer.append(", transactionDomainData[" + transactionDomainDataLog + "]");
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public OrderResponse createResponse() {
		OrderResponse response = new OrderResponse();
		response.setMti(MtiRequestMapper.defaultReponseMti(this.getMti()));
		response.setProcessCode(this.getProcessCode());
		response.setInstitutionCode(this.getInstitutionCode());
		response.setRetrievalRef(this.getRetrievalRef());
		response.setTransmissionTime(this.getTransmissionTime());
		
		return response;
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