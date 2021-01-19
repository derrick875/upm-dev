package com.nets.nps.upi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class UpiQrcGenerationRequest implements Loggable {
	
	@JsonProperty("msgInfo")
	private MsgInfo msgInfo;
	
	@JsonProperty("trxInfo")
	private UpiQrcGenerationRequestTransactionRequest trxInfo;
	
	public MsgInfo getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	public UpiQrcGenerationRequestTransactionRequest getTrxInfo() {
		return trxInfo;
	}

	public void setTrxInfo(UpiQrcGenerationRequestTransactionRequest trxInfo) {
		this.trxInfo = trxInfo;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("UpiQrcGenerationRequest [");
		String msgInfoLog = (msgInfo != null) ? msgInfo.log() : null;
		buffer.append("msgInfo[" + msgInfoLog + "]");
		String trxInfoLog = (trxInfo != null) ? trxInfo.log() : null;
		buffer.append(", trxInfo[" + trxInfoLog + "]");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
