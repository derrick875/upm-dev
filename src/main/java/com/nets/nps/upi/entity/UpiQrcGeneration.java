package com.nets.nps.upi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@Entity
@Table(name = "upi_qrc_generation")
public class UpiQrcGeneration implements Loggable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "cpqrc_no")
	private String cpqrcNo;
	
	@Column(name = "emv_cpqrc_payload")
	private String emvCpqrcPayload;
	
	@Column(name = "barcode_cpqrc_payload")
	private String barcodeCpqrcPayload;
	
	@Column(name = "cpmqr_payment_token")
	private String cpmqrPaymentToken;
	
	
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
	
//	public UpiQrcGeneration(Long id, String cpqrcNo, String emvCpqrcPayload, String barcodeCpqrcPayload,
//			String cpmqrPaymentToken, String transmissionTime, String institutionCode) {
//		this.id = id;
//		this.cpqrcNo = cpqrcNo;
//		this.emvCpqrcPayload = emvCpqrcPayload;
//		this.barcodeCpqrcPayload = barcodeCpqrcPayload;
//		this.cpmqrPaymentToken = cpmqrPaymentToken;
//		this.transmissionTime = transmissionTime;
//		this.institutionCode = institutionCode;
//	}
	
	
	
	public UpiQrcGeneration() {
	}


	public UpiQrcGeneration(Long id, String cpqrcNo, String emvCpqrcPayload, String barcodeCpqrcPayload,
			String cpmqrPaymentToken, String retrievalRef, String institutionCode, String acquirerInstitutionCode,
			String sofUri, String transmissionTime, String sofAccountId) {
		super();
		this.id = id;
		this.cpqrcNo = cpqrcNo;
		this.emvCpqrcPayload = emvCpqrcPayload;
		this.barcodeCpqrcPayload = barcodeCpqrcPayload;
		this.cpmqrPaymentToken = cpmqrPaymentToken;
		this.retrievalRef = retrievalRef;
		this.institutionCode = institutionCode;
		this.acquirerInstitutionCode = acquirerInstitutionCode;
		this.sofUri = sofUri;
		this.transmissionTime = transmissionTime;
		this.sofAccountId = sofAccountId;
	}

 
	public String getRetrievalRef() {
		return retrievalRef;
	}


	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
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

	public String getSofAccountId() {
		return sofAccountId;
	}

	public void setSofAccountId(String sofAccountId) {
		this.sofAccountId = sofAccountId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCpqrcNo() {
		return cpqrcNo;
	}

	public void setCpqrcNo(String cpqrcNo) {
		this.cpqrcNo = cpqrcNo;
	}

	public String getEmvCpqrcPayload() {
		return emvCpqrcPayload;
	}

	public void setEmvCpqrcPayload(String emvCpqrcPayload) {
		this.emvCpqrcPayload = emvCpqrcPayload;
	}

	public String getBarcodeCpqrcPayload() {
		return barcodeCpqrcPayload;
	}

	public void setBarcodeCpqrcPayload(String barcodeCpqrcPayload) {
		this.barcodeCpqrcPayload = barcodeCpqrcPayload;
	}

	public String getCpmqrPaymentToken() {
		return cpmqrPaymentToken;
	}

	public void setCpmqrPaymentToken(String cpmqrPaymentToken) {
		this.cpmqrPaymentToken = cpmqrPaymentToken;
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
		return "id: " + id + ", cpqrcNo: " + cpqrcNo + ", emvCpqrcPayload:" + emvCpqrcPayload + ", barcodeCpqrcPayload: " + barcodeCpqrcPayload + ", cpmqrPaymentToken: " + cpmqrPaymentToken + ", transmissionTime: " + transmissionTime + ", institutionCode: " + institutionCode;	
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
