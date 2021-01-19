package com.nets.nps.upi.entity;

public class TransactionResultNotificationResponse {
	
	private MsgInfo msgInfo;
	private MessageResponse msgResponse;
	
	@Override
	public String toString() {
		return "TransactionResultNotificationResponse [msgInfo=" + msgInfo + ", msgResponse=" + msgResponse +"]";
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
	
	
}
