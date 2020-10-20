package com.nets.nps.paynow.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.nets.nps.paynow.entity.OrderQueryRequest;
import com.nets.nps.paynow.entity.OrderQueryTransactionEnquiryData;
import com.nets.nps.paynow.service.impl.OrderQueryRequestValidator;
import com.nets.upos.commons.entity.JsonNpxData;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class OrderQueryRequestValidationTest {

	private OrderQueryRequest request;
	private OrderQueryTransactionEnquiryData transactionEnquiryData;
	private OrderQueryRequestValidator unit;

	@Before
	public void setUp() {
		request = createOrderQueryRequest();
		transactionEnquiryData = new OrderQueryTransactionEnquiryData();
		unit = new OrderQueryRequestValidator();

	}

	/*
	 * Method to create a sample OrderQueryRequest for testing purposes
	 */
	private OrderQueryRequest createOrderQueryRequest() {
		OrderQueryTransactionEnquiryData transactionEnquiryData = new OrderQueryTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("11112222");
		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		OrderQueryRequest request = new OrderQueryRequest();
		request.setRetrievalRef("12345678128");
		request.setInstitutionCode("30000000033");
		request.setTransmissionTime("0608123413");
		request.setChannelIndicator("PNT");
		request.setTransactionEnquiryData(transactionEnquiryData);
		return request;
	}

	@Test
	public void validateOrderQueryRequest() throws JsonFormatException {
		unit.validate(request);
	}

	/*
	 * Test for retrieval ref
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestRetrievalRefNullValue() throws JsonFormatException {
		// Testing null value for retrieval ref
		request.setRetrievalRef(null);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestRetrievalRefEmptyValue() throws JsonFormatException {
		// Testing empty value for retrieval ref
		request.setRetrievalRef("");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestRetrievalRefLengthMoreThan20() throws JsonFormatException {
		// Testing length more than 20
		request.setRetrievalRef("abcdabcdabcd123412341234");
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestRetrievalRefLengthLessThan20() throws JsonFormatException {
		// Testing length less than 20
		request.setRetrievalRef("abcdabcdabc");
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestRetrievalRefLengthEquals20() throws JsonFormatException {
		// Testing length equal 20
		request.setRetrievalRef("12345123451234512345");
		unit.validate(request);
	}

	/*
	 * Test for institution code
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeNullValue() throws JsonFormatException {
		// Testing null value for institution code
		request.setInstitutionCode(null);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeEmptyValue() throws JsonFormatException {
		// Testing empty value for institution code
		request.setInstitutionCode("");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeAlphaValue() throws JsonFormatException {
		// Testing alpha value for institution code
		request.setInstitutionCode("abcdabcdabc");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for institution code
		request.setInstitutionCode("abcdabcd123");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeNegativeValue() throws JsonFormatException {
		// Testing negative value for institution code
		request.setInstitutionCode("-30000000033");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeSymbols() throws JsonFormatException {
		// Testing symbols value for institution code
		request.setInstitutionCode("!@!@!@!@!@!");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeWhiteSpace() throws JsonFormatException {
		// Testing white space for institution code
		request.setInstitutionCode("30000 000033");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeDecimalPoint() throws JsonFormatException {
		// Testing decimal point for institution code
		request.setInstitutionCode("30000.000033");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeLengthMoreThan11() throws JsonFormatException {
		// Testing length more than 11 for institution code
		request.setInstitutionCode("300000000033");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestInstitutionCodeLengthLessThan11() throws JsonFormatException {
		// Testing length less than 11 for institution code
		request.setInstitutionCode("3000000033");
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestInstitutionCodeLengthEquals11() throws JsonFormatException {
		// Testing length equal 11 for institution code
		request.setInstitutionCode("30000000033");
		unit.validate(request);
	}

	/*
	 * Test for transmission time
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeNullValue() throws JsonFormatException {
		// Testing null value for transmission time
		request.setTransmissionTime(null);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeEmptyValue() throws JsonFormatException {
		// Testing empty value for transmission time
		request.setTransmissionTime("");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestTransmissionTimeNegativeValue() throws JsonFormatException {
		// Testing negative value for transmission time
		request.setTransmissionTime("-0608123413");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestTransmissionTimeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for transmission time
		request.setTransmissionTime("abc8123413");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeMonthValue() throws JsonFormatException {
		// Testing month value for transmission time
		request.setTransmissionTime("1308123456");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeDayValue() throws JsonFormatException {
		// Testing day value for transmission time
		request.setTransmissionTime("1233123456");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeHourValue() throws JsonFormatException {
		// Testing hour value for transmission time
		request.setTransmissionTime("1230253456");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeMinuteValue() throws JsonFormatException {
		// Testing minute value for transmission time
		request.setTransmissionTime("1230126656");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeSecondValue() throws JsonFormatException {
		// Testing second value for transmission time
		request.setTransmissionTime("1230123466");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeLengthMoreThan10() throws JsonFormatException {
		// Testing length more than 10 for transmission time
		request.setTransmissionTime("12301234561");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransmissionTimeLengthLessThan10() throws JsonFormatException {
		// Testing less than 10 for transmission time
		request.setTransmissionTime("123012345");
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransmissionTimeLengthEqual10() throws JsonFormatException {
		// Testing equal 10 for transmission time
		request.setTransmissionTime("1230123456");
		unit.validate(request);
	}

	/*
	 * Test for channel indicator
	 */
	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForChannelIndicatorNullValue() throws JsonFormatException {
		// Testing null value for channel indicator
		request.setChannelIndicator(null);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForChannelIndicatorEmptyValue() throws JsonFormatException {
		// Testing empty value for channel indicator
		request.setChannelIndicator("");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForChannelIndicatorLengthMoreThan3() throws JsonFormatException {
		// Testing length more than 3 for channel indicator
		request.setChannelIndicator("PPPT");
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForChannelIndicatorLengthLessThan3() throws JsonFormatException {
		// Testing length less than 3 for channel indicator
		request.setChannelIndicator("PP");
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForChannelIndicatorLengthEqual3() throws JsonFormatException {
		// Testing length equal 3 for channel indicator
		request.setChannelIndicator("PPT");
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNullObject() throws JsonFormatException {
		// Testing null txn enquiry data
		request.setTransactionEnquiryData(null);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data original retrieval reference
	 */
	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataOriginalRetrievalRefNullValue()
			throws JsonFormatException {
		// Testing null value for original retrieval reference
		transactionEnquiryData.setOriginalRetrievalReference(null);
		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataOriginalRetrievalRefEmptyValue()
			throws JsonFormatException {
		// Testing empty value for original retrieval reference
		transactionEnquiryData.setOriginalRetrievalReference("");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataOriginalRetrievalRefLengthMoreThan20()
			throws JsonFormatException {
		// Testing length more than 20 for original retrieval reference
		transactionEnquiryData.setOriginalRetrievalReference("11112222abcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataOriginalRetrievalRefLengthEqual20()
			throws JsonFormatException {
		// Testing length equal 20 for original retrieval reference
		transactionEnquiryData.setOriginalRetrievalReference("222abcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataOriginalRetrievalRefLengthLessThan20()
			throws JsonFormatException {
		// Testing length less than 20 for original retrieval reference
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");
		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data retry attempt
	 */
	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptNullValue() throws JsonFormatException {
		// Testing null value for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");
		transactionEnquiryData.setRetryAttempt(null);
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptEmptyValue() throws JsonFormatException {
		// Testing empty value for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");
		transactionEnquiryData.setRetryAttempt("");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptAlphaValue() throws JsonFormatException {
		// Testing alpha value for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");
		transactionEnquiryData.setRetryAttempt("ab");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptAlphaNumericValue()
			throws JsonFormatException {
		// Testing alpha numeric value for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");
		transactionEnquiryData.setRetryAttempt("a1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptNegativeValue()
			throws JsonFormatException {
		// Testing negative value for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptSymbols() throws JsonFormatException {
		// Testing symbols for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");
		transactionEnquiryData.setRetryAttempt("!@");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptWhiteSpace() throws JsonFormatException {
		// Testing white space for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("! @");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptDecimalPoint()
			throws JsonFormatException {
		// Testing decimal point for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("1.2");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptLengthMoreThan2()
			throws JsonFormatException {
		// Testing length more than 2 for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("123");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptLengthLessThan2()
			throws JsonFormatException {
		// Testing length less than 2 for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataRetryAttemptLengthEqual2()
			throws JsonFormatException {
		// Testing length equal 2 for retry attempt
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data host mid
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataHostMidLengthMoreThan15() throws JsonFormatException {
		// Testing length more than 15 for host mid
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("1234567890123456");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataHostMidLengthLessThan15() throws JsonFormatException {
		// Testing length less than 15 for host mid
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("1234567890123");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataHostMidLengthEqual15() throws JsonFormatException {
		// Testing length less than 15 for host mid
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("123456789012345");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data host tid
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataHostTidLengthMoreThan8() throws JsonFormatException {
		// Testing length more than 8 for host tid
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("123456789");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataHostTidLengthLessThan8() throws JsonFormatException {
		// Testing length less than 8 for host tid
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("1234567");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataHostTidLengthEqual8() throws JsonFormatException {
		// Testing length equal 8 for host tid
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data nets paynow mpan
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanAlphaValue()
			throws JsonFormatException {
		// Testing alpha value for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("abcabcabcabcabcd");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanAlphaNumericValue()
			throws JsonFormatException {
		// Testing alpha numeric value for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("abcabcabc1231234");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanNegativeValue()
			throws JsonFormatException {
		// Testing negative value for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("-1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanSymbols() throws JsonFormatException {
		// Testing symbol for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("!@#$%^&!@#$%^&!!");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanWhiteSpace()
			throws JsonFormatException {
		// Testing white spaces for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("12345678 90123456");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanDecimalPoint()
			throws JsonFormatException {
		// Testing decimal point for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("12345678.90123456");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanLengthMoreThan16()
			throws JsonFormatException {
		// Testing length more than 16 for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("12345678901234567890");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanLengthLessThan16()
			throws JsonFormatException {
		// Testing length less than 16 for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataNetsPaynowMpanLengthEqual16()
			throws JsonFormatException {
		// Testing length less than 15 for nets paynow mpan
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry data bank merchant proxy
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataBankMerchantProxyLengthMoreThan100()
			throws JsonFormatException {
		// Testing length more than 100 for bank merchant proxy
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("123456789012345");
		transactionEnquiryData.setBankMerchantProxy(
				"thisismorethanlengthonehundredthisismorethanlengthonehundredthisismorethanlengthonehundredthisismorethanlengthonehundred");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataBankMerchantProxyLengthLessThan100()
			throws JsonFormatException {
		// Testing length less than 100 for bank merchant proxy
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataBankMerchantProxyLengthEqual100()
			throws JsonFormatException {
		// Testing length equal 100 for bank merchant proxy
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy(
				"thisislengthequalonehundredthisislengthequalonehundredthisislengthequalonehundredthisislengthequalon");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry bank merchant proxy type
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataBankMerchantProxyTypeLengthMoreThan20()
			throws JsonFormatException {
		// Testing length more than 20 for bank merchant proxy type
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("123456789012345");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisismorethantwentythisismorethantwenty");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataBankMerchantProxyTypeLengthLessThan20()
			throws JsonFormatException {
		// Testing length less than 20 for bank merchant proxy type
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("lessthantwenty");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataBankMerchantProxyTypeLengthEqual20()
			throws JsonFormatException {
		// Testing length equal 20 for bank merchant proxy type
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("equaltwentyequaltwen");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry merchant reference number
	 */

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataMerchantReferenceNumberLengthMoreThan40()
			throws JsonFormatException {
		// Testing length more than 40 for merchant reference number
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("123456789012345");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890123456789");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataMerchantReferenceNumberLengthLessThan40()
			throws JsonFormatException {
		// Testing length less than 40 for merchant reference number
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1264567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataMerchantReferenceNumberLengthEqual40()
			throws JsonFormatException {
		// Testing length equal 40 for merchant reference number
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1264567890126456789012345678901234567890");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry invoice reference
	 */

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataInvoiceReferenceLengthLessThan16()
			throws JsonFormatException {
		// Testing length less than 16 for invoice reference
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1264567890126456789012345678900");
		transactionEnquiryData.setInvoiceRef("1234567890");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test(expected = JsonFormatException.class)
	public void validateOrderQueryRequestForTransactionEnquiryDataInvoiceReferenceLengthMoreThan16()
			throws JsonFormatException {
		// Testing length more than 16 for invoice reference
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("123456789012345");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1264567890126456789012345678900");
		transactionEnquiryData.setInvoiceRef("1234567890123456789");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataInvoiceReferenceLengthEqual16()
			throws JsonFormatException {
		// Testing length equal 16 for invoice reference
		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1264567890126456789012345678900");
		transactionEnquiryData.setInvoiceRef("1234567890123456");
		transactionEnquiryData.setNpxData(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

	/*
	 * Test for transaction enquiry npx data
	 */

	@Test
	public void validateOrderQueryRequestForTransactionEnquiryDataNpxData() throws JsonFormatException {
		// Testing non null for npx data
		JsonNpxData npxData = new JsonNpxData();

		transactionEnquiryData.setOriginalRetrievalReference("bcdabcdabccdefgh");

		transactionEnquiryData.setRetryAttempt("11");
		transactionEnquiryData.setHostMid("12345678901234");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1234567890123456");
		transactionEnquiryData.setBankMerchantProxy("thisislessthanonehundred");
		transactionEnquiryData.setBankMerchantProxyType("thisism");
		transactionEnquiryData.setMerchantReferenceNumber("1264567890126456789012345678900");
		transactionEnquiryData.setInvoiceRef("1234567890");
		transactionEnquiryData.setNpxData(npxData);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}

}
