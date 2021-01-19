package com.nets.nps.upi.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TransactionResultNotificationRequest implements Serializable {

	private static final long serialVersionUID = -4178677828713687373L;
	
	private MsgInfo msgInfo;
	private TransactionResultTrxInfo trxInfo;
	
	public MsgInfo getMsgInfo() {
		return msgInfo;
	}
	
	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	public TransactionResultTrxInfo getTrxInfo() {
		return trxInfo;
	}

	public void setTrxInfo(TransactionResultTrxInfo trxInfo) {
		this.trxInfo = trxInfo;
	}

	@Override
	public String toString() {
		return "TransactionResultNotificationRequest [msgInfo=" + msgInfo + ", trxInfo=" + trxInfo + "]";
	}
	
}
