package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.nets.upos.commons.logger.Loggable;

@JsonSubTypes({})
public abstract class Response implements Loggable {

	@JsonProperty("mti")
	protected String mti;

	@JsonProperty("process_code")
	protected String processCode;

	@JsonProperty("institution_code")
	protected String institutionCode;

	@JsonProperty("retrieval_ref")
	protected String retrievalRef;

	@JsonProperty("transmission_time")
	protected String transmissionTime;

	@JsonProperty("response_code")
	protected String responseCode;

	//Todo Set Json ignore
	@JsonIgnore 
	//@JsonProperty("response_msg")
	protected String responseMsg;

	public String getMti() {
		return mti;
	}

	public void setMti(String mti) {
		this.mti = mti;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

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

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	@Override
	public String toString() {
		return "Response{" + "mti='" + mti + '\'' + ", processCode='" + processCode + '\'' + '}';
	}
}
