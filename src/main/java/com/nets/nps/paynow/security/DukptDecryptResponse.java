package com.nets.nps.paynow.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DukptDecryptResponse {
	@JsonProperty("DID")
	private String domainId;

	@JsonProperty("SRID")
	private String srId;

	@JsonProperty("BDKToken")
	private String bdkToken;

	@JsonProperty("MFR")
	private String manufacturerRef;

	@JsonProperty("KSN")
	private String ksn;

	@JsonProperty("Plaintext")
	private String plaintext;

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getSrId() {
		return srId;
	}

	public void setSrId(String srId) {
		this.srId = srId;
	}

	public String getBdkToken() {
		return bdkToken;
	}

	public void setBdkToken(String bdkToken) {
		this.bdkToken = bdkToken;
	}

	public String getManufacturerRef() {
		return manufacturerRef;
	}

	public void setManufacturerRef(String manufacturerRef) {
		this.manufacturerRef = manufacturerRef;
	}

	public String getKsn() {
		return ksn;
	}

	public void setKsn(String ksn) {
		this.ksn = ksn;
	}

	public String getPlaintext() {
		return plaintext;
	}

	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}
}
