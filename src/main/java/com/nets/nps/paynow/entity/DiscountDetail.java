package com.nets.nps.paynow.entity;

public class DiscountDetail {

	private String discountAmt;
	private String discountNote;
	
	public String getDiscountAmt() {
		return discountAmt;
	}
	public void setDiscountAmt(String discountAmt) {
		this.discountAmt = discountAmt;
	}
	public String getDiscountNote() {
		return discountNote;
	}
	public void setDiscountNote(String discountNote) {
		this.discountNote = discountNote;
	}

	@Override
	public String toString() {
		return "DiscountDetail [discountAmt=" + discountAmt + ", discountNote=" + discountNote + "]";
	}
}
