package com.nets.nps.paynow.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.nets.nps.paynow.entity.TransactionEnquiryData;
import com.nets.nps.paynow.entity.TransactionEnquiryRequest;
import com.nets.nps.paynow.service.impl.TransactionEnquiryRequestValidator;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class TransactionEnquiryRequestValidationTest {

	private TransactionEnquiryRequest request;
	private TransactionEnquiryData transactionEnquiryData;
	private TransactionEnquiryRequestValidator unit;
	/*
	 * Method to create a sample TransactionEnquiryRequest for testing purposes
	 */
	
	private TransactionEnquiryRequest createTransactionEnquiryRequest() {
		TransactionEnquiryData transactionEnquiryData = new TransactionEnquiryData();
		transactionEnquiryData.setReceivingAccountNumber("330555522334564321321345");
		transactionEnquiryData.setReceivingAccountType("OWN");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		TransactionEnquiryRequest request = new TransactionEnquiryRequest();
		request.setRetrievalRef("12345678128");
		request.setInstitutionCode("30000000033");
		request.setTransmissionTime("0608123413");
		request.setTransactionType("PNC");
		request.setTransactionEnquiryData(transactionEnquiryData);
		return request;
	}
	
	@Before
	public void setUp() {
		request = createTransactionEnquiryRequest();
		transactionEnquiryData = new TransactionEnquiryData();
		unit = new TransactionEnquiryRequestValidator();
	}
	
	@Test
	public void validateTransactionEnquiryRequest() throws JsonFormatException {
		unit.validate(request);
	}
	
	/*
	 * Test for retrieval ref
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestRetrievalRefNullValue() throws JsonFormatException {
		//Testing null value for retrieval ref
		request.setRetrievalRef(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestRetrievalRefEmptyValue() throws JsonFormatException {
		//Testing empty value for retrieval ref
		request.setRetrievalRef("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestRetrievalRefLengthMoreThan20() throws JsonFormatException {
		//Testing length more than 20
		request.setRetrievalRef("abcdabcdabcd123412341234");
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestRetrievalRefLengthLessThan20() throws JsonFormatException {
		//Testing length less than 20
		request.setRetrievalRef("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestRetrievalRefLengthEqual20() throws JsonFormatException {
		//Testing length equal 20
		request.setRetrievalRef("12345123451234512345");
		unit.validate(request);
	}
	
	/*
	 * Test for institution code
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeNullValue() throws JsonFormatException {
		//Testing null value for institution code
		request.setInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeEmptyValue() throws JsonFormatException {
		//Testing empty value for institution code
		request.setInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeAlphaValue() throws JsonFormatException {
		//Testing alpha value for institution code
		request.setInstitutionCode("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeAlphaNumericValue() throws JsonFormatException {
		//Testing alpha numeric value for institution code
		request.setInstitutionCode("abcdabcd123");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeNegativeValue() throws JsonFormatException {
		//Testing negative value for institution code
		request.setInstitutionCode("-30000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeSymbols() throws JsonFormatException {
		//Testing symbols value for institution code
		request.setInstitutionCode("!@!@!@!@!@!");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeWhiteSpace() throws JsonFormatException {
		//Testing white space for institution code
		request.setInstitutionCode("30000 000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeDecimalPoint() throws JsonFormatException {
		//Testing decimal point for institution code
		request.setInstitutionCode("30000.000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeLengthMoreThan11() throws JsonFormatException {
		//Testing length more than 11 for institution code
		request.setInstitutionCode("300000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestInstitutionCodeLengthLessThan11() throws JsonFormatException {
		//Testing length less than 11 for institution code
		request.setInstitutionCode("3000000033");
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestInstitutionCodeLengthEquals11() throws JsonFormatException {
		//Testing length equal 11 for institution code
		request.setInstitutionCode("30000000033");
		unit.validate(request);
	}
	
	/*
	 * Test for transmission time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeNullValue() throws JsonFormatException {
		// Testing null value for transmission time
		request.setTransmissionTime(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeEmptyValue() throws JsonFormatException {
		// Testing empty value for transmission time
		request.setTransmissionTime("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeAlphaNumericValue() throws JsonFormatException {
		//Testing alpha numeric value for transmission time
		request.setTransmissionTime("abc8123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeMonthValue() throws JsonFormatException {
		// Testing month value for transmission time
		request.setTransmissionTime("1308123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeDayValue() throws JsonFormatException {
		// Testing day value for transmission time
		request.setTransmissionTime("1233123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeHourValue() throws JsonFormatException {
		// Testing hour value for transmission time
		request.setTransmissionTime("1230253456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeMinuteValue() throws JsonFormatException {
		// Testing minute value for transmission time
		request.setTransmissionTime("1230126656");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeSecondValue() throws JsonFormatException {
		// Testing second value for transmission time
		request.setTransmissionTime("1230123466");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeLengthMoreThan10() throws JsonFormatException {
		// Testing length more than 10 for transmission time
		request.setTransmissionTime("12301234561");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransmissionTimeLengthLessThan10() throws JsonFormatException {
		// Testing less than 10 for transmission time
		request.setTransmissionTime("123012345");
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestTransmissionTimeLengthEqual10() throws JsonFormatException {
		// Testing equal 10 for transmission time
		request.setTransmissionTime("1230123456");
		unit.validate(request);
	}
	
	/*
	 * Test for Transaction type
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransactionTypeNullValue() throws JsonFormatException {
		// Testing null value for transaction type
		request.setTransactionType(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransactionTypeEmptyValue() throws JsonFormatException {
		// Testing empty value for transaction type
		request.setTransactionType("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransactionTypeLengthMoreThan3() throws JsonFormatException {
		// Testing length more than 3 for transaction type
		request.setTransactionType("abvcd");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestTransactionTypeLengthLessThan3() throws JsonFormatException {
		// Testing length less than 3 for transaction type
		request.setTransactionType("ab");
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestTransactionTypeLengthEqual3() throws JsonFormatException {
		// Testing length equal 3 for transaction type
		request.setTransactionType("abc");
		unit.validate(request);
	}
	
	/*
	 * Test for transaction enquiry data
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataNullObject() throws JsonFormatException {
		// Testing null txn enquiry data 
		request.setTransactionEnquiryData(null);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction enquiry data receiving account number
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccNumNullValue() throws JsonFormatException {
		// Testing null for txn enquiry data receiving acc num
		transactionEnquiryData.setReceivingAccountNumber(null);
		transactionEnquiryData.setReceivingAccountType("OWN");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccNumEmptyValue() throws JsonFormatException {
		// Testing empty for txn enquiry data receiving acc num
		transactionEnquiryData.setReceivingAccountNumber("");
		transactionEnquiryData.setReceivingAccountType("OWN");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccNumLengthMoreThan40() throws JsonFormatException {
		// Testing length more than 40 for txn enquiry data receiving acc num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij");
		transactionEnquiryData.setReceivingAccountType("OWN");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccNumLengthLessThan40() throws JsonFormatException {
		// Testing length less than 40 for txn enquiry data receiving acc num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("OWN");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccNumLengthEqual40() throws JsonFormatException {
		// Testing length equal 40 for txn enquiry data receiving acc num
		transactionEnquiryData.setReceivingAccountNumber("abcedfghijabcedfghijabcedfghijabcedfghij");
		transactionEnquiryData.setReceivingAccountType("OWN");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction enquiry data receiving account type
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccTypeNullValue() throws JsonFormatException {
		// Testing null for txn enquiry data receiving acc type
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType(null);
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccTypeEmptyValue() throws JsonFormatException {
		// Testing empty for txn enquiry data receiving acc type
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccTypeLengthMoreThan3() throws JsonFormatException {
		// Testing length more than 3 for txn enquiry data receiving acc type
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABCDD");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccTypeLengthLessThan3() throws JsonFormatException {
		// Testing length less than 3 for txn enquiry data receiving acc type
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("AB");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataReceivingAccTypeLengthEqual3() throws JsonFormatException {
		// Testing length equal 3 for txn enquiry data receiving acc type
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction enquiry data merchant reference number
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataMerchantRefNumNullValue() throws JsonFormatException {
		// Testing null for txn enquiry data merchant ref num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber(null);
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataMerchantRefNumEmptyValue() throws JsonFormatException {
		// Testing empty for txn enquiry data merchant ref num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataMerchantRefNumLengthMoreThan40() throws JsonFormatException {
		// Testing length more than 40 for txn enquiry data merchant ref num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890123");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataMerchantRefNumLengthLessThan40() throws JsonFormatException {
		// Testing length less than 40 for txn enquiry data merchant ref num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("12345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataMerchantRefNumLengthEqual40() throws JsonFormatException {
		// Testing length equal 40 for txn enquiry data merchant ref num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0407111111");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/*
	 * Testing txn enquiry data crediting start time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeNullValue() throws JsonFormatException {
		// Testing null value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime(null);
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeEmptyValue() throws JsonFormatException {
		// Testing null value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("abcde12345");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeNegativeValue() throws JsonFormatException {
		// Testing negative value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("-1212121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeMonthValue() throws JsonFormatException {
		// Testing month value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("1312121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeDayValue() throws JsonFormatException {
		// Testing day value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("1233121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeHourValue() throws JsonFormatException {
		// Testing hour value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("1212251212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeMinuteValue() throws JsonFormatException {
		// Testing minute value for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("1212126112");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeSecondValue() throws JsonFormatException {
		// Testing sec value for txn enquiry data merchant ref num
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("1212121261");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeLengthMoreThan10() throws JsonFormatException {
		// Testing length more than 10 for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("121212121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeLengthLessThan10() throws JsonFormatException {
		// Testing length less than 10 for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("12121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingStartTimeLengthEqual10() throws JsonFormatException {
		// Testing length equal 10 for txn enquiry data crediting start time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("1212121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	/*
	 * Testing txn enquiry data crediting end time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeNullValue() throws JsonFormatException {
		// Testing null value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime(null);
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeEmptyValue() throws JsonFormatException {
		// Testing empty value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeAlphaNumericValue() throws JsonFormatException {
		// Testing alpha numeric value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("0407ab1212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeNegativeValue() throws JsonFormatException {
		// Testing negative value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("-0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeMonthValue() throws JsonFormatException {
		// Testing month value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("1307121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeDayValue() throws JsonFormatException {
		// Testing day value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("0433121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeHourValue() throws JsonFormatException {
		// Testing hour value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("0407251212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeMinuteValue() throws JsonFormatException {
		// Testing minute value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("0407126612");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeSecondValue() throws JsonFormatException {
		// Testing sec value for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121266");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeLengthMoreThan10() throws JsonFormatException {
		// Testing length more than 10 for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("040712121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeLengthLessThan10() throws JsonFormatException {
		// Testing length less than 10 for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("040712121");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionEnquiryRequestForTransactionEnquiryDataCreditingEndTimeLengthEqual10() throws JsonFormatException {
		// Testing length equal 10 for txn enquiry data crediting end time
		transactionEnquiryData.setReceivingAccountNumber("abcdefghijabcdef");
		transactionEnquiryData.setReceivingAccountType("ABC");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345678901234567890");
		transactionEnquiryData.setCreditingStartTime("0408121212");
		transactionEnquiryData.setCreditingEndTime("0407121212");
		request.setTransactionEnquiryData(transactionEnquiryData);
		unit.validate(request);
	}
	
	
	
	
	
	
	
	
}
