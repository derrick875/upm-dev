package com.nets.nps.paynow.entity;

public class MessageResponse {

	private String responseCode;
	private String responseMsg;

	@Override
	public String toString() {
		return "MessageResponse [responseCode=" + responseCode + ", responseMsg=" + responseMsg + "]";
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
}
