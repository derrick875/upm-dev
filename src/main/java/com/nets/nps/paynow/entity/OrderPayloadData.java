package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class OrderPayloadData implements Loggable {

	@JsonProperty("qrpayload_image")
	private String qrpayloadImage;

	@JsonProperty("qr_format")
	private String qrFormat;

	@JsonProperty("qr_version")
	private String qrVersion;

	@JsonProperty("merchant_reference_number")
	private String merchantReferenceNumber;

	public String getQrpayloadImage() {
		return qrpayloadImage;
	}

	public void setQrpayloadImage(String qrpayloadImage) {
		this.qrpayloadImage = qrpayloadImage;
	}

	public String getQrFormat() {
		return qrFormat;
	}

	public void setQrFormat(String qrFormat) {
		this.qrFormat = qrFormat;
	}

	public String getQrVersion() {
		return qrVersion;
	}

	public void setQrVersion(String qrVersion) {
		this.qrVersion = qrVersion;
	}

	public String getMerchantReferenceNumber() {
		return merchantReferenceNumber;
	}

	public void setMerchantReferenceNumber(String merchantReferenceNumber) {
		this.merchantReferenceNumber = merchantReferenceNumber;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(", qrpayloadImage=" + qrpayloadImage);
		buffer.append(", qrFormat=" + qrFormat);
		buffer.append(", qrVersion=" + qrVersion);
		buffer.append("merchantReferenceNumber=" + merchantReferenceNumber);

		return buffer.toString();
	}

	@Override
	public String logKey() {
		return merchantReferenceNumber;
	}

}
