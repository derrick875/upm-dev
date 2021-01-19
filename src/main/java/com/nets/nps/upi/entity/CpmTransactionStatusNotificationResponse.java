package com.nets.nps.upi.entity;

import com.nets.upos.commons.logger.Loggable;

public class CpmTransactionStatusNotificationResponse implements Loggable{

	private String responseCode;
	private String retrievalRef;
	private String transmissionTime;
	private String institutionCode;
	
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getRetrievalRef() {
		return retrievalRef;
	}

	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
	}

	public String getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CpmTransactionStatusNotificationResponse [");
		buffer.append("responseCode=" + responseCode);
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append("transmissionTime=" + transmissionTime);
		buffer.append("institutionCode=" + institutionCode);
		buffer.append("]");
		
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
