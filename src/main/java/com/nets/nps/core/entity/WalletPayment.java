package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.nps.core.entity.Payment;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class WalletPayment extends Payment implements Loggable{
	
	@JsonProperty("wallet")
	private Wallet wallet;

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	@Override
	public String log() {
		StringBuffer buffer = new StringBuffer();
		String walletStr = (wallet != null) ? wallet.log() : null;
	    buffer.append(", wallet[" + walletStr + "]");
		return buffer.toString();
	}

	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	} 
	
}
