package com.nets.nps.upi.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonInclude(Include.NON_NULL)
public class AdditionalProcessingResultRequest implements Serializable {

	private static final long serialVersionUID = 148512397498107460L;
	private MsgInfo msgInfo;
	private AdditionalProcessingResultTransactionInfo trxInfo;

	public MsgInfo getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	public AdditionalProcessingResultTransactionInfo getTrxInfo() {
		return trxInfo;
	}

	public void setTrxInfo(AdditionalProcessingResultTransactionInfo trxInfo) {
		this.trxInfo = trxInfo;
	}

	@Override
	public String toString() {
		return "AdditionalProcessingResultRequest [msgInfo=" + msgInfo + ", trxInfo=" + trxInfo + "]";
	}
	
	
}
