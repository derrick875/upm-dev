package com.nets.nps.paynow.entity;

import com.nets.nps.client.impl.MsgInfo;

public class UpiQrcGenerationResponse {

	private MsgInfo msgInfo;
    private UpiQrcGenerationTransactionResponse trxInfo;
    private MessageResponse msgResponse;
    
	public MsgInfo getMsgInfo() {
		return msgInfo;
	}
	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}
	public UpiQrcGenerationTransactionResponse getTrxInfo() {
		return trxInfo;
	}
	public void setTrxInfo(UpiQrcGenerationTransactionResponse trxInfo) {
		this.trxInfo = trxInfo;
	}
	public MessageResponse getMsgResponse() {
		return msgResponse;
	}
	public void setMsgResponse(MessageResponse msgResponse) {
		this.msgResponse = msgResponse;
	}
}
