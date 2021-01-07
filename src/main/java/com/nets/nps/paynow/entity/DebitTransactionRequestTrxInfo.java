package com.nets.nps.paynow.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class DebitTransactionRequestTrxInfo implements Loggable{

	private String relTrxMsgID;
	private String relTrxType;
	private String authID;
	private String otpKey;
	private String otpValue;

	private MerchantInfo merchantInfo;
	
	private String onUsFlag;
	
	private String debitAccountInfo;
	
	private String trxAmt;
	private String trxCurrency;
	
	private List<DiscountDetail> discountDetails;
	
	private String billAmt;
	private String billCurrency;
	private String markupAmt;
	private String feeAmt;
	private String billConvRate;
	
	private String settAmt;
	private String settCurrency;
	private String settConvRate;
	private String convDate;
	private String trxNote;
	private String settDate;
	private String posEntryModeCode;
	private String retrivlRefNum;
	private String transDatetime;
	private String traceNum;
	private String appOrderNo;
	private String referNo;
	
	public String getRelTrxMsgID() {
		return relTrxMsgID;
	}
	public void setRelTrxMsgID(String relTrxMsgID) {
		this.relTrxMsgID = relTrxMsgID;
	}
	public String getRelTrxType() {
		return relTrxType;
	}
	public void setRelTrxType(String relTrxType) {
		this.relTrxType = relTrxType;
	}
	public String getAuthID() {
		return authID;
	}
	public void setAuthID(String authID) {
		this.authID = authID;
	}
	public String getOtpKey() {
		return otpKey;
	}
	public void setOtpKey(String otpKey) {
		this.otpKey = otpKey;
	}
	public String getOtpValue() {
		return otpValue;
	}
	public void setOtpValue(String otpValue) {
		this.otpValue = otpValue;
	}
	public MerchantInfo getMerchantInfo() {
		return merchantInfo;
	}
	public void setMerchantInfo(MerchantInfo merchantInfo) {
		this.merchantInfo = merchantInfo;
	}
	public String getOnUsFlag() {
		return onUsFlag;
	}
	public void setOnUsFlag(String onUsFlag) {
		this.onUsFlag = onUsFlag;
	}
	public String getDebitAccountInfo() {
		return debitAccountInfo;
	}
	public void setDebitAccountInfo(String debitAccountInfo) {
		this.debitAccountInfo = debitAccountInfo;
	}
	public String getTrxAmt() {
		return trxAmt;
	}
	public void setTrxAmt(String trxAmt) {
		this.trxAmt = trxAmt;
	}
	public String getTrxCurrency() {
		return trxCurrency;
	}
	public void setTrxCurrency(String trxCurrency) {
		this.trxCurrency = trxCurrency;
	}
	public List<DiscountDetail> getDiscountDetails() {
		return discountDetails;
	}
	public void setDiscountDetails(List<DiscountDetail> discountDetails) {
		this.discountDetails = discountDetails;
	}
	public String getBillAmt() {
		return billAmt;
	}
	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}
	public String getBillCurrency() {
		return billCurrency;
	}
	public void setBillCurrency(String billCurrency) {
		this.billCurrency = billCurrency;
	}
	public String getMarkupAmt() {
		return markupAmt;
	}
	public void setMarkupAmt(String markupAmt) {
		this.markupAmt = markupAmt;
	}
	public String getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getBillConvRate() {
		return billConvRate;
	}
	public void setBillConvRate(String billConvRate) {
		this.billConvRate = billConvRate;
	}
	public String getSettAmt() {
		return settAmt;
	}
	public void setSettAmt(String settAmt) {
		this.settAmt = settAmt;
	}
	public String getSettCurrency() {
		return settCurrency;
	}
	public void setSettCurrency(String settCurrency) {
		this.settCurrency = settCurrency;
	}
	public String getSettConvRate() {
		return settConvRate;
	}
	public void setSettConvRate(String settConvRate) {
		this.settConvRate = settConvRate;
	}
	public String getConvDate() {
		return convDate;
	}
	public void setConvDate(String convDate) {
		this.convDate = convDate;
	}
	public String getTrxNote() {
		return trxNote;
	}
	public void setTrxNote(String trxNote) {
		this.trxNote = trxNote;
	}
	public String getSettDate() {
		return settDate;
	}
	public void setSettDate(String settDate) {
		this.settDate = settDate;
	}
	public String getPosEntryModeCode() {
		return posEntryModeCode;
	}
	public void setPosEntryModeCode(String posEntryModeCode) {
		this.posEntryModeCode = posEntryModeCode;
	}
	public String getRetrivlRefNum() {
		return retrivlRefNum;
	}
	public void setRetrivlRefNum(String retrivlRefNum) {
		this.retrivlRefNum = retrivlRefNum;
	}
	public String getTransDatetime() {
		return transDatetime;
	}
	public void setTransDatetime(String transDatetime) {
		this.transDatetime = transDatetime;
	}
	public String getTraceNum() {
		return traceNum;
	}
	public void setTraceNum(String traceNum) {
		this.traceNum = traceNum;
	}
	public String getAppOrderNo() {
		return appOrderNo;
	}
	public void setAppOrderNo(String appOrderNo) {
		this.appOrderNo = appOrderNo;
	}
	public String getReferNo() {
		return referNo;
	}
	public void setReferNo(String referNo) {
		this.referNo = referNo;
	}
	
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		String merchantInfoLog = (merchantInfo !=null) ? merchantInfo.log() : null;
		buffer.append("merchantInfo[" + merchantInfoLog + "]");
		buffer.append(", debitAccountInfo" + debitAccountInfo);
		buffer.append(", discountDetails" + discountDetails);
		buffer.append(", relTrxMsgID" + relTrxMsgID);
		buffer.append(", relTrxType" + relTrxType);
		buffer.append(", authID" + authID);
		buffer.append(", otpKey" + otpKey);
		buffer.append(", otpValue" + otpValue);
		buffer.append(", onUsFlag" + onUsFlag);
		buffer.append(", trxAmt" + trxAmt);
		buffer.append(", trxCurrency" + trxCurrency);
		buffer.append(", billAmt" + billAmt);
		buffer.append(", billCurrency" + billCurrency);
		buffer.append(", markupAmt" + markupAmt);
		buffer.append(", feeAmt" + feeAmt);
		buffer.append(", billConvRate" + billConvRate);
		buffer.append(", settAmt" + settAmt);
		buffer.append(", settCurrency" + settCurrency);
		buffer.append(", settConvRate" + settConvRate);
		buffer.append(", convDate" + convDate);
		buffer.append(", trxNote" + trxNote);
		buffer.append(", settDate" + settDate);
		buffer.append(", posEntryModeCode" + posEntryModeCode);
		buffer.append(", retrivlRefNum" + retrivlRefNum);
		buffer.append(", transDatetime" + transDatetime);
		buffer.append(", traceNum" + traceNum);
		buffer.append(", appOrderNo" + appOrderNo);
		buffer.append(", referNo" + referNo);
		
		return buffer.toString();
	}
	
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
