package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class DetokenizationHeader implements Loggable {
	
	private String product_indicator;
	private String release_number;
	private String status;
	private String originator_code;
	private String responder_code;
	private String mti;
	private String nets_tag_1;
	
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Detokenization Header [");
		buffer.append("product_indicator=" + product_indicator);
		buffer.append(", release_number=" + release_number);
		buffer.append(", status=" + status);
		buffer.append(", originator_code=" + originator_code);
		buffer.append(", responder_code=" + responder_code);
		buffer.append(", mti=" + mti);
		buffer.append(", nets_tag_1=" + nets_tag_1);
		buffer.append("]");
		return buffer.toString();
	}
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getProduct_indicator() {
		return product_indicator;
	}
	public void setProduct_indicator(String product_indicator) {
		this.product_indicator = product_indicator;
	}
	public String getRelease_number() {
		return release_number;
	}
	public void setRelease_number(String release_number) {
		this.release_number = release_number;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOriginator_code() {
		return originator_code;
	}
	public void setOriginator_code(String originator_code) {
		this.originator_code = originator_code;
	}
	public String getResponder_code() {
		return responder_code;
	}
	public void setResponder_code(String responder_code) {
		this.responder_code = responder_code;
	}
	public String getMti() {
		return mti;
	}
	public void setMti(String mti) {
		this.mti = mti;
	}
	public String getNets_tag_1() {
		return nets_tag_1;
	}
	public void setNets_tag_1(String nets_tag_1) {
		this.nets_tag_1 = nets_tag_1;
	}
	
	
	
}
