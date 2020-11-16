package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.nps.core.entity.Request;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class QrcGenerationRequest extends Request implements Loggable{

	@JsonProperty("retrieval_ref")
	private String retrievalRef;
	
	@JsonProperty("institution_code")
	private String institutionCode;
	
	@JsonProperty("acquirer_institution_code")
	private String acquirerInstitutionCode;
	
	@JsonProperty("SOF_uri")
	private String sofUri;
	
	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("sof_account_id")
	private String sofAccountId;
	
	@JsonProperty("transaction_domain_data")
	private QrcGenerationTransactionDomainData transactionDomainData;
	
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

	public String getAcquirerInstitutionCode() {
		return acquirerInstitutionCode;
	}

	public void setAcquirerInstitutionCode(String acquirerInstitutionCode) {
		this.acquirerInstitutionCode = acquirerInstitutionCode;
	}

	public String getSofUri() {
		return sofUri;
	}

	public void setSofUri(String sofUri) {
		this.sofUri = sofUri;
	}

	public String getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	public String getSofAccountId() {
		return sofAccountId;
	}

	public void setSofAccountId(String sofAccountId) {
		this.sofAccountId = sofAccountId;
	}

	public QrcGenerationTransactionDomainData getTransactionDomainData() {
		return transactionDomainData;
	}

	public void setTransactionDomainData(QrcGenerationTransactionDomainData transactionDomainData) {
		this.transactionDomainData = transactionDomainData;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("QrcGenerationRequest [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", acquirerInstitutionCode=" + acquirerInstitutionCode);
		buffer.append(", sofUri=" + sofUri);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", sofAccountId=" + sofAccountId);
		String transactionDomainDataLog = (transactionDomainData != null) ? transactionDomainData.log() : null;
		buffer.append(", transactionDomainData[" + transactionDomainDataLog + "]");
		buffer.append("]");
		
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return retrievalRef;
	}

}
