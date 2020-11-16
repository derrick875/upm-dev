package com.nets.nps.client.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class MsgInfo implements Loggable {
	
	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("msgID")
	private String msgID;
	
	@JsonProperty("timeStamp")
	private String timeStamp;
	
	@JsonProperty("msgType")
	private String msgType;
	
	@JsonProperty("insID")
	private String insID;

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getMsgId() {
		return msgID;
	}

	public void setMsgId(String msgId) {
		this.msgID = msgId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getInsID() {
		return insID;
	}

	public void setInsID(String insID) {
		this.insID = insID;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("versionNo=" + versionNo);
		buffer.append(", msgID=" + msgID);
		buffer.append(", timeStamp=" + timeStamp);
		buffer.append(", msgType=" + msgType);
		buffer.append(", insID=" + insID);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
