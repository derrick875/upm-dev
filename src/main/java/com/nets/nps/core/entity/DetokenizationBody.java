package com.nets.nps.core.entity;

import java.util.ArrayList;

import com.nets.upos.commons.logger.Loggable;

public class DetokenizationBody implements Loggable {

	private String nets_tag_2;
	private String processing_code;
	private String trxn_amount;
	private String xmit_datetime;
	private String stan;
	private String trxn_time;
	private String trxn_date;
	private String entry_mode;
	private String condition_code;
	private String ret_ref_num;
	private String terminal_id;
	private String retailer_id;
	private String retailer_info;
	private ArrayList<DetokenizationSpecificData> txn_specific_data;

	private String settlement_date;
	private String approval_code;
	private String response_code;
	
	public String getNets_tag_2() {
		return nets_tag_2;
	}
	public void setNets_tag_2(String nets_tag_2) {
		this.nets_tag_2 = nets_tag_2;
	}
	public String getProcessing_code() {
		return processing_code;
	}
	public void setProcessing_code(String processing_code) {
		this.processing_code = processing_code;
	}
	public String getTrxn_amount() {
		return trxn_amount;
	}
	public void setTrxn_amount(String trxn_amount) {
		this.trxn_amount = trxn_amount;
	}
	public String getXmit_datetime() {
		return xmit_datetime;
	}
	public void setXmit_datetime(String xmit_datetime) {
		this.xmit_datetime = xmit_datetime;
	}
	public String getStan() {
		return stan;
	}
	public void setStan(String stan) {
		this.stan = stan;
	}
	public String getTrxn_time() {
		return trxn_time;
	}
	public void setTrxn_time(String trxn_time) {
		this.trxn_time = trxn_time;
	}
	public String getTrxn_date() {
		return trxn_date;
	}
	public void setTrxn_date(String trxn_date) {
		this.trxn_date = trxn_date;
	}
	public String getEntry_mode() {
		return entry_mode;
	}
	public void setEntry_mode(String entry_mode) {
		this.entry_mode = entry_mode;
	}
	public String getCondition_code() {
		return condition_code;
	}
	public void setCondition_code(String condition_code) {
		this.condition_code = condition_code;
	}
	public String getRet_ref_num() {
		return ret_ref_num;
	}
	public void setRet_ref_num(String ret_ref_num) {
		this.ret_ref_num = ret_ref_num;
	}
	public String getTerminal_id() {
		return terminal_id;
	}
	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}
	public String getRetailer_id() {
		return retailer_id;
	}
	public void setRetailer_id(String retailer_id) {
		this.retailer_id = retailer_id;
	}
	public String getRetailer_info() {
		return retailer_info;
	}
	public void setRetailer_info(String retailer_info) {
		this.retailer_info = retailer_info;
	}
	public ArrayList<DetokenizationSpecificData> getTxn_specific_data() {
		return txn_specific_data;
	}
	public void setTxn_specific_data(ArrayList<DetokenizationSpecificData> txn_specific_data) {
		this.txn_specific_data = txn_specific_data;
	}
	public String getSettlement_date() {
		return settlement_date;
	}
	public void setSettlement_date(String settlement_date) {
		this.settlement_date = settlement_date;
	}
	public String getApproval_code() {
		return approval_code;
	}
	public void setApproval_code(String approval_code) {
		this.approval_code = approval_code;
	}
	public String getResponse_code() {
		return response_code;
	}
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	
	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Detokenization Body [");
		buffer.append("nets_tag_2=" + nets_tag_2);
		buffer.append(", processing_code=" + processing_code);
		buffer.append(", trxn_amount=" + trxn_amount);
		buffer.append(", xmit_datetime=" + xmit_datetime);
		buffer.append(", stan=" + stan);
		buffer.append(", trxn_time=" + trxn_time);
		buffer.append(", trxn_date=" + trxn_date);
		buffer.append(", entry_mode=" + entry_mode);
		buffer.append(", condition_code=" + condition_code);
		buffer.append(", ret_ref_num=" + ret_ref_num);
		buffer.append(", terminal_id=" + terminal_id);
		buffer.append(", retailer_id=" + retailer_id);
		buffer.append(", retailer_info=" + retailer_info);
		buffer.append(", txn_specific_data=" + txn_specific_data);
		buffer.append(", settlement_date=" + settlement_date);
		buffer.append(", approval_code=" + approval_code);
		buffer.append(", response_code=" + response_code);
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
