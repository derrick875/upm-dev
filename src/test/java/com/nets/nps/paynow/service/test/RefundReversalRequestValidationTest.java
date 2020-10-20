package com.nets.nps.paynow.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.nets.nps.paynow.entity.RefundReversalRequest;
import com.nets.nps.paynow.entity.RefundReversalTransactionDomainData;
import com.nets.nps.paynow.service.impl.RefundReversalRequestValidator;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class RefundReversalRequestValidationTest {

	private RefundReversalRequest request;
	private RefundReversalTransactionDomainData transactionDomainData;
	private RefundReversalRequestValidator unit;
	
	@Before
	public void setUp() {
		request = createRefundReversalRequest();
		transactionDomainData = request.getTransactionDomainData();
		unit = new RefundReversalRequestValidator();
	}
	
	/**
	 * Method to create a sample RefundReversalRequest for testing purposes
	 */
	private RefundReversalRequest createRefundReversalRequest() {
		RefundReversalTransactionDomainData transactionDomainData = new RefundReversalTransactionDomainData();
		transactionDomainData.setCreditAmount("112233445566");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("0622123456");
		transactionDomainData.setReceivingAccountNumber("1234567890123456789012345678901234567890");
		transactionDomainData.setReceivingAccountType("OWN");
		transactionDomainData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionDomainData.setReversalRefundAmount("112233445566");
		transactionDomainData.setReversalRefundCurrency("SGD");
		transactionDomainData.setReversalRefundTarget("AC");
		transactionDomainData.setSendingAccountBank("1234567890123456789012345678901234567890");
		transactionDomainData.setSendingAccountNumber("1234567890123456789012345678901234567890");
		transactionDomainData.setSendingAccountName("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij");
		transactionDomainData.setSendingProxy("abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890");
		transactionDomainData.setSendingProxyType("MOBILE");
		transactionDomainData.setOriginalRetrievalReference("abcdefghij1234567890");
		transactionDomainData.setAdditionalBankReference("abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890");
		transactionDomainData.setReversalRefundReference("abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890abcdefghij1234567890");
		RefundReversalRequest request = new RefundReversalRequest();
		request.setRetrievalRef("11223344556677889900");
		request.setInstitutionCode("12345654321");
		request.setTransmissionTime("0622123456");
		request.setTransactionType("REF");
		request.setTransactionDomainData(transactionDomainData);
		
		return request;
	}
	
	@Test
	public void validateRefundReversalRequest() throws JsonFormatException {
		unit.validate(request);
	}
	
	/**
	 * Tests for retrieval ref
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_RetrievalRef_NullValue() throws JsonFormatException {
		//testing null value
		request.setRetrievalRef(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_RetrievalRef_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setRetrievalRef("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_RetrievalRef_LengthMoreThan20() throws JsonFormatException {
		//testing length > 20
		request.setRetrievalRef("1234567890ABCDEFGHIJKLMNOP");
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_RetrievalRef_LengthLessThan20() throws JsonFormatException {
		//testing length < 20
		request.setRetrievalRef("1234ABCD");
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_RetrievalRef_LengthEqualTo20() throws JsonFormatException {
		//testing length == 20
		request.setRetrievalRef("1234567890ABCDEFGHIJ");
		unit.validate(request);
	}
	
	/**
	 * Tests for institution code
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_NullValue() throws JsonFormatException {
		//testing null value
		request.setInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_AlphaValue() throws JsonFormatException {
		//testing alpha value
		request.setInstitutionCode("abcdefghijk");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		request.setInstitutionCode("abcdef12345");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_NegativeValue() throws JsonFormatException {
		//testing negative value
		request.setInstitutionCode("-1234567890");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_Symbols() throws JsonFormatException {
		//testing symbols
		request.setInstitutionCode("~!@#$%^&*()");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_WhiteSpace() throws JsonFormatException {
		//testing whitespace
		request.setInstitutionCode("12345 54321");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_DecimalValue() throws JsonFormatException {
		//testing decimal
		request.setInstitutionCode("12345.54321");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_LengthMoreThan11() throws JsonFormatException {
		//testing length > 11
		request.setInstitutionCode("112233445566");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_InstitutionCode_LengthLessThan11() throws JsonFormatException {
		//testing length < 11
		request.setInstitutionCode("1122334455");
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_InstitutionCode_EqualTo11() throws JsonFormatException {
		//testing length == 11
		request.setInstitutionCode("12345654321");
		unit.validate(request);
	}
	
	/**
	 * Tests for transmission time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_NullValue() throws JsonFormatException {
		//testing null value
		request.setTransmissionTime(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setTransmissionTime("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_AlphaValue() throws JsonFormatException {
		//testing alpha value
		request.setTransmissionTime("abcdefghij");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		request.setTransmissionTime("abcde12345");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_Symbols() throws JsonFormatException {
		//testing symbols
		request.setTransmissionTime("!@#$%^&*()");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_WhiteSpace() throws JsonFormatException {
		//testing whitespace value
		request.setTransmissionTime("0618 10154");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_Decimal() throws JsonFormatException {
		//testing decimal value
		request.setTransmissionTime("0618.10154");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_NegativeValue() throws JsonFormatException {
		//testing negative value
		request.setTransmissionTime("-180610154");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_MonthValueMoreThan12() throws JsonFormatException {
		//testing month value > 12
		request.setTransmissionTime("1318101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_MonthValueLessThan1() throws JsonFormatException {
		//testing month value < 1
		request.setTransmissionTime("0018101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_DayValueMoreThan31() throws JsonFormatException {
		//testing day value > 31
		request.setTransmissionTime("0632101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransmissionTime_DayValueMoreThan30For30DayMonth() throws JsonFormatException {
		//testing day value > 30 for 30day month
		request.setTransmissionTime("0631101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_DayValueMoreThan29ForFebruary() throws JsonFormatException {
		//testing day value < 29 for February
		request.setTransmissionTime("0230101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_DayValueLessThan1() throws JsonFormatException {
		//testing day value < 1
		request.setTransmissionTime("0600101545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_HourValueMoreThan23() throws JsonFormatException {
		//testing hour value > 23
		request.setTransmissionTime("0618241545");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_MinuteValueMoreThan60() throws JsonFormatException {
		//testing minute value > 60
		request.setTransmissionTime("0618106145");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_SecondValueMoreThan60() throws JsonFormatException {
		//testing second value > 60
		request.setTransmissionTime("0618101561");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_LengthMoreThan10() throws JsonFormatException {
		//testing length > 60
		request.setTransmissionTime("06181015453");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransmissionTime_LengthLessThan10() throws JsonFormatException {
		//testing length < 10
		request.setTransmissionTime("061810154");
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransmissionTime_LengthEqualTo10() throws JsonFormatException {
		//testing length == 10
		request.setTransmissionTime("0618101545");
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction type
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionType_NullValue() throws JsonFormatException {
		//testing null value
		request.setTransactionType(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionType_EmptyValue() throws JsonFormatException {
		//testing empty value
		request.setTransactionType("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionType_LengthMoreThan3() throws JsonFormatException {
		//testing length > 3
		request.setTransactionType("1234");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionType_LengthLessThan3() throws JsonFormatException {
		//testing length < 3
		request.setTransactionType("12");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionType_LengthEqualTo3AndInvalidInput() throws JsonFormatException {
		//testing length == 3 but invalid input
		request.setTransactionType("123");
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionType_LengthEqualTo3AndValidInputREF() throws JsonFormatException {
		//testing length == 3 and valid input REF
		request.setTransactionType("REF");
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionType_LengthEqualTo3AndValidInputREV() throws JsonFormatException {
		//testing length == 3 and valid input REV
		request.setTransactionType("REV");
		unit.validate(request);
	}

	/**
	 * Tests for transaction domain data
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_NullObject() throws JsonFormatException {
		//testing null object
		request.setTransactionDomainData(null);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data credit amount
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setCreditAmount(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setCreditAmount("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_AlphaValue() throws JsonFormatException {
		//testing alpha value
		transactionDomainData.setCreditAmount("abcdefghijkl");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		transactionDomainData.setCreditAmount("abcdef123456");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_Symbols() throws JsonFormatException {
		//testing symbols
		transactionDomainData.setCreditAmount("!@#$%^&*()_+");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_WhiteSpace() throws JsonFormatException {
		//testing symbols
		transactionDomainData.setCreditAmount("123456 12345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_Decimal() throws JsonFormatException {
		//testing decimal
		transactionDomainData.setCreditAmount("123456.12345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_NegativeValue() throws JsonFormatException {
		//testing negative value
		transactionDomainData.setCreditAmount("-12345612345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_LengthMoreThan12() throws JsonFormatException {
		//testing length > 12
		transactionDomainData.setCreditAmount("1234561234561");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_LengthLessThan12() throws JsonFormatException {
		//testing length < 12
		transactionDomainData.setCreditAmount("1234561234561");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_CreditAmount_LengthEqualTo12() throws JsonFormatException {
		//testing length == 12
		transactionDomainData.setCreditAmount("123456123456");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data credit currency
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditCurrency_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setCreditCurrency(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditCurrency_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setCreditCurrency("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditCurrency_LengthMoreThan3() throws JsonFormatException {
		//testing length > 3
		transactionDomainData.setCreditCurrency("ABCD");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditCurrency_LengthLessThan3() throws JsonFormatException {
		//testing length < 3
		transactionDomainData.setCreditCurrency("AB");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_CreditCurrency_LengthEqualTo3() throws JsonFormatException {
		//testing length == 3
		transactionDomainData.setCreditCurrency("ABC");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data crediting time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setCreditingTime(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setCreditingTime("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime_AlphaValue() throws JsonFormatException {
		//testing alpha value	
		transactionDomainData.setCreditingTime("abcdefghij");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value00000000
		transactionDomainData.setCreditingTime("abcde12345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime__Symbols() throws JsonFormatException {
		//testing symbols
		transactionDomainData.setCreditingTime("!@#$%^&*()");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime__WhiteSpace() throws JsonFormatException {
		//testing whitespace value
		transactionDomainData.setCreditingTime("0618 10154");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__TransmissionTime_Decimal() throws JsonFormatException {
		//testing decimal value
		transactionDomainData.setCreditingTime("0618.10154");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime__NegativeValue() throws JsonFormatException {
		//testing negative value
		transactionDomainData.setCreditingTime("-061810154");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime__MonthValueMoreThan12() throws JsonFormatException {
		//testing month value > 12
		transactionDomainData.setCreditingTime("1318101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__MonthValueLessThan1() throws JsonFormatException {
		//testing month value < 1
		transactionDomainData.setCreditingTime("0018101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime__DayValueMoreThan31() throws JsonFormatException {
		//testing day value > 31
		transactionDomainData.setCreditingTime("0632101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_CreditingTime__DayValueMoreThan30For30DayMonth() throws JsonFormatException {
		//testing day value > 30 for 30day month
		transactionDomainData.setCreditingTime("0631101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__DayValueMoreThan29ForFebruary() throws JsonFormatException {
		//testing day value < 29 for February
		transactionDomainData.setCreditingTime("0230101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__DayValueLessThan1() throws JsonFormatException {
		//testing day value < 1
		transactionDomainData.setCreditingTime("0600101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__HourValueMoreThan23() throws JsonFormatException {
		//testing hour value > 23
		transactionDomainData.setCreditingTime("0618241545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__MinuteValueMoreThan60() throws JsonFormatException {
		//testing minute value > 60
		transactionDomainData.setCreditingTime("0618106145");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__SecondValueMoreThan60() throws JsonFormatException {
		//testing second value > 60
		transactionDomainData.setCreditingTime("0618101561");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__LengthMoreThan10() throws JsonFormatException {
		//testing length > 60
		transactionDomainData.setCreditingTime("06181015453");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__LengthLessThan10() throws JsonFormatException {
		//testing length < 10
		transactionDomainData.setCreditingTime("061810154");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_CreditingTime__LengthEqualTo10() throws JsonFormatException {
		//testing length == 10
		transactionDomainData.setCreditingTime("0618101545");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data receiving account number
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountNumber_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setReceivingAccountNumber(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountNumber_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setReceivingAccountNumber("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountNumber_LengthMoreThan40() throws JsonFormatException {
		//testing length > 40
		transactionDomainData.setReceivingAccountNumber("1234567890abcdefghij1234567890abcdefghij1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountNumber_LengthLessThan40() throws JsonFormatException {
		//testing length < 40
		transactionDomainData.setReceivingAccountNumber("1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountNumber_LengthEqualTo40() throws JsonFormatException {
		//testing length == 40
		transactionDomainData.setReceivingAccountNumber("1234567890abcdefghij1234567890abcdefghij");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data receiving account type
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setReceivingAccountType(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setReceivingAccountType("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_LengthMoreThan3() throws JsonFormatException {
		//testing length > 3
		transactionDomainData.setReceivingAccountType("OWNO");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_LengthLessThan3() throws JsonFormatException {
		//testing length < 3
		transactionDomainData.setReceivingAccountType("OW");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_LengthEqualTo3AndInvalidArgument() throws JsonFormatException {
		//testing length == 3 but wrong argument
		transactionDomainData.setReceivingAccountType("OWE");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_LengthEqualTo3AndArgumentOWN() throws JsonFormatException {
		//testing length == 3 and correct argument OWN
		transactionDomainData.setReceivingAccountType("OWN");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_ReceivingAccountType_LengthEqualTo3AndArgumentOBO() throws JsonFormatException {
		//testing length == 3 and correct argument OBO
		transactionDomainData.setReceivingAccountType("OBO");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data merchant reference number
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_MerchantReferenceNumber_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setMerchantReferenceNumber(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_MerchantReferenceNumber_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setMerchantReferenceNumber("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_MerchantReferenceNumber_LengthMoreThan40() throws JsonFormatException {
		//testing length > 40
		transactionDomainData.setMerchantReferenceNumber("1234567890abcdefghij1234567890abcdefghij1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_MerchantReferenceNumber_LengthLessThan40() throws JsonFormatException {
		//testing length < 40
		transactionDomainData.setMerchantReferenceNumber("1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_MerchantReferenceNumber_LengthEqualTo40() throws JsonFormatException {
		//testing length == 40
		transactionDomainData.setMerchantReferenceNumber("1234567890abcdefghij1234567890abcdefghij");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data reversal refund amount
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setReversalRefundAmount(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setReversalRefundAmount("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_AlphaValue() throws JsonFormatException {
		//testing alpha value
		transactionDomainData.setReversalRefundAmount("abcdefghijkl");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_AlphaNumericValue() throws JsonFormatException {
		//testing alphanumeric value
		transactionDomainData.setReversalRefundAmount("abcdef123456");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_Symbols() throws JsonFormatException {
		//testing symbols
		transactionDomainData.setReversalRefundAmount("!@#$%^&*()_+");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_WhiteSpace() throws JsonFormatException {
		//testing symbols
		transactionDomainData.setReversalRefundAmount("123456 12345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_Decimal() throws JsonFormatException {
		//testing decimal
		transactionDomainData.setReversalRefundAmount("123456.12345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_NegativeValue() throws JsonFormatException {
		//testing negative value
		transactionDomainData.setReversalRefundAmount("-12345612345");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_LengthMoreThan12() throws JsonFormatException {
		//testing length > 12
		transactionDomainData.setReversalRefundAmount("1234561234561");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_LengthLessThan12() throws JsonFormatException {
		//testing length < 12
		transactionDomainData.setReversalRefundAmount("1234561234561");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundAmount_LengthEqualTo12() throws JsonFormatException {
		//testing length == 12
		transactionDomainData.setReversalRefundAmount("123456123456");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data reversal refund currency
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundCurrency_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setReversalRefundCurrency(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundCurrency_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setReversalRefundCurrency("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundCurrency_LengthMoreThan3() throws JsonFormatException {
		//testing length > 3
		transactionDomainData.setReversalRefundCurrency("ABCD");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundCurrency_LengthLessThan3() throws JsonFormatException {
		//testing length < 3
		transactionDomainData.setReversalRefundCurrency("AB");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundCurrency_LengthEqualTo3() throws JsonFormatException {
		//testing length == 3
		transactionDomainData.setReversalRefundCurrency("ABC");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 *Tests for transaction domain data reversal refund target
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setReversalRefundTarget(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setReversalRefundTarget("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_LengthMoreThan2() throws JsonFormatException {
		//testing length > 3
		transactionDomainData.setReversalRefundTarget("ACC");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_LengthLessThan2() throws JsonFormatException {
		//testing length < 3
		transactionDomainData.setReversalRefundTarget("A");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_LengthEqualTo2AndInvalidArgument() throws JsonFormatException {
		//testing length == 2 but wrong argument
		transactionDomainData.setReversalRefundTarget("AB");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_LengthEqualTo2AndArgumentAC() throws JsonFormatException {
		//testing length == 2 and correct argument AC
		transactionDomainData.setReversalRefundTarget("AC");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void validateRefundReversalRequest_TransactionDomainData_ReversalRefundTarget_LengthEqualTo2AndArgumentPN() throws JsonFormatException {
		//testing length == 2 and correct argument OBO
		transactionDomainData.setReversalRefundTarget("PN");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data sending account bank
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountBank_LengthMoreThan40() throws JsonFormatException{
		//testing length > 40
		transactionDomainData.setSendingAccountBank("1234567890abcdefghij1234567890abcdefghij1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountBank_LengthLessThan40() throws JsonFormatException{
		//testing length < 40
		transactionDomainData.setSendingAccountBank("1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountBank_LengthEqualTo40() throws JsonFormatException{
		//testing length == 40
		transactionDomainData.setSendingAccountBank("1234567890123456789012345678901234567890");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data sending account number
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountNumber_LengthMoreThan40() throws JsonFormatException{
		//testing length > 40
		transactionDomainData.setSendingAccountNumber("12345678901234567890123456789012345678901");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountNumber_LengthLessThan40() throws JsonFormatException{
		//testing length < 40
		transactionDomainData.setSendingAccountNumber("1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountNumber_LengthEqualTo40() throws JsonFormatException{
		//testing length == 40
		transactionDomainData.setSendingAccountNumber("1234567890123456789012345678901234567890");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data sending account name
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountName_LengthMoreThan80() throws JsonFormatException{
		//testing length > 80
		transactionDomainData.setSendingAccountName("123456789012345678901234567890123456789012345678901234567890123456789012345678901");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountName_LengthLessThan80() throws JsonFormatException{
		//testing length < 80
		transactionDomainData.setSendingAccountName("1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingAccountName_LengthEqualTo80() throws JsonFormatException{
		//testing length == 80
		transactionDomainData.setSendingAccountName("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data sending proxy
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxy_LengthMoreThan100() throws JsonFormatException{
		//testing length > 100
		transactionDomainData.setSendingProxy("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxy_LengthLessThan80() throws JsonFormatException{
		//testing length < 100
		transactionDomainData.setSendingProxy("1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxy_LengthEqualTo100() throws JsonFormatException{
		//testing length == 100
		transactionDomainData.setSendingProxy("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data sending proxy type
	 */
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxyType_LengthMoreThan20() throws JsonFormatException{
		//testing length > 20
		transactionDomainData.setSendingProxyType("1234567890abcdefghij1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test (expected = JsonFormatException.class)
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxyType_LengthLessThan20AndInvalidArgument() throws JsonFormatException{
		//testing length < 20 and invalid argument
		transactionDomainData.setSendingProxyType("invalid");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxyType_LengthLessThan20AndArgumentMOBILE() throws JsonFormatException{
		//testing length < 20 and valid argument MOBILE
		transactionDomainData.setSendingProxyType("MOBILE");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxyType_LengthLessThan20AndArgumentNRIC() throws JsonFormatException{
		//testing length < 20 and valid argument NRIC
		transactionDomainData.setSendingProxyType("NRIC");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test 
	public void validateRefundReversalRequest_TransactionDomainData_SendingProxyType_LengthLessThan20AndArgumentUEN() throws JsonFormatException{
		//testing length < 20 and valid argument UEN
		transactionDomainData.setSendingProxyType("UEN");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data original retrieval reference
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_OriginalRetrievalReference_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setOriginalRetrievalReference(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_OriginalRetrievalReference_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setOriginalRetrievalReference("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_OriginalRetrievalReference_LengthMoreThan20() throws JsonFormatException {
		//testing length > 20
		transactionDomainData.setOriginalRetrievalReference("1234567890ABCDEFGHIJKLMNOP");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionDomainData_OriginalRetrievalReference_LengthLessThan20() throws JsonFormatException {
		//testing length < 20
		transactionDomainData.setOriginalRetrievalReference("1234ABCD");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionDomainData_OriginalRetrievalReference_LengthEqualTo20() throws JsonFormatException {
		//testing length == 20
		transactionDomainData.setOriginalRetrievalReference("1234567890ABCDEFGHIJ");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data additional bank reference
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_AdditionalBankReference_NullValue() throws JsonFormatException {
		//testing null value
		transactionDomainData.setAdditionalBankReference(null);
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_AdditionalBankReference_EmptyValue() throws JsonFormatException {
		//testing empty value
		transactionDomainData.setAdditionalBankReference("");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_AdditionalBankReference_LengthMoreThan200() throws JsonFormatException {
		//testing length > 200
		transactionDomainData.setAdditionalBankReference("1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionDomainData_AdditionalBankReference_LengthLessThan200() throws JsonFormatException {
		//testing length < 200
		transactionDomainData.setAdditionalBankReference("1234ABCD");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionDomainData_AdditionalBankReference_LengthEqualTo200() throws JsonFormatException {
		//testing length == 200
		transactionDomainData.setAdditionalBankReference("1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	/**
	 * Tests for transaction domain data reversal refund reference
	 */
	
	@Test(expected = JsonFormatException.class)
	public void  validateRefundReversalRequest_TransactionDomainData_ReversalRefundReference_LengthMoreThan200() throws JsonFormatException {
		//testing length > 200
		transactionDomainData.setReversalRefundReference("1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionDomainData_ReversalRefundReference_LengthLessThan200() throws JsonFormatException {
		//testing length < 200
		transactionDomainData.setReversalRefundReference("1234ABCD");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
	@Test
	public void  validateRefundReversalRequest_TransactionDomainData_ReversalRefundReference_LengthEqualTo200() throws JsonFormatException {
		//testing length == 200
		transactionDomainData.setReversalRefundReference("1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ1234567890ABCDEFGHIJ");
		request.setTransactionDomainData(transactionDomainData);
		unit.validate(request);
	}
	
}

