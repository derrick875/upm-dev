package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

public class CvmCheckResponse implements Loggable {

	@JsonProperty("response_code")
	private String responseCode;

	@JsonProperty("retrieval_ref")
	private String retrievalRef;

	@JsonProperty("transmission_time")
	private String transmissionTime;

	@JsonProperty("institution_code")
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
		buffer.append("CvmCheckResponse [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", institutionCode" + institutionCode);
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return null;
	}
}
