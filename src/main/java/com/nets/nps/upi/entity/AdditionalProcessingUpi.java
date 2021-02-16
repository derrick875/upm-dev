package com.nets.nps.upi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@Entity
@Table(name = "add_processing_upi")
public class AdditionalProcessingUpi implements Loggable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "version_no")
	private String versionNo;

	@Column(name = "msg_id")
	private String msgID;

	@Column(name = "time_stamp")
	private String timeStamp;

	@Column(name = "msg_type")
	private String msgType;

	@Column(name = "ins_id")
	private String insID;

	@Column(name = "emv_cpqrc_payload")
	private String emvCpqrcPayload;

	@Column(name = "barcode_cpqrc_payload")
	private String barcodeCpqrcPayload;

	@Column(name = "token")
	private String token;

	@Column(name = "device_id")
	private String deviceID;

	@Column(name = "trx_amt")
	private String trxAmt;

	@Column(name = "trx_currency")
	private String trxCurrency;

	@Column(name = "merchant_name")
	private String merchantName;

	public Long getId() {
		return id;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getInsID() {
		return insID;
	}

	public void setInsID(String insID) {
		this.insID = insID;
	}

	public String getEmvCpqrcPayload() {
		return emvCpqrcPayload;
	}

	public void setEmvCpqrcPayload(String emvCpqrcPayload) {
		this.emvCpqrcPayload = emvCpqrcPayload;
	}

	public String getBarcodeCpqrcPayload() {
		return barcodeCpqrcPayload;
	}

	public void setBarcodeCpqrcPayload(String barcodeCpqrcPayload) {
		this.barcodeCpqrcPayload = barcodeCpqrcPayload;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
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

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	@Override
	public String log() {
		return "id: " + id + ", versionNo: " + versionNo + ", msgID:" + msgID + "," + " timeStamp: " + timeStamp
				+ ", msgType: " + msgType + ", insID: " + insID + ", institutionCode: " + insID + ", emvCpqrcPayload: "
				+ emvCpqrcPayload + "barcode_cpqrc_payload: " + barcodeCpqrcPayload + "token: " + token + "deviceID: "
				+ deviceID + "trxAmt: " + trxAmt + "trxCurrency: " + trxCurrency + "merchantName: " + merchantName;

	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
