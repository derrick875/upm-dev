package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Wallet implements Loggable{
	
	@JsonProperty("sof_uri")
	private String sofUri;
	
	@JsonProperty("sof_account_id")
	private String sofAccountId;

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

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("sofUri=" + sofUri);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
