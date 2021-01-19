package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class DetokenizationSpecificData implements Loggable {

	private String sub_id;
	private String sub_length;
	private String nets_bank_fiid;
	private String nets_token_id;
	private String scheme_network_id;
	private String scheme_vc_pan;
	private String scheme_vc_psn;
	private String scheme_vc_exp;
	private String scheme_token_id;
	private String scheme_spec_info;
	private String hash_id;
	private String nets_token_pan;
	private String status;
	
	public String getSub_id() {
		return sub_id;
	}
	public void setSub_id(String sub_id) {
		this.sub_id = sub_id;
	}
	public String getSub_length() {
		return sub_length;
	}
	public void setSub_length(String sub_length) {
		this.sub_length = sub_length;
	}
	public String getNets_bank_fiid() {
		return nets_bank_fiid;
	}
	public void setNets_bank_fiid(String nets_bank_fiid) {
		this.nets_bank_fiid = nets_bank_fiid;
	}
	public String getNets_token_id() {
		return nets_token_id;
	}
	public void setNets_token_id(String nets_token_id) {
		this.nets_token_id = nets_token_id;
	}
	public String getScheme_network_id() {
		return scheme_network_id;
	}
	public void setScheme_network_id(String scheme_network_id) {
		this.scheme_network_id = scheme_network_id;
	}
	public String getScheme_vc_pan() {
		return scheme_vc_pan;
	}
	public void setScheme_vc_pan(String scheme_vc_pan) {
		this.scheme_vc_pan = scheme_vc_pan;
	}
	public String getScheme_vc_psn() {
		return scheme_vc_psn;
	}
	public void setScheme_vc_psn(String scheme_vc_psn) {
		this.scheme_vc_psn = scheme_vc_psn;
	}
	public String getScheme_vc_exp() {
		return scheme_vc_exp;
	}
	public void setScheme_vc_exp(String scheme_vc_exp) {
		this.scheme_vc_exp = scheme_vc_exp;
	}
	public String getScheme_token_id() {
		return scheme_token_id;
	}
	public void setScheme_token_id(String scheme_token_id) {
		this.scheme_token_id = scheme_token_id;
	}
	public String getScheme_spec_info() {
		return scheme_spec_info;
	}
	public void setScheme_spec_info(String scheme_spec_info) {
		this.scheme_spec_info = scheme_spec_info;
	}
	public String getHash_id() {
		return hash_id;
	}
	public void setHash_id(String hash_id) {
		this.hash_id = hash_id;
	}
	public String getNets_token_pan() {
		return nets_token_pan;
	}
	public void setNets_token_pan(String nets_token_pan) {
		this.nets_token_pan = nets_token_pan;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Detokenization Body [");
		buffer.append("sub_id=" + sub_id);
		buffer.append(", sub_length=" + sub_length);
		buffer.append(", nets_bank_fiid=" + nets_bank_fiid);
		buffer.append(", nets_token_id=" + nets_token_id);
		buffer.append(", scheme_network_id=" + scheme_network_id);
		buffer.append(", scheme_vc_pan=" + scheme_vc_pan);
		buffer.append(", scheme_vc_psn=" + scheme_vc_psn);
		buffer.append(", scheme_vc_exp=" + scheme_vc_exp);
		buffer.append(", scheme_token_id=" + scheme_token_id);
		buffer.append(", scheme_spec_info=" + scheme_spec_info);
		buffer.append(", hash_id=" + hash_id);
		buffer.append(", nets_token_pan=" + nets_token_pan);
		buffer.append(", status=" + status);
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
