package com.nets.nps.paynow.entity;

import com.nets.nps.client.impl.MsgInfo;

public class DebitTransactionResponse {
	
	private MsgInfo msgInfo;
	private MessageResponse msgResponse;
	private DebitTransactionRequestTrxInfo trxInfo;
	
	@Override
	public String toString() {
		return "DebitUPIResponse [msgInfo=" + msgInfo + ", msgResponse=" + msgResponse + ", trxInfo=" + trxInfo + "]";
	}

	public MsgInfo getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	public MessageResponse getMsgResponse() {
		return msgResponse;
	}

	public void setMsgResponse(MessageResponse msgResponse) {
		this.msgResponse = msgResponse;
	}

	public DebitTransactionRequestTrxInfo getTrxInfo() {
		return trxInfo;
	}

	public void setTrxInfo(DebitTransactionRequestTrxInfo trxInfo) {
		this.trxInfo = trxInfo;
	}
}
