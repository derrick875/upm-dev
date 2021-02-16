package com.nets.nps.upi.entity;

import java.io.Serializable;

public class AdditionalProcessingUpiResponse implements Serializable {
	
	private static final long serialVersionUID = 7319567287959144325L;
	private MsgInfo msgInfo;
	private MessageResponse msgResponse;
	
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
	@Override
	public String toString() {
		return "AdditionalProcessingUpiResponse [msgInfo=" + msgInfo.log() + ", msgResponse=" + msgResponse.toString() + "]";
	}
}

