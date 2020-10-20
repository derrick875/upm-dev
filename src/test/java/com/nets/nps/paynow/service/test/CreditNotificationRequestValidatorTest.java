package com.nets.nps.paynow.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.PaymentTransactionDomainData;
import com.nets.nps.paynow.service.impl.CreditNotificationRequestValidator;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class CreditNotificationRequestValidatorTest {
	
	private CreditNotificationRequest request;
	private CreditNotificationRequestValidator unit;
	private PaymentTransactionDomainData txnData;
	
	@Before
	public void setUp() {
		request=createJsonCreditNotificationRequest();
		txnData = new PaymentTransactionDomainData();
		unit=new CreditNotificationRequestValidator();
	}
	
	@Test
	public void validateJsonCreditNotificationRequest() throws JsonFormatException {
		unit.validate(request);
	}
	
	/*
	 * Tests For RRN
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateJsonCreditNotificationRequestRRNNullValue() throws JsonFormatException {
		request.setRetrievalRef(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateJsonCreditNotificationRequestRRNEmptyValue() throws JsonFormatException {
		request.setRetrievalRef("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public final void validateCreditNotificationRequestInvalidFailedWithLongRetrivalRef() throws JsonFormatException {
		request.setRetrievalRef("123456789012345678901");
		unit.validate(request);
	}
	
	@Test
	public final void validateCreditNotificationRequestJsonWithRetrivalRefLengthLessThan20() throws JsonFormatException {
		request.setRetrievalRef("123456789012345678");
		unit.validate(request);
	}

	@Test
	public final void validateCreditNotificationRequestJsonWithRetrivalRefLength20() throws JsonFormatException {
		request.setRetrievalRef("12345678901234567890");
		unit.validate(request);
	}
	
	/*
	 * Test for institution code
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeNullValue() throws JsonFormatException {
		request.setInstitutionCode(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeEmptyValue() throws JsonFormatException {
		request.setInstitutionCode("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeAlphaValue() throws JsonFormatException {
		request.setInstitutionCode("abcdabcdabc");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeAlphaNumericValue() throws JsonFormatException {
		request.setInstitutionCode("abcdabcd123");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeNegativeValue() throws JsonFormatException {
		request.setInstitutionCode("-30000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeSymbol() throws JsonFormatException {
		request.setInstitutionCode("!@!@!@!@!@!");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeWhiteSpace() throws JsonFormatException {
		request.setInstitutionCode("30000 000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeDecimalPoint() throws JsonFormatException {
		request.setInstitutionCode("30000.000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeLengthMoreThan11() throws JsonFormatException {
		request.setInstitutionCode("300000000033");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateInstitutionCodeLengthLessThan11() throws JsonFormatException {
		request.setInstitutionCode("300000033");
		unit.validate(request);
	}

	@Test
	public void validateInstitutionCodeLengthEqual11() throws JsonFormatException {
		request.setInstitutionCode("30000000033");
		unit.validate(request);
	}
	
	/*
	 * Test for transmission time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeNullValue() throws JsonFormatException {
		request.setTransmissionTime(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeEmptyValue() throws JsonFormatException {
		request.setTransmissionTime("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeNegativeValue() throws JsonFormatException {
		request.setTransmissionTime("-0608123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeAlphaNumericValue() throws JsonFormatException {
		request.setTransmissionTime("abc8123413");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeMonthValue() throws JsonFormatException {
		request.setTransmissionTime("1308123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeDayValue() throws JsonFormatException {
		request.setTransmissionTime("1233123456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeHourValue() throws JsonFormatException {
		request.setTransmissionTime("1230253456");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeMinuteValue() throws JsonFormatException {
		request.setTransmissionTime("1230126656");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeSecondValue() throws JsonFormatException {
		request.setTransmissionTime("1230123466");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeLengthMoreThan10() throws JsonFormatException {
		request.setTransmissionTime("12301234566");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransmissionTimeLengthLessThan10() throws JsonFormatException {
		request.setTransmissionTime("123012345");
		unit.validate(request);
	}
	
	@Test
	public void validateTransmissionTimeLength10() throws JsonFormatException {
		request.setTransmissionTime("1230123456");
		unit.validate(request);
	}
	
	/*
	 * Test for transaction type
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionTypeNullValue() throws JsonFormatException {
		request.setTransactionType(null);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionTypeEmptyValue() throws JsonFormatException {
		request.setTransactionType("");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionTypeLengthMoreThan3() throws JsonFormatException {
		request.setTransactionType("PNCC");
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTransactionTypeLengthLessThan3() throws JsonFormatException {
		request.setTransactionType("PN");
		unit.validate(request);
	}
	
	@Test
	public void validateTransactionTypeLength3() throws JsonFormatException {
		request.setTransactionType("PNC");
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainData() throws JsonFormatException {
		request.setTransactionDomainData(null);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data credit amount
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountAlphaValue() throws JsonFormatException {
		txnData.setCreditAmount("abcdeffffftt");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountAlphaNumericValue() throws JsonFormatException {
		txnData.setCreditAmount("abcdefffff11");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountNegativeValue() throws JsonFormatException {
		txnData.setCreditAmount("-123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountSymbols() throws JsonFormatException {
		txnData.setCreditAmount("!@!@!@!@!@!@");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountWhiteSpace() throws JsonFormatException {
		txnData.setCreditAmount("1234567 89012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountDecimalPoint() throws JsonFormatException {
		txnData.setCreditAmount("1234567.89012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountLengthMoreThan12() throws JsonFormatException {
		txnData.setCreditAmount("12345671289012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditAmountLengthLessThan12() throws JsonFormatException {
		txnData.setCreditAmount("1234589012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataCreditAmountLength12() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data credit currency
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditCurrencyNullValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency(null);
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditCurrencyEmptyValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditCurrencyLengthMoreThan3() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGDD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditCurrencyLengthLessThan3() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SG");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataCreditCurrencyLength3() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("0526000000");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data crediting time
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeNullValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime(null);
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeEmptyValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeNegativeValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("-0608123413");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeAlphaNumericValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("abee123413");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeMonthValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1311123413");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeDayValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1133123413");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeHourValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1111253413");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeMinuteValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1111116613");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeSecondValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1111111166");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeLengthMoreThan10() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("12121234561");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataCreditingTimeLengthLessThan10() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("121212345");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataCreditingTimeLength10() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("12345678909876554433222");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("1234");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data receiving account number
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountNumberNullValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber(null);
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountNumberEmptyValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountNumberLengthMoreThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("123456789012345678901234567890123456789023");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataReceivingAccountNumberLengthLessThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("11223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("12345678");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("123456");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataReceivingAccountNumberLength40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data account type
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountTypeNullValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType(null);
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountTypeEmptyValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountTypeLengthMoreThan3() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataReceivingAccountTypeLengthLessThan3() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("ON");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataReceivingAccountTypeLength3() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		txnData.setSendingAccountBank("123");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data merchant reference number
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataMerchantReferenceNumberNullValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber(null);
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataMerchantReferenceNumberEmptyValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataMerchantReferenceNumberLengthMoreThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("aaaabbbbeeeeeedddfddaaaabbbbeeeeeedddfddwefew");
		txnData.setSendingAccountBank("gjsgiguigdiuwqqw8qtyg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataMerchantReferenceNumberLengthLessThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("1234444");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataMerchantReferenceNumberLengthEqual40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("aassddffgghhjjkkllooaassddffgghhjjkklloo");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data sending account bank
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankAlphaValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("abxedfg");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankAlphaNumericValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("abxedfg12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankNegativeValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("-12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankSymbolValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("!@#$%^&*");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankWhiteSpace() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("123 345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankDecimalPoint() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("123.345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountBankLengthMoreThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("1234567890123456789012345678901234567890123");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataSendingAccountBankLengthLessThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataSendingAccountBankLength40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("1234567890123456789012345678901234567890");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data sending account number
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberAlphaValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("abcedef");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberAlphaNumericValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("abcedef123");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberNegativeValues() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("-12345");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberSymbols() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("!@#$%");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberWhiteSpaces() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("12 345");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberDecimalPoint() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("12.345");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNumberLengthMoreThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("thisislessthan40too");
		txnData.setSendingAccountNumber("12345678901234567890123456789012345678901234567890");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataSendingAccountNumberLengthLessThan40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataSendingAccountNumberLength40() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("1234");
		txnData.setSendingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setSendingAccountName("ABCDEFGHIJKLM");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data sending account name
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataSendingAccountNameLengthMoreThan80() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("thisislessthan40too");
		txnData.setSendingAccountNumber("thisislessthan40tree");
		txnData.setSendingAccountName("accountnamemorethan80accountnamemorethan80accountnamemorethan80accountnamemorethan80");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataSendingAccountNameLengthLessThan80() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataSendingAccountNameLength80() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Test for transaction domain data additional bank reference
	 */
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataAdditionalBankReferenceNullValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("thisislessthan40too");
		txnData.setSendingAccountNumber("thisislessthan40tree");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference(null);
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataAdditionalBankReferenceEmptyValue() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("thisislessthan40too");
		txnData.setSendingAccountNumber("thisislessthan40tree");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference("");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test(expected = JsonFormatException.class)
	public void validateTxnDomainDataAdditionalBankReferenceLengthMoreThan35() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("thisislessthan40too");
		txnData.setSendingAccountNumber("thisislessthan40tree");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference("additionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnumadditionalbankrefnum");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataAdditionalBankReferenceLengthLessThan35() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("123456");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference("thisislessthan35");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	@Test
	public void validateTxnDomainDataAdditionalBankReferenceLength35() throws JsonFormatException {
		txnData.setCreditAmount("123456789012");
		txnData.setCreditCurrency("SGD");
		txnData.setCreditingTime("1212123456");
		txnData.setReceivingAccountNumber("1122334455112233445511223344551122334455");
		txnData.setReceivingAccountType("OWN");
		txnData.setMerchantReferenceNumber("thisislessthan40");
		txnData.setSendingAccountBank("12345");
		txnData.setSendingAccountNumber("12345");
		txnData.setSendingAccountName("thisislessthan80not40");
		txnData.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(txnData);
		unit.validate(request);
	}
	
	/*
	 * Method to create a sample CreditNotificationRequest for testing purposes
	 */
	private CreditNotificationRequest createJsonCreditNotificationRequest() {
		CreditNotificationRequest request=new CreditNotificationRequest();
		request.setRetrievalRef("12345678901234567890");
		request.setMti("8200");
		request.setProcessCode("420000");
		request.setInstitutionCode("30000000033");
		request.setTransmissionTime("0527131219");
		request.setTransactionType("PNC");
		PaymentTransactionDomainData tdd=new PaymentTransactionDomainData();
		tdd.setCreditAmount("000000000012");
		tdd.setCreditCurrency("SGD");
		tdd.setCreditingTime("0526000000");
		tdd.setReceivingAccountNumber("12345678909876554433222");
		tdd.setReceivingAccountType("OWN");
		tdd.setMerchantReferenceNumber("ABCDEFGH1234567890");
		tdd.setSendingAccountBank("12345678");
		tdd.setSendingAccountNumber("123456");
		tdd.setSendingAccountName("ABCDEFGHIJKLM");
		tdd.setAdditionalBankReference("2542456gjgjiouihuioyuoiiobohiphihoh");
		request.setTransactionDomainData(tdd);
		
		return request;
	}

}
