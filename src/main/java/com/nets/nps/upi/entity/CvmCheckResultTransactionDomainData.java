package com.nets.nps.upi.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
@JsonInclude(Include.NON_NULL)
public class CvmCheckResultTransactionDomainData implements Loggable {
	@JsonProperty("cvm_passed")
	private String cvmPassed;

	@JsonProperty("cvm_rejection_reason")
	private String cvmRejectionReason;

	@JsonProperty("cpmqrpayment_token")
	private String cpmQrPaymentToken;

	public String getCvmPassed() {
		return cvmPassed;
	}

	public void setCvmPassed(String cvmPassed) {
		this.cvmPassed = cvmPassed;
	}

	public String getCvmRejectionReason() {
		return cvmRejectionReason;
	}

	public void setCvmRejectionReason(String cvmRejectionReason) {
		this.cvmRejectionReason = cvmRejectionReason;
	}

	public String getCpmQrPaymentToken() {
		return cpmQrPaymentToken;
	}

	public void setCpmQrPaymentToken(String cpmQrPaymentToken) {
		this.cpmQrPaymentToken = cpmQrPaymentToken;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("cvmPassed=" + cvmPassed);
		buffer.append(", cvmRejectionReason=" + cvmRejectionReason);
		buffer.append(", cpmQrPaymentToken=" + cpmQrPaymentToken);
		return buffer.toString();
	}

	@Override
	public String logKey() {
		return null;
	}
}

