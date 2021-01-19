package com.nets.nps.upi.entity;

import com.nets.upos.commons.logger.Loggable;

public class MerchantInfo implements Loggable{
	
	private String acquirerIIN;
	private String fwdIIN;
	private String mid;
	private String merchantName;
	private String mcc;
	private String merchantCountry;
	private String termId;
	private String agentCode;
	
	public String getAcquirerIIN() {
		return acquirerIIN;
	}
	public void setAcquirerIIN(String acquirerIIN) {
		this.acquirerIIN = acquirerIIN;
	}
	public String getFwdIIN() {
		return fwdIIN;
	}
	public void setFwdIIN(String fwdIIN) {
		this.fwdIIN = fwdIIN;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	public String getMerchantCountry() {
		return merchantCountry;
	}
	public void setMerchantCountry(String merchantCountry) {
		this.merchantCountry = merchantCountry;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public String getAgentCode() {
		return agentCode;
	}
	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	
	@Override
	public String toString() {
		return "MerchantInfo [acquirerIIN=" + acquirerIIN + ", fwdIIN=" + fwdIIN + ", mid=" + mid + ", merchantName="
				+ merchantName + ", mcc=" + mcc + ", merchantCountry=" + merchantCountry + ", termId=" + termId
				+ ", agentCode=" + agentCode + "]";
	}
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("acquirerIIN=" + acquirerIIN);
		buffer.append(", fwdIIN=" + fwdIIN);
		buffer.append(", mid=" + mid);
		buffer.append(", merchantName=" + merchantName);
		buffer.append(", mcc=" + mcc);
		buffer.append(", merchantCountry=" + merchantCountry);
		buffer.append(", termId=" + termId);
		buffer.append(", agentCode=" + agentCode);
		return buffer.toString();
	}
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
