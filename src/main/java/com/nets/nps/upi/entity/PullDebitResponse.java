package com.nets.nps.upi.entity;

import com.nets.upos.commons.logger.Loggable;

public class PullDebitResponse implements Loggable {
	
	private String responseCode;
	private String retrievalRef;
	private String transmissionTime;
	private String institutionCode;
	private String debitPassed;
	private String debitApprovalCode;
	private String debitApprovalRetrievalRef;
	private String debitRejectionReason;
	
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRetrievalRef() {
		return retrievalRef;
	}
	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
	}
	public String getTransmissionTime() {
		return transmissionTime;
	}
	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}
	public String getInstitutionCode() {
		return institutionCode;
	}
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}
	public String getDebitPassed() {
		return debitPassed;
	}
	public void setDebitPassed(String debitPassed) {
		this.debitPassed = debitPassed;
	}
	public String getDebitApprovalCode() {
		return debitApprovalCode;
	}
	public void setDebitApprovalCode(String debitApprovalCode) {
		this.debitApprovalCode = debitApprovalCode;
	}
	public String getDebitApprovalRetrievalRef() {
		return debitApprovalRetrievalRef;
	}
	public void setDebitApprovalRetrievalRef(String debitApprovalRetrievalRef) {
		this.debitApprovalRetrievalRef = debitApprovalRetrievalRef;
	}
	public String getDebitRejectionReason() {
		return debitRejectionReason;
	}
	public void setDebitRejectionReason(String debitRejectionReason) {
		this.debitRejectionReason = debitRejectionReason;
	}
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PullDebitResponse [");
		buffer.append("responseCode=" + responseCode);
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append("transmissionTime=" + transmissionTime);
		buffer.append("institutionCode=" + institutionCode);
		buffer.append("debitPassed=" + debitPassed);
		buffer.append("debitApprovalCode=" + debitApprovalCode);
		buffer.append("debitApprovalRetrievalRef=" + debitApprovalRetrievalRef);
		buffer.append("debitRejectionReason=" + debitRejectionReason);
		buffer.append("]");
		return buffer.toString();
	}
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
