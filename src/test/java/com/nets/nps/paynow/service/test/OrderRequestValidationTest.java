package com.nets.nps.paynow.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.nets.nps.paynow.entity.OrderRequest;
import com.nets.nps.paynow.entity.OrderTransactionDomainData;
import com.nets.nps.paynow.service.impl.OrderRequestValidator;
import com.nets.upos.commons.entity.JsonNpxData;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class OrderRequestValidationTest {

	private OrderRequest request;
	private OrderTransactionDomainData transactionDomainData;
	private OrderRequestValidator unit;
	
	@Before
	public void setUp() {
		request = createOrderRequest();
		transactionDomainData = new OrderTransactionDomainData();
		unit = new OrderRequestValidator();
	}
	
	/*
	 * Method to create a sample OrderRequest for testing purposes
	 */
	private OrderRequest createOrderRequest() {
		OrderTransactionDomainData transactionDomainData = new OrderTransactionDomainData();
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		OrderRequest request = new OrderRequest();
		request.setRetrievalRef("12345678129");
		request.setInstitutionCode("30000000033");
		request.setTransmissionTime("0608123413");
		request.setChannelIndecator("PNT");
		request.setQrFormat("PAYNOW");
		request.setQrVersion("122");
		request.setTransactionDomainData(transactionDomainData);
		return request;	
	}
	
	@Test
	public void validateOrderRequest() throws JsonFormatException {
		unit.validate(request);
	}
	
	/*
	 * Test for retrieval ref
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestRetrievalRefNullValue() throws JsonFormatException {
		//Testing null value for retrieval ref
		request.setRetrievalRef(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestRetrievalRefEmptyValue() throws JsonFormatException {
		//Testing empty value for retrieval ref
		request.setRetrievalRef("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestRetrievalRefLengthMoreThan20() throws JsonFormatException {
		//Testing length more than 20
		request.setRetrievalRef("abcdabcdabcd123412341234");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestRetrievalRefLengthLessThan20() throws JsonFormatException {
		//Testing length less than 20
		request.setRetrievalRef("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestRetrievalRefLengthEquals20() throws JsonFormatException {
		//Testing length less than 20
		request.setRetrievalRef("12345123451234512345");
		unit.validate(request);
	}
	
	/*
	 * Test for institution code
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeNullValue() throws JsonFormatException {
		//Testing null value for institution code
		request.setInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeEmptyValue() throws JsonFormatException {
		//Testing empty value for institution code
		request.setInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeAlphaValue() throws JsonFormatException {
		//Testing alpha value for institution code
		request.setInstitutionCode("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeAlphaNumericValue() throws JsonFormatException {
		//Testing alpha numeric value for institution code
		request.setInstitutionCode("abcdabcd123");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeNegativeValue() throws JsonFormatException {
		//Testing alpha numeric value for institution code
		request.setInstitutionCode("-30000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeSymbols() throws JsonFormatException {
		//Testing symbols value for institution code
		request.setInstitutionCode("!@!@!@!@!@!");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeWhiteSpace() throws JsonFormatException {
		//Testing white space for institution code
		request.setInstitutionCode("30000 000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeDecimalPoint() throws JsonFormatException {
		//Testing decimal point for institution code
		request.setInstitutionCode("30000.000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeLengthMoreThan11() throws JsonFormatException {
		//Testing length more than 11 for institution code
		request.setInstitutionCode("300000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestInstitutionCodeLengthLessThan11() throws JsonFormatException {
		//Testing length less than 11 for institution code
		request.setInstitutionCode("3000000033");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestInstitutionCodeLengthEquals11() throws JsonFormatException {
		//Testing length equal 11 for institution code
		request.setInstitutionCode("30000000033");
		unit.validate(request);
	}
	
	/*
	 * Test for transmission time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeNullValue() throws JsonFormatException {
		// Testing null value for transmission time
		request.setTransmissionTime(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeEmptyValue() throws JsonFormatException {
		// Testing null value for transmission time
		request.setTransmissionTime("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestTransmissionTimeNegativeValue() throws JsonFormatException {
		//Testing negative value for transmission time
		request.setTransmissionTime("-0608123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestTransmissionTimeAlphaNumericValue() throws JsonFormatException {
		//Testing alpha numeric value for transmission time
		request.setTransmissionTime("abc8123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeMonthValue() throws JsonFormatException {
		// Testing month value for transmission time
		request.setTransmissionTime("1308123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeDayValue() throws JsonFormatException {
		// Testing month value for transmission time
		request.setTransmissionTime("1233123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeHourValue() throws JsonFormatException {
		// Testing hour value for transmission time
		request.setTransmissionTime("1230253456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeMinuteValue() throws JsonFormatException {
		// Testing minute value for transmission time
		request.setTransmissionTime("1230126656");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeSecondValue() throws JsonFormatException {
		// Testing second value for transmission time
		request.setTransmissionTime("1230123466");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeLengthMoreThan10() throws JsonFormatException {
		// Testing length more than 10 for transmission time
		request.setTransmissionTime("12301234561");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransmissionTimeLengthLessThan10() throws JsonFormatException {
		// Testing less more than 10 for transmission time
		request.setTransmissionTime("123012345");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransmissionTimeLengthEqual10() throws JsonFormatException {
		// Testing equal 10 for transmission time
		request.setTransmissionTime("1230123456");
		unit.validate(request);
	}
	
	/*
	 * Test for channel indicator
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForChannelIndicatorNullValue() throws JsonFormatException {
		//Testing null for channel indicator
		request.setChannelIndecator(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForChannelIndicatorEmptyValue() throws JsonFormatException {
		//Testing null for channel indicator
		request.setChannelIndecator("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForChannelIndicatorLengthLessThan3() throws JsonFormatException {
		//Testing length less than 3 for channel indicator
		request.setChannelIndecator("PT");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForChannelIndicatorLengthMoreThan3() throws JsonFormatException {
		//Testing length more than 3 for channel indicator
		request.setChannelIndecator("PTNN");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForChannelIndicatorLengthEqual3() throws JsonFormatException {
		//Testing length equal 3 for channel indicator
		request.setChannelIndecator("PNT");
		unit.validate(request);
	}
	
	/*
	 * Test for qr format
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForQrFormatNullValue() throws JsonFormatException {
		//Testing null for qr format
		request.setQrFormat(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForQrFormatEmptyValue() throws JsonFormatException {
		//Testing null for qr format
		request.setQrFormat("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForQrFormatLengthMoreThan10() throws JsonFormatException {
		//Testing length more than 10 for qr format
		request.setQrFormat("PAYNOWPAYNOW");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForQrFormatLengthLessThan10() throws JsonFormatException {
		//Testing length less than 10 for qr format
		request.setQrFormat("PAYNOW");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForQrFormatLengthEqual10() throws JsonFormatException {
		//Testing length equal 10 for qr format
		request.setQrFormat("PAYNOWPAYN");
		unit.validate(request);
	}
	
	/*
	 * Test for qr version
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForQrVersionNullValue() throws JsonFormatException {
		//Testing null for qr version
		request.setQrVersion(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForQrVersionEmptyValue() throws JsonFormatException {
		//Testing null for qr version
		request.setQrVersion("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForQrVersionLengthMoreThan10() throws JsonFormatException {
		//Testing length more than 10 for qr version
		request.setQrVersion("PAYNOWPAYNOW");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForQrVersionLengthLessThan10() throws JsonFormatException {
		//Testing length less than 10 for qr version
		request.setQrVersion("PAYNOW");
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForQrVersionLengthEqual10() throws JsonFormatException {
		//Testing length equal 10 for qr version
		request.setQrVersion("PAYNOWPAYN");
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNullObject() throws JsonFormatException {
		// Testing null txn Domain data 
		request.setTransactionDomainData(null);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountAlphaValue() throws JsonFormatException {
		// Testing alpha value for payment amount
		transactionDomainData.setPaymentAmount("abcedfgheeee");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for payment amount
		transactionDomainData.setPaymentAmount("abcedfghe123");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountNegativeValue() throws JsonFormatException {
		// Testing negative value for payment amount
		transactionDomainData.setPaymentAmount("-000000000460");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountSymbols() throws JsonFormatException {
		// Testing symbol for payment amount
		transactionDomainData.setPaymentAmount("!@#!@#!@#!@#");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountWhiteSpace() throws JsonFormatException {
		// Testing white space for payment amount
		transactionDomainData.setPaymentAmount("000000 000460");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountDecimalPoint() throws JsonFormatException {
		// Testing decimal point for payment amount
		transactionDomainData.setPaymentAmount("000000.000460");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountLengthMoreThan12() throws JsonFormatException {
		// Testing length more than 12 for payment amount
		transactionDomainData.setPaymentAmount("00000000046011");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentAmountLengthLessThan12() throws JsonFormatException {
		// Testing length less than 12 for payment amount
		transactionDomainData.setPaymentAmount("00000000046");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataPaymentAmountLengthEqual12() throws JsonFormatException {
		// Testing length equal 12 for payment amount
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data payment currency
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentCurrencyNullValue() throws JsonFormatException {
		// Testing null value for payment currency
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency(null);
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentCurrencyEmptyValue() throws JsonFormatException {
		// Testing empty value for payment currency
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentCurrencyLengthMoreThan3() throws JsonFormatException {
		// Testing length more than 3 for payment currency
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKKD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataPaymentCurrencyLengthLessThan3() throws JsonFormatException {
		// Testing length less than 3 for payment currency
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HK");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}

	@Test
	public void validateOrderRequestForTransactionDomainDataPaymentCurrencyLengthEqual3() throws JsonFormatException {
		// Testing length equal 3 for payment currency
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data transaction time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeNullValue() throws JsonFormatException {
		// Testing null value for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime(null);
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeEmptyValue() throws JsonFormatException {
		// Testing empty value for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeAlphaValue() throws JsonFormatException {
		// Testing alpha value for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("abcdef");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("abcdef");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeNegativeValue() throws JsonFormatException {
		// Testing negative value for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("-112233");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeSymbols() throws JsonFormatException {
		// Testing symbols for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("!@!@!@");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeWhiteSpace() throws JsonFormatException {
		// Testing white space for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112 233");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeDecimalPoint() throws JsonFormatException {
		// Testing decimal point for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112.233");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeLengthMoreThan6() throws JsonFormatException {
		// Testing length more than 6 for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("11211233");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionTimeLengthLessThan6() throws JsonFormatException {
		// Testing length less than 6 for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("11222");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataTransactionTimeLengthEqual6() throws JsonFormatException {
		// Testing length less than 6 for transaction time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data transaction date
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateNullValue() throws JsonFormatException {
		// Testing null value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate(null);
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateEmptyValue() throws JsonFormatException {
		// Testing empty value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateAlphaValue() throws JsonFormatException {
		// Testing alpha value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("abcd");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("ab12");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateNegativeValue() throws JsonFormatException {
		// Testing negative value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("-1111");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateSymbols() throws JsonFormatException {
		// Testing symbols for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("!@!@");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateWhiteSpace() throws JsonFormatException {
		// Testing white space for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("12 12");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateDecimalPoint() throws JsonFormatException {
		// Testing decimal point for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("12.12");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateLengthMoreThan4() throws JsonFormatException {
		// Testing length more than 4 for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("12112");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataTransactionDateLengthLessThan4() throws JsonFormatException {
		// Testing length less than 4 for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("121");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataTransactionDateLengthEqual4() throws JsonFormatException {
		// Testing length equal 4 for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data validity time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeNullValue() throws JsonFormatException {
		// Testing null value for validity time
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime(null);
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeEmptyValue() throws JsonFormatException {
		// Testing empty value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeAlphaValue() throws JsonFormatException {
		// Testing alpha value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("abcd");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("abcd12");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeNegativeValue() throws JsonFormatException {
		// Testing negative value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("-111111");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeSymbol() throws JsonFormatException {
		// Testing negative value for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("!@!@@#");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeWhiteSpace() throws JsonFormatException {
		// Testing white space for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("1231 23");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeDecimalPoint() throws JsonFormatException {
		// Testing decimal point for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("1231.23");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataValidityTimeLengthMoreThan8() throws JsonFormatException {
		// Testing length more than 8 for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("1231231233");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataValidityTimeLengthLessThan8() throws JsonFormatException {
		// Testing length less than 8 for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("123123");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataValidityTimeLengthEqual8() throws JsonFormatException {
		// Testing length equal 8 for transaction date
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data entry mode
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeAlphaValue() throws JsonFormatException {
		// Testing alpha value for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("abc");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("1a2");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeNegativeValue() throws JsonFormatException {
		// Testing negative value for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("-11");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeSymbols() throws JsonFormatException {
		// Testing symbols for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("!@!");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeWhiteSpace() throws JsonFormatException {
		// Testing white space for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("1 1");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeDecimalPoint() throws JsonFormatException {
		// Testing decimal point for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("1.1");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataEntryModeLengthMoreThan3() throws JsonFormatException {
		// Testing length more than 3 for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("1231");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataEntryModeLengthLessThan3() throws JsonFormatException {
		// Testing length less than 3 for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("12");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataEntryModeLengthEqual3() throws JsonFormatException {
		// Testing length equal 3 for entry mode
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for condition code
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeAlphaValue() throws JsonFormatException {
		// Testing alpha value for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("aa");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("a1");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeNegativeValue() throws JsonFormatException {
		// Testing negative value for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("-11");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeSymbols() throws JsonFormatException {
		// Testing symbols for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("!@");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeWhiteSpaces() throws JsonFormatException {
		// Testing white spaces for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("1 2");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeDecimalPoint() throws JsonFormatException {
		// Testing white spaces for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("1.2");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeLengthMoreThan2() throws JsonFormatException {
		// Testing length more than 2 for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("121");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataConditionCodeLengthLessThan2() throws JsonFormatException {
		// Testing length less than 2 for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("1");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataConditionCodeLengthEqual2() throws JsonFormatException {
		// Testing length equal 2 for condition code
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data host mid
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataHostMidLengthMoreThan15() throws JsonFormatException {
		// Testing length more than 15 for host mid
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("11111111119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataHostMidLengthLessThan15() throws JsonFormatException {
		// Testing length less than 15 for host mid
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("11111111119");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataHostMidLengthEqual15() throws JsonFormatException {
		// Testing length equal 15 for host mid
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data host tid
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataHostTidLengthMoreThan8() throws JsonFormatException {
		// Testing length more than 8 for host mid
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("1119860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataHostTidLengthLessThan8() throws JsonFormatException {
		// Testing length more than 8 for host mid
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("123456");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataHostTidLengthEqual8() throws JsonFormatException {
		// Testing length equal 8 for host mid
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data nets paynow mpan
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanAlphaValue() throws JsonFormatException {
		// Testing alpha value for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("abcabcabcabcabcd");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("abcabcabc1231234");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanNegativeValue() throws JsonFormatException {
		// Testing negative value for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("-1234567890123456");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanSymbols() throws JsonFormatException {
		// Testing negative value for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("!@#$%^&!@#$%^&!!");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanWhiteSpace() throws JsonFormatException {
		// Testing white space for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("12345678 90123456");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanDecimalPoint() throws JsonFormatException {
		// Testing decimal point for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("12345678.90123456");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanLengthMoreThan16() throws JsonFormatException {
		// Testing length more than 16 for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("12345678901234567890");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanLengthLessThan16() throws JsonFormatException {
		// Testing length less than 16 for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataNetsPaynowMpanLengthEqual16() throws JsonFormatException {
		// Testing length equal 16 for nets paynow mpan
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data bank merchant proxy
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataBankMerchantProxyLengthMoreThan100() throws JsonFormatException {
		// Testing length more than 100 for bank merchant proxy
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisismorethanlengthonehundredthisismorethanlengthonehundredthisismorethanlengthonehundredthisismorethanlengthonehundred");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataBankMerchantProxyLengthLessThan100() throws JsonFormatException {
		// Testing length less than 100 for bank merchant proxy
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataBankMerchantProxyLengthEqual100() throws JsonFormatException {
		// Testing length equal 100 for bank merchant proxy
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislengthequalonehundredthisislengthequalonehundredthisislengthequalonehundredthisislengthequalon");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data bank merchant proxy type
	 */	
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataBankMerchantProxyTypeLengthMoreThan20() throws JsonFormatException {
		// Testing length more than 20 for bank merchant proxy type
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("thisismorethantwentythisismorethantwenty");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataBankMerchantProxyTypeLengthLessThan20() throws JsonFormatException {
		// Testing length less than 20 for bank merchant proxy type
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataBankMerchantProxyTypeLengthEqual20() throws JsonFormatException {
		// Testing length equal 20 for bank merchant proxy type
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("equaltwentyequaltwen");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 *  Test for transaction domain data merchant reference number
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataMerchantReferenceNumberLengthMoreThan40() throws JsonFormatException {
		// Testing length more than 40 for bank merchant reference number
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("1234567890123456789012345678901234567890123456789");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataMerchantReferenceNumberLengthLessThan40() throws JsonFormatException {
		// Testing length less than 40 for bank merchant reference number
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("12345678901234567890123456789012");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}

	@Test
	public void validateOrderRequestForTransactionDomainDataMerchantReferenceNumberLengthEqual40() throws JsonFormatException {
		// Testing length equal 40 for bank merchant reference number
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 *  Test for transaction domain data invoice reference
	 */
	
	@Test
	public void validateOrderRequestForTransactionDomainDataInvoiceReferenceLengthLessThan16() throws JsonFormatException {
		// Testing length less than 16 for invoice reference
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("12345678901234567890123456789012");
		transactionDomainData.setInvoiceRef("1234567890");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateOrderRequestForTransactionDomainDataInvoiceReferenceLengthMoreThan16() throws JsonFormatException {
		// Testing length more than 16 for invoice reference
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("12345678901234567890123456789012");
		transactionDomainData.setInvoiceRef("123456789012345678");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateOrderRequestForTransactionDomainDataInvoiceReferenceLengthEqual16() throws JsonFormatException {
		// Testing length more than 16 for invoice reference
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("12345678901234567890123456789012");
		transactionDomainData.setInvoiceRef("1234567890123456");
		transactionDomainData.setNpxData(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data npx data 
	 */
	@Test
	public void validateOrderRequestForTransactionDomainDataNpxData() throws JsonFormatException {
		// Testing non null for npx data
		JsonNpxData npxData = new JsonNpxData();
		
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("HKD");
		transactionDomainData.setTransactionTime("112222");
		transactionDomainData.setTransactionDate("1212");
		transactionDomainData.setValidityTime("12312312");
		transactionDomainData.setEntryMode("121");
		transactionDomainData.setConditionCode("11");
		transactionDomainData.setHostMid("123456789012345");
		transactionDomainData.setHostTid("12345678");
		transactionDomainData.setNetsPaynowMpan("1234567890123456");
		transactionDomainData.setBankMerchantProxy("thisislessthanonehundred");
		transactionDomainData.setBankMerchantProxyType("lessthantwenty");
		transactionDomainData.setMerchantReferenceNumber("12345678901234567890123456789012");
		transactionDomainData.setInvoiceRef("1234567890123456");
		transactionDomainData.setNpxData(npxData);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
}
