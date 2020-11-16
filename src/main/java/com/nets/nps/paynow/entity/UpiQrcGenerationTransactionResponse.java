package com.nets.nps.paynow.entity;

public class UpiQrcGenerationTransactionResponse {
	
	private String cpqrcNo;
	private String emvCpqrcPayload; //this is an array
	private String barcodeCpqrcPayload; // this is an array
	public String getCpqrcNo() {
		return cpqrcNo;
	}
	public void setCpqrcNo(String cpqrcNo) {
		this.cpqrcNo = cpqrcNo;
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
}
