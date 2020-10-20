package com.nets.nps.paynow.client.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.client.bank.ocbc.OcbcPaynowAdapter;

@Service
public class BankAdapterProcessingFactory {
	@Autowired
	private OcbcPaynowAdapter ocbcPaynowAdapter;

	public PaynowBankAdapter getBankAdapter(String institutionCode) {

		switch (institutionCode) {
		default:
			return ocbcPaynowAdapter;
		}

	}

}
