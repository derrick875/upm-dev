package com.nets.nps.upi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "tbl_1_cvm_check_entity")
public class CvmCheckEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "response_code")
	protected String responseCode;
	
	@Column(name = "retrieval_ref")
	private String retrievalRef;
	
	@Column(name = "institution_code")
	private String institutionCode;
	
	@Column(name = "acquirer_institution_code")
	private String acquirerInstitutionCode;
	
	@Column(name = "selected_qrpayload")
	private String selectedQrPayLoad;
	
	@Column(name = "transaction_amount")
	private String transactionAmount;
	
	@Column(name = "amount_currency")
	private String amountCurrency;
	
	@Column(name = "merchant_name")
	private String merchantName;
	
	@Column(name = "cpmqrpayment_token")
	private String cpmQrPaymentToken;

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

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getAcquirerInstitutionCode() {
		return acquirerInstitutionCode;
	}

	public void setAcquirerInstitutionCode(String acquirerInstitutionCode) {
		this.acquirerInstitutionCode = acquirerInstitutionCode;
	}

	public String getSelectedQrPayLoad() {
		return selectedQrPayLoad;
	}

	public void setSelectedQrPayLoad(String selectedQrPayLoad) {
		this.selectedQrPayLoad = selectedQrPayLoad;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getAmountCurrency() {
		return amountCurrency;
	}

	public void setAmountCurrency(String amountCurrency) {
		this.amountCurrency = amountCurrency;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getCpmQrPaymentToken() {
		return cpmQrPaymentToken;
	}

	public void setCpmQrPaymentToken(String cpmQrPaymentToken) {
		this.cpmQrPaymentToken = cpmQrPaymentToken;
	}

	@Override
	public String toString() {
		return "CvmCheckEntity [id=" + id + ", responseCode=" + responseCode + ", retrievalRef=" + retrievalRef
				+ ", institutionCode=" + institutionCode + ", acquirerInstitutionCode=" + acquirerInstitutionCode
				+ ", selectedQrPayLoad=" + selectedQrPayLoad + ", transactionAmount=" + transactionAmount
				+ ", amountCurrency=" + amountCurrency + ", merchantName=" + merchantName + ", cpmQrPaymentToken="
				+ cpmQrPaymentToken + "]";
	}
	
	
	
	
}
