package com.nets.nps.paynow.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderInfo implements Serializable {

	private String merchantShortName;
	private String stan;
	private BigDecimal amount;
	private String seqNum;
	private String mPan;
	private String dynamicCode;
	private String merchantRef;
	private String qrImage;
	private Integer merchantId;
	private Integer posId;
    private int QrValidityTime;
    private Timestamp creationDate;
    private String qrExpiryTime;
    private String proxyValue;
    private String mid;
    
	public String getMerchantShortName() {
		return merchantShortName;
	}

	public void setMerchantShortName(String merchantShortName) {
		this.merchantShortName = merchantShortName;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getmPan() {
		return mPan;
	}

	public void setmPan(String mPan) {
		this.mPan = mPan;
	}

	public String getDynamicCode() {
		return dynamicCode;
	}

	public void setDynamicCode(String dynamicCode) {
		this.dynamicCode = dynamicCode;
	}

	public String getMerchantRef() {
		return merchantRef;
	}

	public void setMerchantRef(String searchId) {
		this.merchantRef = searchId;
	}

	public String getQrImage() {
		return qrImage;
	}

	public void setQrImage(String qrImage) {
		this.qrImage = qrImage;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public Integer getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public int getQrValidityTime() {
		return QrValidityTime;
	}

	public void setQrValidityTime(int qrValidityTime) {
		QrValidityTime = qrValidityTime;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public String getQrExpiryTime() {
		return qrExpiryTime;
	}

	public void setQrExpiryTime(String qrExpiryTime) {
		this.qrExpiryTime = qrExpiryTime;
	}

	public String getProxyValue() {
		return proxyValue;
	}

	public void setProxyValue(String proxyValue) {
		this.proxyValue = proxyValue;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

}
