package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.nps.client.impl.MsgInfo;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class DebitTransactionRequest implements Loggable {
	
	private MsgInfo msgInfo;
	private DebitTransactionRequestTrxInfo trxInfo;
	
	public MsgInfo getMsgInfo() {
		return msgInfo;
	}
	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}
	public DebitTransactionRequestTrxInfo getTrxInfo() {
		return trxInfo;
	}
	public void setTrxInfo(DebitTransactionRequestTrxInfo trxInfo) {
		this.trxInfo = trxInfo;
	}
	
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DebitUpiRequest [");
		String msgInfoLog = (msgInfo !=null) ? msgInfo.log() : null;
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
