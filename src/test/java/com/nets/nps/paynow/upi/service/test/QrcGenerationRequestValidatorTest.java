package com.nets.nps.paynow.upi.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.service.impl.QRGenerationRequestValidator;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class QrcGenerationRequestValidatorTest {
	
	private QrcGenerationRequest request;
	private QrcGenerationTransactionDomainData transactionDomainData;
	private QRGenerationRequestValidator unit;
	
	@Before
	public void setUp() {
		request = createQrGenRequest();
		transactionDomainData = new QrcGenerationTransactionDomainData();
		unit = new QRGenerationRequestValidator();
		
	}
	private QrcGenerationRequest createQrGenRequest() {
		QrcGenerationTransactionDomainData transactionDomainData = new QrcGenerationTransactionDomainData();
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		QrcGenerationRequest request = new QrcGenerationRequest();
		request.setRetrievalRef("12345678129");
		request.setInstitutionCode("30000000033");
		request.setAcquirerInstitutionCode("30000000033");
		request.setSofUri("sof uri");
		request.setTransmissionTime("0608123413");
		request.setSofAccountId("sof account id");
		request.setTransactionDomainData(transactionDomainData);
		
		return request;		
	}
	
	@Test
	public void validateQRGenerateRequest() throws JsonFormatException {
		unit.validate(request);
	}
	
	/*
	 * Test for retrieval ref
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestRetrievalRefNullValue() throws JsonFormatException {
		//Testing null value for retrieval ref
		request.setRetrievalRef(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestRetrievalRefEmptyValue() throws JsonFormatException {
		//Testing empty value for retrieval ref
		request.setRetrievalRef("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestRetrievalRefLengthMoreThan20() throws JsonFormatException {
		//Testing length more than 20
		request.setRetrievalRef("abcdabcdabcd123412341234");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerateRequestRetrievalRefLengthLessThan20() throws JsonFormatException {
		//Testing length less than 20
		request.setRetrievalRef("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerateRequestRetrievalRefLengthEquals20() throws JsonFormatException {
		//Testing length equal 20
		request.setRetrievalRef("12345123451234512345");
		unit.validate(request);
	}
	
	/*
	 * Test for institution code
	 */

	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeNullValue() throws JsonFormatException {
		//Testing null value for institution code
		request.setInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeEmptyValue() throws JsonFormatException {
		//Testing empty value for institution code
		request.setInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeAlphaValue() throws JsonFormatException {
		//Testing alpha value for institution code
		request.setInstitutionCode("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeAlphaNumericValue() throws JsonFormatException {
		//Testing alpha numeric value for institution code
		request.setInstitutionCode("abcdabcd123");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeNegativeValue() throws JsonFormatException {
		//Testing alpha numeric value for institution code
		request.setInstitutionCode("-30000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeSymbols() throws JsonFormatException {
		//Testing symbols value for institution code
		request.setInstitutionCode("!@!@!@!@!@!");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeWhiteSpace() throws JsonFormatException {
		//Testing white space for institution code
		request.setInstitutionCode("30000 000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeDecimalPoint() throws JsonFormatException {
		//Testing decimal point for institution code
		request.setInstitutionCode("30000.000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeLengthMoreThan11() throws JsonFormatException {
		//Testing length more than 11 for institution code
		request.setInstitutionCode("300000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestInstitutionCodeLengthLessThan11() throws JsonFormatException {
		//Testing length less than 11 for institution code
		request.setInstitutionCode("3000000033");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerateRequestInstitutionCodeLengthEquals11() throws JsonFormatException {
		//Testing length equal 11 for institution code
		request.setInstitutionCode("30000000033");
		unit.validate(request);
	}
	
	/*
	 * Test for acquirer instituton code
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestAcqInstitutionCodeNullValue() throws JsonFormatException {
		//Testing null value for acq institution code
		request.setAcquirerInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerateRequestAcqInstitutionCodeEmptyValue() throws JsonFormatException {
		//Testing empty value for acq institution code
		request.setAcquirerInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeAlphaValue() throws JsonFormatException {
		// Testing alpha value for acq institution code
		request.setAcquirerInstitutionCode("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for acq institution code
		request.setAcquirerInstitutionCode("abcdabcd123");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeNegativeValue() throws JsonFormatException {
		// Testing negative value for acq institution code
		request.setAcquirerInstitutionCode("-30000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeSymbols() throws JsonFormatException {
		// Testing symbols value for acq institution code
		request.setAcquirerInstitutionCode("!@!@!@!@!@!");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeWhiteSpace() throws JsonFormatException {
		// Testing negative value for acq institution code
		request.setAcquirerInstitutionCode("30 000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeDecimalPoint() throws JsonFormatException {
		// Testing decimal point for acq institution code
		request.setAcquirerInstitutionCode("30 000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeLengthMoreThan11() throws JsonFormatException {
		// Testing length more than 11 for acq institution code
		request.setAcquirerInstitutionCode("301000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestAcqInstitutionCodeLengthLessThan11() throws JsonFormatException {
		// Testing length less than 11 for acq institution code
		request.setAcquirerInstitutionCode("3000000033");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestAcqInstitutionCodeLengthEquals11() throws JsonFormatException {
		// Testing length equal 11 for acq institution code
		request.setAcquirerInstitutionCode("30000000033");
		unit.validate(request);
	}
	
	/*
	 * Test for SOF uri
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestSOFUriNullValue() throws JsonFormatException {
		// Testing null value for sof uri
		request.setSofUri(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestSOFUriEmptyValue() throws JsonFormatException {
		// Testing empty value for sof uri
		request.setSofUri("");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestSOFUriLengthLessThan200() throws JsonFormatException {
		// Testing length less than 200 for sof uri
		request.setSofUri("thisislessthan200");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestSOFUriLengthEquals200() throws JsonFormatException {
		// Testing length equals 200 for sof uri
		request.setSofUri("thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisisequals200thisi");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestSOFUriLengthMoreThan200() throws JsonFormatException {
		// Testing length more than 200 for sof uri
		request.setSofUri("thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200thisismorethan200");
		unit.validate(request);
	}
	
	/*
	 * Test for transmission time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransmissionTimeNullValue() throws JsonFormatException {
		// Testing null value for transmission time
		request.setTransmissionTime(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeEmptyValue() throws JsonFormatException {
		// Testing empty value for transmission time
		request.setTransmissionTime("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransmissionTimeNegativeValue() throws JsonFormatException {
		//Testing negative value for transmission time
		request.setTransmissionTime("-0608123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransmissionTimeAlphaNumericValue() throws JsonFormatException {
		//Testing alpha numeric value for transmission time
		request.setTransmissionTime("abc8123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeMonthValue() throws JsonFormatException {
		// Testing month value for transmission time
		request.setTransmissionTime("1308123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeDayValue() throws JsonFormatException {
		// Testing month value for transmission time
		request.setTransmissionTime("1233123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeHourValue() throws JsonFormatException {
		// Testing hour value for transmission time
		request.setTransmissionTime("1230253456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeMinuteValue() throws JsonFormatException {
		// Testing minute value for transmission time
		request.setTransmissionTime("1230126656");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeSecondValue() throws JsonFormatException {
		// Testing second value for transmission time
		request.setTransmissionTime("1230123466");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeLengthMoreThan10() throws JsonFormatException {
		// Testing length more than 10 for transmission time
		request.setTransmissionTime("12301234561");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestForTransmissionTimeLengthLessThan10() throws JsonFormatException {
		// Testing length less than 10 for transmission time
		request.setTransmissionTime("123012345");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestForTransmissionTimeLengthEqual10() throws JsonFormatException {
		// Testing length equal 10 for transmission time
		request.setTransmissionTime("1230123456");
		unit.validate(request);
	}
	
	/*
	 * Test for sof account id
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestSofAccountIdNullValue() throws JsonFormatException {
		// Testing null value for sof account id
		request.setSofAccountId(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestSofAccountIdEmptyValue() throws JsonFormatException {
		// Testing empty value for sof account id
		request.setSofAccountId("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestSofAccountIdLengthMoreThan80() throws JsonFormatException {
		// Testing length more than 80 for sof account id
		request.setSofAccountId("thisismorethanlength80thisismorethanlength80thisismorethanlength80thisismorethanlength80");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestSofAccountIdLengthEqual80() throws JsonFormatException {
		// Testing length equal 80 for sof account id
		request.setSofAccountId("thisisslengthequal80thisisslengthequal80thisisslengthequal80thisisslengthequal80");
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestSofAccountIdLengthLessThan80() throws JsonFormatException {
		// Testing length equal 80 for sof account id
		request.setSofAccountId("thisislessthan80");
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataNullObject() throws JsonFormatException {
		// Testing null txn domain data
		request.setTransactionDomainData(null);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data txn limit amount
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountNullValue() throws JsonFormatException {
		// Test null value for txn limit amount
		transactionDomainData.setTxnLimitAmount(null);
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountEmptyValue() throws JsonFormatException {
		// Test empty value for txn limit amount
		transactionDomainData.setTxnLimitAmount("");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountAlphaValue() throws JsonFormatException {
		// Test alpha value for txn limit amount
		transactionDomainData.setTxnLimitAmount("abcedfgheeee");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountAlphaNumericValue() throws JsonFormatException {
		// Test alpha numeric value for txn limit amount
		transactionDomainData.setTxnLimitAmount("abcedfghe123");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountNegativeValue() throws JsonFormatException {
		// Test negative value for txn limit amount
		transactionDomainData.setTxnLimitAmount("-000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountSymbols() throws JsonFormatException {
		// Test symbols for txn limit amount
		transactionDomainData.setTxnLimitAmount("!@#!@#!@#!@#");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountWhiteSpace() throws JsonFormatException {
		// Test white space for txn limit amount
		transactionDomainData.setTxnLimitAmount("000000 000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountDecimalPoint() throws JsonFormatException {
		// Test decimal point for txn limit amount
		transactionDomainData.setTxnLimitAmount("000000.000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountLengthMoreThan12() throws JsonFormatException {
		// Test length more than 12 for txn limit amount
		transactionDomainData.setTxnLimitAmount("0000001000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountLengthLessThan12() throws JsonFormatException {
		// Test length less than 12 for txn limit amount
		transactionDomainData.setTxnLimitAmount("00000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataTxnLimitAmountLengthEqual12() throws JsonFormatException {
		// Test length equal 12 for txn limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data cvm limit amount
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountNullValue() throws JsonFormatException {
		// Test null value for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount(null);
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountEmptyValue() throws JsonFormatException {
		// Test empty value for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountAlphaValue() throws JsonFormatException {
		// Test alpha value for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("abcedfgheeee");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountAlphaNumericValue() throws JsonFormatException {
		// Test alpha numeric value for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("abcedfghe123");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountNegativeValue() throws JsonFormatException {
		// Test negative value for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("-000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountSymbols() throws JsonFormatException {
		// Test symbols for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("!@#!@#!@#!@#");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountWhiteSpace() throws JsonFormatException {
		// Test white space for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000 000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountDecimalPoint() throws JsonFormatException {
		// Test decimal point for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000.000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountLengthMoreThan12() throws JsonFormatException {
		// Test length more than 12 for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("0000001000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountLengthLessThan12() throws JsonFormatException {
		// Test length less than 12 for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("00000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataCvmLimitAmountLengthEqual12() throws JsonFormatException {
		// Test length equal 12 for cvm limit amount
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for amount limit currency
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataAmountLimitCurrencyNullValue() throws JsonFormatException {
		// Test null value for amount limit currency
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency(null);
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataAmountLimitCurrencyEmptyValue() throws JsonFormatException {
		// Test empty value for amount limit currency
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataAmountLimitCurrencyLengthMoreThan3() throws JsonFormatException {
		// Test length more than 3 for amount limit currency
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGDD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataAmountLimitCurrencyLengthLessThan3() throws JsonFormatException {
		// Test length less than 3 for amount limit currency
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataAmountLimitCurrencyLengthEquals3() throws JsonFormatException {
		// Test length equals 3 for amount limit currency
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction type
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTransactionTypeNullValue() throws JsonFormatException {
		// Test null value for transaction type
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType(null);
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTransactionTypeEmptyValue() throws JsonFormatException {
		// Test empty value for transaction type
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataTransactionTypeLengthMoreThan2() throws JsonFormatException {
		// Test length more than 2 for transaction type
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("112");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataTransactionTypeLengthLessThan2() throws JsonFormatException {
		// Test length less than 2 for transaction type
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("2");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataTransactionTypeLengthEquals2() throws JsonFormatException {
		// Test length equals 2 for transaction type
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("discount coupon info");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for discount coupon info
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateQRGenerationRequestTransactionDomainDataDiscountCouponInfoLengthMoreThan30() throws JsonFormatException {
		// Test length more than 30 for discount coupon info
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("thisismorethanlength30thisismorethanlength30");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataDiscountCouponInfoLengthLessThan30() throws JsonFormatException {
		// Test length less than 30 for discount coupon info
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("thisislessthan30");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateQRGenerationRequestTransactionDomainDataDiscountCouponInfoLengthEquals30() throws JsonFormatException {
		// Test length equals 30 for discount coupon info
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000000460");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("11");
		transactionDomainData.setDiscountCouponInfo("thisisequalss30thisisequalss30");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	
	
	
	
	
	
	
}
