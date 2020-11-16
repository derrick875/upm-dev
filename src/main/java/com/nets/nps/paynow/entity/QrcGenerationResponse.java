package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.nps.core.entity.Response;

public class QrcGenerationResponse extends Response {

	@JsonProperty("qrpayload_data_emv")
	private String qrpayloadDataEmv;
	
	@JsonProperty("qrpayload_data_alternate")
	private String qrpayloadDataAlternate;
	
	@JsonProperty("cpmqrpayment_token")
	private String cpmqrpaymentToken;
	
	public String getQrpayloadDataEmv() {
		return qrpayloadDataEmv;
	}

	public void setQrpayloadDataEmv(String qrpayloadDataEmv) {
		this.qrpayloadDataEmv = qrpayloadDataEmv;
	}

	public String getQrpayloadDataAlternate() {
		return qrpayloadDataAlternate;
	}

	public void setQrpayloadDataAlternate(String qrpayloadDataAlternate) {
		this.qrpayloadDataAlternate = qrpayloadDataAlternate;
	}

	public String getCpmqrpaymentToken() {
		return cpmqrpaymentToken;
	}

	public void setCpmqrpaymentToken(String cpmqrpaymentToken) {
		this.cpmqrpaymentToken = cpmqrpaymentToken;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("QrcGenerationResponse [");
		buffer.append("qrpayloadDataEmv=" + qrpayloadDataEmv);
		buffer.append(", qrpayloadDataAlternate=" + qrpayloadDataAlternate);
		buffer.append(", cpmqrpaymentToken" + cpmqrpaymentToken);
		buffer.append("]");
		
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
