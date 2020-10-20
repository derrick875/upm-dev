package com.nets.nps.paynow.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.nets.nps.paynow.entity.ReversalRequest;
import com.nets.nps.paynow.entity.ReversalTransactionEnquiryData;
import com.nets.nps.paynow.service.impl.ReversalRequestValidator;
import com.nets.upos.commons.entity.JsonNpxData;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class ReversalRequestValidationTest {

	private ReversalRequest request;
	private ReversalTransactionEnquiryData transactionEnquiryData;
	private ReversalRequestValidator unit;
	
	@Before
	public void setUp() {
		request = createReversalRequest();
		transactionEnquiryData = new ReversalTransactionEnquiryData();
		unit = new ReversalRequestValidator();
	}
	
	/**
	 * Method to create a sample ReversalRequest for testing purposes
	 */
	private ReversalRequest createReversalRequest() {
		ReversalTransactionEnquiryData transactionEnquiryData = new ReversalTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("11223344556677889900");
		transactionEnquiryData.setHostMid("123456787654321");
		transactionEnquiryData.setHostTid("12345678");
		transactionEnquiryData.setNetsPaynowMpan("1122334455667788");
		transactionEnquiryData.setBankMerchantProxy("1234ABCD");
		transactionEnquiryData.setBankMerchantProxyType("11223344556677889900");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGHIJ1234567890");
		transactionEnquiryData.setInvoiceRef("1122334455667788");
		transactionEnquiryData.setNpxData(null);
		ReversalRequest request = new ReversalRequest();
		request.setRetrievalRef("11223344556677889900");
		request.setInstitutionCode("12345654321");
		request.setTransmissionTime("0618101545");
		request.setChannelIndicator("PNT");
		request.setTransactionEnquiryData(transactionEnquiryData);
		return request;
	}
	
	@Test
	public void validateReversalRequest () throws JsonFormatException {
		unit.validate(request);
	}

	/**
	 * Tests for retrieval ref
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_RetrievalRef_NullValue() throws JsonFormatException {
		//testing null value
		request.setRetrievalRef(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_RetrievalRef_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setRetrievalRef("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_RetrievalRef_LengthMoreThan20() throws JsonFormatException {
		//testing length > 20
		request.setRetrievalRef("1234567890ABCDEFGHIJKLMNOP");
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_RetrievalRef_LengthLessThan20() throws JsonFormatException {
		//testing length < 20
		request.setRetrievalRef("1234ABCD");
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_RetrievalRef_LengthEqualTo20() throws JsonFormatException {
		//testing length == 20
		request.setRetrievalRef("1234567890ABCDEFGHIJ");
		unit.validate(request);
	}
	
	/**
	 * Tests for institution code
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_NullValue() throws JsonFormatException {
		//testing null value
		request.setInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_AlphaValue() throws JsonFormatException {
		//testing alpha value
		request.setInstitutionCode("abcdefghijk");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		request.setInstitutionCode("abcdef12345");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_NegativeValue() throws JsonFormatException {
		//testing negative value
		request.setInstitutionCode("-1234567890");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_Symbols() throws JsonFormatException {
		//testing symbols
		request.setInstitutionCode("~!@#$%^&*()");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_WhiteSpace() throws JsonFormatException {
		//testing whitespace
		request.setInstitutionCode("12345 54321");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_DecimalValue() throws JsonFormatException {
		//testing decimal
		request.setInstitutionCode("12345.54321");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_LengthMoreThan11() throws JsonFormatException {
		//testing length > 11
		request.setInstitutionCode("112233445566");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_InstitutionCode_LengthLessThan11() throws JsonFormatException {
		//testing length < 11
		request.setInstitutionCode("1122334455");
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_InstitutionCode_EqualTo11() throws JsonFormatException {
		//testing length == 11
		request.setInstitutionCode("12345654321");
		unit.validate(request);
	}
	
	/**
	 * Tests for transmission time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_NullValue() throws JsonFormatException {
		//testing null value
		request.setTransmissionTime(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setTransmissionTime("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_AlphaValue() throws JsonFormatException {
		//testing alpha value
		request.setTransmissionTime("abcdefghij");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		request.setTransmissionTime("abcde12345");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_Symbols() throws JsonFormatException {
		//testing symbols
		request.setTransmissionTime("!@#$%^&*()");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_WhiteSpace() throws JsonFormatException {
		//testing whitespace value
		request.setTransmissionTime("0618 10154");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_Decimal() throws JsonFormatException {
		//testing decimal value
		request.setTransmissionTime("0618.10154");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_NegativeValue() throws JsonFormatException {
		//testing negative value
		request.setTransmissionTime("-180610154");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_MonthValueMoreThan12() throws JsonFormatException {
		//testing month value > 12
		request.setTransmissionTime("1318101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_MonthValueLessThan1() throws JsonFormatException {
		//testing month value < 1
		request.setTransmissionTime("0018101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_DayValueMoreThan31() throws JsonFormatException {
		//testing day value > 31
		request.setTransmissionTime("0632101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_DayValueMoreThan30For30DayMonth() throws JsonFormatException {
		//testing day value > 30 for 30day month
		request.setTransmissionTime("0631101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_DayValueMoreThan29ForFebruary() throws JsonFormatException {
		//testing day value < 29 for February
		request.setTransmissionTime("0230101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_DayValueLessThan1() throws JsonFormatException {
		//testing day value < 1
		request.setTransmissionTime("0600101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_HourValueMoreThan23() throws JsonFormatException {
		//testing hour value > 23
		request.setTransmissionTime("0618241545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_MinuteValueMoreThan60() throws JsonFormatException {
		//testing minute value > 60
		request.setTransmissionTime("0618106145");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_SecondValueMoreThan60() throws JsonFormatException {
		//testing second value > 60
		request.setTransmissionTime("0618101561");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_LengthMoreThan10() throws JsonFormatException {
		//testing length > 60
		request.setTransmissionTime("06181015453");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransmissionTime_LengthLessThan10() throws JsonFormatException {
		//testing length < 10
		request.setTransmissionTime("061810154");
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransmissionTime_LengthEqualTo10() throws JsonFormatException {
		//testing length == 10
		request.setTransmissionTime("0618101545");
		unit.validate(request);
	}
	
	/**
	 * Tests for channel indicator
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_ChannelIndicator_NullValue() throws JsonFormatException {
		//testing null value
				request.setChannelIndicator(null);
				unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_ChannelIndicator_EmptyValue() throws JsonFormatException {
		//testing null value
				request.setChannelIndicator("");
				unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_ChannelIndicator_LengthMoreThan3() throws JsonFormatException {
		//testing length > 3
				request.setChannelIndicator("ABCD");
				unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_ChannelIndicator_LengthLessThan3() throws JsonFormatException {
		//testing length < 3
				request.setChannelIndicator("AB");
				unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_ChannelIndicator_LengthEqualTo3AndInvalidInput() throws JsonFormatException {
		//testing length == 3 but invalid input
				request.setChannelIndicator("ABC");
				unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_ChannelIndicator_LengthEqualTo3AndValidInputPNT() throws JsonFormatException {
		//testing length == 3 and valid input PNT
				request.setChannelIndicator("PNT");
				unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_ChannelIndicator_LengthEqualTo3AndValidInputPNO() throws JsonFormatException {
		//testing length == 3 and valid input PNO
				request.setChannelIndicator("PNO");
				unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_ChannelIndicator_LengthEqualTo3AndValidInputPBC() throws JsonFormatException {
		//testing length == 3 and valid input PBC
				request.setChannelIndicator("PBC");
				unit.validate(request);
	}
	
	/**
	 * Tests for transmission enquiry data
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NullObject() throws JsonFormatException {
		//testing null object
		request.setTransactionEnquiryData(null);
		unit.validate(request);
	}
	
	/**
	 * Tests for transmission enquiry data original retrieval reference
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_OriginalRetrievalReference_NullValue() throws JsonFormatException {
		//testing null value
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_OriginalRetrievalReference_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_OriginalRetrievalReference_LengthMoreThan20() throws JsonFormatException {
		//testing length > 20
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("112233445566778899001");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_OriginalRetrievalReference_LengthLessThan20() throws JsonFormatException {
		//testing length < 20
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("112233445566778899");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_TransactionEnquiryData_OriginalRetrievalReference_LengthEqualTo20() throws JsonFormatException {
		//testing length == 20
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("11223344556677889900");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data host mid
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_HostMid_LengthMoreThan15() throws JsonFormatException {
		//testing length > 15 
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setHostMid("1122334455667788");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_TransactionEnquiryData_HostMid_LengthLessThan15() throws JsonFormatException {
		//testing length < 15 
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setHostMid("11223344556677");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_TransactionEnquiryData_HostMid_LengthEqualTo15() throws JsonFormatException {
		//testing length == 15 
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setHostMid("112233445566778");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data host tid
	 * 
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_HostTid_LengthMoreThan8() throws JsonFormatException {
		//testing length > 8 
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setHostTid("112233445");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_HostTid_LengthLessThan8() throws JsonFormatException {
		//testing length < 8 
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setHostTid("1122334");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_HostTid_LengthEqualTo8() throws JsonFormatException {
		//testing length == 8 
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setHostTid("11223344");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data nets paynow mpan
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_AlphaValue() throws JsonFormatException {
		//testing alpha value
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("abcdefghijklmnop");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("abcdefgh12345678");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_NegativeValue() throws JsonFormatException {
		//testing negative value
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("-123456787654321");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_Symbols() throws JsonFormatException {
		//testing symbols
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("!@#$%^&*!@#$%^&*");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_Whitespace() throws JsonFormatException {
		//testing symbols
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("112233 44 556677");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_Decimal() throws JsonFormatException {
		//testing decimal
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("11223344.5566778");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_LengthMoreThan16() throws JsonFormatException {
		//testing length > 16
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("11223344556677889");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_LengthLessThan16() throws JsonFormatException {
		//testing length < 16
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("112233445566778");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_NetsPaynowMpan_LengthEqualTo16() throws JsonFormatException {
		//testing length == 16
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNetsPaynowMpan("1122334455667788");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data bank merchant proxy
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_BankMerchantProxy_LengthMoreThan100() throws JsonFormatException {
		//testing length > 100
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setBankMerchantProxy("X1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_BankMerchantProxy_LengthLessThan100() throws JsonFormatException {
		//testing length < 100
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setBankMerchantProxy("123abc");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_BankMerchantProxy_LengthEqualTo100() throws JsonFormatException {
		//testing length == 100
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setBankMerchantProxy("1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data bank merchant proxy type
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_BankMerchantProxyType_LengthMoreThan20() throws JsonFormatException {
		//testing length > 20
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setBankMerchantProxyType("1234567890abcdefghijk");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_BankMerchantProxyType_LengthLessThan20() throws JsonFormatException {
		//testing length < 20
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setBankMerchantProxyType("1234567890abcdefghi");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_BankMerchantProxyType_LengthEqualTo20() throws JsonFormatException {
		//testing length == 20
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setBankMerchantProxyType("1234567890abcdefghij");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data merchant reference number
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_MerchantReferenceNumber_LengthMoreThan40() throws JsonFormatException {
		//testing length > 40
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setMerchantReferenceNumber("12345678901234567890123456789012345678901");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_MerchantReferenceNumber_LengthLessThan40() throws JsonFormatException {
		//testing length < 40
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setMerchantReferenceNumber("123456789012345678901234567890123456789");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateReversalRequest_TransactionEnquiryData_MerchantReferenceNumber_LengthEqualTo40() throws JsonFormatException {
		//testing length == 40
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data invoice ref
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateReversalRequest_TransactionEnquiryData_InvoiceRef_LengthMoreThan16() throws JsonFormatException {
		//testing length > 16
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setInvoiceRef("11223344556677889");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_TransactionEnquiryData_InvoiceRef_LengthLessThan16() throws JsonFormatException {
		//testing length < 16
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setInvoiceRef("112233445566778");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test 
	public void validateReversalRequest_TransactionEnquiryData_InvoiceRef_LengthEqualTo16() throws JsonFormatException {
		//testing length == 16
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setInvoiceRef("1122334455667788");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction enquiry data npx data
	 */
	
	@Test 
	public void validateReversalRequest_TransactionEnquiryData_NpxData() throws JsonFormatException {
		JsonNpxData npxData = new JsonNpxData();
		transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setNpxData(npxData);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
}
