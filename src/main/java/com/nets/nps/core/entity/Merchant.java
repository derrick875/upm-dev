package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Merchant implements Loggable {
	
	@JsonProperty("nets_tid")
	private String netsTid;
	
	@JsonProperty("mid")
	private String mid;
	
	@JsonProperty("acceptor_name")
	private String acceptorName;

	public String getNetsTid() {
		return netsTid;
	}
	
	public void setNetsTid(String netsTid) {
		this.netsTid = netsTid;
	}
	
	public String getMid() {
		return mid;
	}
	
	public void setMid(String mid) {
		this.mid = mid;
	}
	
	public String getAcceptorName() {
		return acceptorName;
	}
	
	public void setAcceptorName(String acceptorName) {
		this.acceptorName = acceptorName;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("netsTid=" + netsTid);
		buffer.append(", mid=" + mid);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return mid;
	}
	
}
