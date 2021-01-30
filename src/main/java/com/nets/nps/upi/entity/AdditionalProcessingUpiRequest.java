package com.nets.nps.upi.entity;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalProcessingUpiRequest implements Serializable{
	
	private static final long serialVersionUID = 7319567287959144986L;
	private MsgInfo msgInfo;
	private AdditionalProcessingTransactionInfo trxInfo;
	private String correlationId;

	public MsgInfo getMsgInfo() {
		return msgInfo;
	}

	public void setMsgInfo(MsgInfo msgInfo) {
		this.msgInfo = msgInfo;
	}

	public AdditionalProcessingTransactionInfo getTrxInfo() {
		return trxInfo;
	}

	public void setTrxInfo(AdditionalProcessingTransactionInfo trxInfo) {
		this.trxInfo = trxInfo;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	@Override
	public String toString() {
		return "AdditionalProcessingUpiRequest [msgInfo=" + msgInfo + ", trxInfo=" + trxInfo + "]";
	}
}
