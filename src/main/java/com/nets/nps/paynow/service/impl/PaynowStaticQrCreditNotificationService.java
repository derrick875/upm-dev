package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.BooleanValidationHelpers.isAccepted;
import static com.nets.upos.commons.validations.helper.ListValidationHelpers.notEmptyList;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.nets.nps.client.impl.FraudwallClient;
import com.nets.nps.client.impl.NonStopClient;
import com.nets.nps.client.impl.NotificationClient;
import com.nets.nps.client.impl.NotificationRequest;
import com.nets.nps.client.impl.TransactionStateMsg;
import com.nets.nps.core.service.PaymentService;
import com.nets.nps.paynow.client.bank.BankAdapterProcessingFactory;
import com.nets.nps.paynow.entity.CreditNotificationInfo;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.CreditNotificationResponse;
import com.nets.nps.paynow.entity.TransactionResponseData;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.utils.PaynowUtil;
import com.nets.upos.commons.constants.AdditionalData;
import com.nets.upos.commons.constants.IDRegistryKeyTypes;
import com.nets.upos.commons.constants.MtiRequestMap;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.entity.APSRequest;
import com.nets.upos.commons.entity.APSRequestWrapper;
import com.nets.upos.commons.entity.APSResponse;
import com.nets.upos.commons.entity.JsonCommunicationData;
import com.nets.upos.commons.entity.JsonNotification;
import com.nets.upos.commons.enums.BankConfigAttributeName;
import com.nets.upos.commons.enums.ContextData;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import com.nets.upos.core.entity.AcqMerAccountEntity;
import com.nets.upos.core.entity.AcquirerEntity;
import com.nets.upos.core.entity.BankCfgAttrEntity;
import com.nets.upos.core.entity.BankEntity;
import com.nets.upos.core.entity.IDRegistryRequest;
import com.nets.upos.core.entity.MerchantCfgAttrEntity;
import com.nets.upos.core.entity.MerchantEntity;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.PhysicalPosEntity;
import com.nets.upos.core.entity.PosNotificationEntity;
import com.nets.upos.core.entity.SOFTypeEntity;
import com.nets.upos.core.entity.StaticRequest;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.repository.projection.PosInfoOnly;
import com.nets.upos.core.service.AcqMerAccountService;
import com.nets.upos.core.service.AcquirerService;
import com.nets.upos.core.service.BankService;
import com.nets.upos.core.service.CodesService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.MDRService;
import com.nets.upos.core.service.MerchantService;
import com.nets.upos.core.service.PhysicalPosService;
import com.nets.upos.core.service.PosNotificationService;
import com.nets.upos.core.service.SOFTypeService;
import com.nets.upos.core.service.StaticRequestService;
import com.nets.upos.core.service.TransactionService;

@Service
public class PaynowStaticQrCreditNotificationService
		implements PaymentService<CreditNotificationRequest, CreditNotificationResponse> {

	private static final ApsLogger logger = new ApsLogger(PaynowStaticQrCreditNotificationService.class);

	@Autowired
	private IDRegistryService idRegistryService;

	@Autowired
	private AcquirerService acquirerService;

	@Autowired
	private AcqMerAccountService acqMerAccountService;

	@Autowired
	private PhysicalPosService posService;

	@Autowired
	private BankService bankService;

	@Autowired
	private StaticRequestService staticRequestService;

	@Autowired
	private MDRService mdrService;

	@Autowired
	private CodesService codesService;

	@Autowired
	private SOFTypeService sofTypeService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private PaynowTransactionDataService paynowTransactionDataService;

	@Autowired
	private NonStopClient nonStopClient;

	@Autowired
	private NotificationClient notificationClient;

	@Autowired
	private PosNotificationService posNotificationService;

	@Autowired
	private BankAdapterProcessingFactory bankAdapterProcessingFactory;
	
	@Autowired
	private MerchantService merchantService;

	@Autowired
	ExceptionHandler exceptionHandler;
	
	@Autowired
	private FraudwallClient fraudwallClient;

	@Value("${paynow.staticqr.validity.period}")
	private int validityPeriod;

	@Override
	public CreditNotificationResponse process(CreditNotificationRequest payment)
			throws BaseBusinessException, ParseException {

		checkDuplicateRetrivalRef(payment);
		CreditNotificationInfo validatedInfo = validatePaynowStaticCreditNotification(payment);
		validatedInfo = createTransaction(payment, validatedInfo);
		validatedInfo = createStaticRequest(payment, validatedInfo);
		createPaynowTransactionData(payment, validatedInfo);
		validateAndReverseTransaction(payment, validatedInfo);
		createAndSendTransactionStateMessage(payment, validatedInfo);
		createAndSendNotification(payment, validatedInfo);
		createAndSendFraudwallMsg(payment, validatedInfo);
	
		return generateResponse(payment, validatedInfo);
	}

	@Transactional
	private CreditNotificationInfo validatePaynowStaticCreditNotification(CreditNotificationRequest payment)
			throws BaseBusinessException {
		CreditNotificationInfo creditNotificationInfo = new CreditNotificationInfo();

		Map<ContextData, Object> entityMap = acquirerService.getAcquirerSofDetails(payment.getSofUri());
		notNull.test(entityMap.get(ContextData.ACQUIRER))
				.throwIfInvalid(BusinessValidationErrorCodes.ACQUIRER_NOT_FOUND);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		creditNotificationInfo.setAcquirerActiveStatus(acquirer.isActive());
		creditNotificationInfo.setAcquirerId(acquirer.getAcquirerId());
		SOFTypeEntity sofType = (SOFTypeEntity) entityMap.get(ContextData.SOF_TYPE);
		creditNotificationInfo.setCardTypeId(sofType.getCardTypeId());

		String mpan = payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16);
		AcqMerAccountEntity ama = (AcqMerAccountEntity) acqMerAccountService
				.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(acquirer.getAcquirerId(), mpan,
						payment.getTransactionDomainData().getCreditCurrency());

		notNull.test(ama).throwIfInvalid(BusinessValidationErrorCodes.MERCHANT_REFERENCE_NUMBER_CANNOT_BE_MATCHED);
		creditNotificationInfo.setAmaActiveStatus(ama.getAcquirerMerchantMapping().isActive());
		creditNotificationInfo.setEcMid(mpan);
		creditNotificationInfo.setPosId(ama.getPosId());
		creditNotificationInfo.setAcqMerAccountId(ama.getAcqMerAccountId());
		creditNotificationInfo.setAcqMerMappingId(ama.getAcquirerMerchantMapping().getMerMappingId());
		creditNotificationInfo.setBankTid(ama.getTid());
		logger.info("POS ID:" + ama.getPosId());
		PosInfoOnly pos = posService.getPosInfoOnlyByPosId(ama.getPosId());
		notNull.test(pos).throwIfInvalid(BusinessValidationErrorCodes.MERCHANT_NOT_FOUND);
		logger.info("POS ID:" + pos.getPosId());
		logger.info("NETS TID:" + pos.getName());
		logger.info("NETS MID:" + pos.getRid());

		creditNotificationInfo.setTid(pos.getName());
		creditNotificationInfo.setMid(pos.getRid());
		BankEntity bank = bankService.findByAcquirerAndValidate(acquirer.getAcquirerId());
		if (Objects.isNull(bank)) {
			throw new BaseBusinessException("15", "No route found to bank.");
		}
		logger.info("BankID:" + bank.getBankId());
		Set<BankCfgAttrEntity> bankConfigs = bank.getBankCfgAttr();
		logger.info("Bank config size:" + bankConfigs.size());

		creditNotificationInfo.setPaymentType(getPaymentType(bankConfigs));
		logger.info("Payment Type:" + creditNotificationInfo.getPaymentType());
		Optional.ofNullable(payment.getTransactionDomainData().getCreditAmount()).ifPresent(s -> creditNotificationInfo
				.setAmount(new BigDecimal(s).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN))));

		isAccepted(true).test(creditNotificationInfo.getAmount().compareTo(BigDecimal.ZERO) > 0)
				.throwIfInvalid(BusinessValidationErrorCodes.INVALID_AMOUNT);
		creditNotificationInfo.setBankSettleDate(
				bankAdapterProcessingFactory.getBankAdapter(payment.getInstitutionCode()).getSettlementDate());
		creditNotificationInfo.setApprovalCode(RandomStringUtils.randomNumeric(6).toUpperCase());
		
		PhysicalPosEntity posEntity = posService.findByNetsTid(creditNotificationInfo.getTid());
		int merchantId = posEntity.getMerchant().getMerchantId();
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = merchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(merchantId, "PAYNOW_SHORT_NAME");
		notEmptyList.test(merchantCfgAttrEntityList).throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_SHORTNAME);
		String merchantShortNameOrig = merchantCfgAttrEntityList.get(0).getAttributeValue();
		notEmpty.test(merchantShortNameOrig).throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_SHORTNAME);
		String merchantShortName = String.format(merchantShortNameOrig.substring(0, Math.min(merchantShortNameOrig.length(), 5)));
		String merchantShortNameFromRequest = payment.getTransactionDomainData().getMerchantReferenceNumber().substring(17,22);
		
		isAccepted(true).test(merchantShortNameFromRequest.startsWith(merchantShortName)).throwIfInvalid(BusinessValidationErrorCodes.MERCHANT_REFERENCE_NUMBER_CANNOT_BE_MATCHED);
		
		return creditNotificationInfo;
	}

	private void checkDuplicateRetrivalRef(CreditNotificationRequest payment) throws BaseBusinessException {
		try {
			createIdRegistry(payment.getInstitutionCode(),
					payment.getTransactionDomainData().getAdditionalBankReference());
		} catch (RuntimeException ex) {
			throw new BaseBusinessException(BusinessValidationErrorCodes.CREDIT_NOTIFICATION_PREVIOUSLY_RECEIVED);
		}
	}

	private void createIdRegistry(String institutionCode, String additionalBankRef) {
		IDRegistryRequest idRegReq = new IDRegistryRequest();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		idRegReq.setKeyType(IDRegistryKeyTypes.PAYNOW_QR_PAYMENT);

		StringBuffer keyDataBuffer = new StringBuffer().append(institutionCode).append("_").append(additionalBankRef);
		idRegReq.setKeyData(keyDataBuffer.toString());

		// idRegReq.setTransactionId(transaction.getTransactionId());
		idRegReq.setCreationDate(currentTimeStamp);
		idRegistryService.saveIDRegistry(idRegReq);
	}

	private CreditNotificationInfo createTransaction(CreditNotificationRequest payment,
			CreditNotificationInfo requestInfo) throws BaseBusinessException {

		TransactionEntity transaction = new TransactionEntity();
		PhysicalPosEntity pos = posService.findByNetsTid(requestInfo.getTid());
		SOFTypeEntity sofType = sofTypeService.getCardTypeById(requestInfo.getCardTypeId());
		MerchantEntity merchant = pos.getMerchant();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setAcqApprovalCode(requestInfo.getApprovalCode());
		transaction.setAcqMerAcctId(requestInfo.getAcqMerAccountId());
		transaction.setAcqMerMappingId(requestInfo.getAcqMerMappingId());
		transaction.setAcquirerId(requestInfo.getAcquirerId());
		transaction.setAuthorizeDate(currentTimeStamp);
		transaction.setCaptureType(codesService.findByCodeValue("QR_CODE").getIntegRefValue());
		transaction.setCreatedBy(2);// Need to change
		transaction.setCreationDate(currentTimeStamp);
		transaction.setCardTypeId(requestInfo.getCardTypeId());
		transaction.setIsDccTransaction('N');
		transaction.setIsRefund('N');
		transaction.setAcquirerAuthResponseId(payment.getRetrievalRef());
		transaction.setEcMid(requestInfo.getEcMid());

		transaction.setMdrPercent(mdrService.getMdrPercent(merchant, sofType));
		transaction.setMerchantId(merchant.getMerchantId());
		transaction.setOrigTargetAmount(requestInfo.getAmount());
		transaction.setPosId(pos.getPosId());
		transaction.setSourceAmount(requestInfo.getAmount());
		transaction.setSourceCurrency("SGD");
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		transaction.setTargetAmount(requestInfo.getAmount());
		transaction.setTargetCurrency("SGD");
		transaction.setTid(requestInfo.getBankTid());
		transaction.setTranType(TransactionTypes.TYPE_PAYNOW_STATIC_CREDIT_NOTIFICATION);
		transaction.setTransactionSource("NA");
		// transaction.setUserData();
		transaction.setWorkflowBizKey(UUID.randomUUID().toString());
		transaction.setBankSettleDate(requestInfo.getBankSettleDate());
		// transaction.setEcrInvoiceNo();

		transaction = transactionService.save(transaction);
		logger.info("Paynow Credit notification Transaction saved", transaction);
		requestInfo.setTransactionId(transaction.getTransactionId());
		requestInfo.setPosActiveStatus(pos.isActive());
		requestInfo.setMerchantActiveStatus(merchant.isActive());
		return requestInfo;
	}

	private CreditNotificationInfo createStaticRequest(CreditNotificationRequest payment,
			CreditNotificationInfo requestInfo) {

		StaticRequest staticRequest = new StaticRequest();
		staticRequest.setPosId(requestInfo.getPosId());
		staticRequest.setTransactionId(requestInfo.getTransactionId());
		staticRequest.setTid(requestInfo.getTid());
		staticRequest.setMid(requestInfo.getMid());
		staticRequest.setTransactionDate(payment.getTransactionDomainData().getCreditingTime().substring(0, 4));
		staticRequest.setTransactionTime(payment.getTransactionDomainData().getCreditingTime().substring(4));
		staticRequest.setPaymentRequesterInstId(payment.getInstitutionCode());
		// staticRequest.setInvoiceRef();
		staticRequest.setRetrievalRefNo(payment.getRetrievalRef());
		staticRequest.setAcquirerId(requestInfo.getAcquirerId());
		staticRequest.setExternalSettlementDate(PaynowUtil.getSettlementDate(requestInfo.getBankSettleDate()));
		staticRequest.setPaymentType(requestInfo.getPaymentType());
		staticRequest = staticRequestService.save(staticRequest);
		logger.info("Paynow Credit notification Static request Created with Stan ", staticRequest);
		requestInfo.setStan(staticRequest.getStan());
		return requestInfo;
	}

	private String getPaymentType(Set<BankCfgAttrEntity> bankConfigs) {

		for (BankCfgAttrEntity bankConfig : bankConfigs) {

			if (BankConfigAttributeName.PAYMENT_TYPE.name().equals(bankConfig.getCfgAttrName())) {
				return bankConfig.getCfgAttrValue();
			}
		}

		return "";
	}

	private void createAndSendTransactionStateMessage(CreditNotificationRequest payment,
			CreditNotificationInfo requestInfo) {

		TransactionStateMsg txnStateMsg = new TransactionStateMsg();
		txnStateMsg.setSofUri(payment.getSofUri());
		txnStateMsg.setAmount(payment.getTransactionDomainData().getCreditAmount());
		txnStateMsg.setTransmissionTime(payment.getTransmissionTime());
		txnStateMsg.setStan(requestInfo.getStan());
		txnStateMsg.setTransactionTime(payment.getTransactionDomainData().getCreditingTime().substring(4));
		txnStateMsg.setTransactionDate(payment.getTransactionDomainData().getCreditingTime().substring(0, 4));
		txnStateMsg.setSettlementDate(PaynowUtil.getTandemSettlementDate(requestInfo.getBankSettleDate()));
		txnStateMsg.setInstitutionCode(payment.getInstitutionCode());
		txnStateMsg.setConditionCode("00");
		txnStateMsg.setResponseCode(ResponseCodes.SUCCESS);
		txnStateMsg.setHostTid(requestInfo.getTid());
		txnStateMsg.setHostMid(requestInfo.getMid());
		txnStateMsg.setTransactionType("PNSTATICCN");
		txnStateMsg.setStatus(TransactionState.STATE_SETTLED.getState());
		txnStateMsg.setPaymentType(requestInfo.getPaymentType());
		txnStateMsg.setApprovalCode(requestInfo.getApprovalCode());
		txnStateMsg.setInstitutionCode(payment.getInstitutionCode());
		txnStateMsg.setRetrievalRef(payment.getRetrievalRef());
		txnStateMsg.setTransactionId(String.valueOf(requestInfo.getTransactionId()));
		nonStopClient.put(txnStateMsg);
	}

	private void createPaynowTransactionData(CreditNotificationRequest payment, CreditNotificationInfo requestInfo) {

		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setRetrievalRef(payment.getRetrievalRef());
		paynowTransactionData.setInstitutionCode(payment.getInstitutionCode());
		paynowTransactionData.setTid(requestInfo.getTid());
		paynowTransactionData.setTransactionId(requestInfo.getTransactionId());
		paynowTransactionData.setCreditAmount(requestInfo.getAmount());
		paynowTransactionData.setCreditCurrency(payment.getTransactionDomainData().getCreditCurrency());
		paynowTransactionData.setCreditingTime(payment.getTransactionDomainData().getCreditingTime());
		paynowTransactionData.setReceivingAccountNumber(payment.getTransactionDomainData().getReceivingAccountNumber());
		paynowTransactionData.setReceivingAccountType(payment.getTransactionDomainData().getReceivingAccountType());
		paynowTransactionData
				.setMerchantReferenceNumber(payment.getTransactionDomainData().getMerchantReferenceNumber());
		paynowTransactionData.setSendingAccountBank(payment.getTransactionDomainData().getSendingAccountBank());
		paynowTransactionData.setSendingAccountName(payment.getTransactionDomainData().getSendingAccountName());
		paynowTransactionData.setSendingAccountNumber(payment.getTransactionDomainData().getSendingAccountNumber());
		paynowTransactionData
				.setAdditionalBankReference(payment.getTransactionDomainData().getAdditionalBankReference());
		paynowTransactionDataService.save(paynowTransactionData);

	}

	private void createAndSendNotification(CreditNotificationRequest payment, CreditNotificationInfo requestInfo) {

		List<PosNotificationEntity> posNotification = posNotificationService.findByPosId(requestInfo.getPosId());

		if (CollectionUtils.isEmpty(posNotification)) {
			logger.info("posNotification data does not exist for tid " + requestInfo.getTid());
			return;
		}
		Gson gson = new Gson();
		NotificationRequest notification = new NotificationRequest();
		notification.setRetrievalRef(payment.getRetrievalRef());
		notification.setMti("0340");
		notification.setProcessCode("000000");
		notification.setStan(requestInfo.getStan());
		notification.setTransactionTime(payment.getTransactionDomainData().getCreditingTime().substring(4));
		notification.setTransactionDate(payment.getTransactionDomainData().getCreditingTime().substring(0, 4));
		notification.setTid(requestInfo.getTid());
		notification.setMid(requestInfo.getMid());
		notification.setResponseCode(ResponseCodes.SUCCESS);
		notification.setTargetAmount(payment.getTransactionDomainData().getCreditAmount());
		notification.setSofUri(payment.getSofUri());
		notification.setPaymentTypeId(requestInfo.getPaymentType());
		notification.setApprovalCode(requestInfo.getApprovalCode());
		notification.setTransactionId(String.valueOf(requestInfo.getTransactionId()));
		notification.setCurrencyCode(payment.getTransactionDomainData().getCreditCurrency());
		notification.setTransactionStatus(TransactionState.STATE_SETTLED.getState());

		ArrayList<JsonCommunicationData> communicationDataList = new ArrayList<JsonCommunicationData>();
		posNotification.stream().forEach(e -> {
			JsonCommunicationData communicationData = new JsonCommunicationData();
			communicationData.setCommunicationCategory(e.getCommunicationCategory());
			communicationData.setCommunicationDestination(e.getCommunicationDestination());
			communicationData.setCommunicationType(e.getCommunicationType());
			Optional.ofNullable(e.getAddonParameterList())
					.ifPresent(s -> communicationData.setAddon(gson.fromJson(s, HashMap.class)));

			communicationDataList.add(communicationData);
		});
		JsonNotification notificationResponse = new JsonNotification();
		notificationResponse.setCommunicationData(communicationDataList);
		notificationResponse.setMessage(notification);
		logger.info("Notification:" + gson.toJson(notificationResponse));
		notificationClient.put(notificationResponse);
	}

	private void validateAndReverseTransaction(CreditNotificationRequest request, CreditNotificationInfo requestInfo)
			throws BaseBusinessException, ParseException {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddHHmmss");

		Date creditingTimeDate = DATE_FORMAT.parse(request.getTransactionDomainData().getCreditingTime());
		Date currentTime = new Date(System.currentTimeMillis());

		long now = DATE_FORMAT.parse(DATE_FORMAT.format(currentTime)).getTime();
		long secondsDiff = (now - creditingTimeDate.getTime()) / 1000;
		Boolean isInvalid = checkTimeOut(secondsDiff);

		if (requestInfo.isAcquirerActiveStatus() == false || requestInfo.isAmaActiveStatus() == false
				|| requestInfo.isPosActiveStatus() == false || requestInfo.isMerchantActiveStatus() == false
				|| isInvalid == true) {

			reverseTransaction(request, requestInfo);
			TransactionEntity reversedTransaction = transactionService.retrieve(requestInfo.getTransactionId());
			reversedTransaction.setState(TransactionState.STATE_REVERSED.getState());
			transactionService.save(reversedTransaction);

			isAccepted(true).test(requestInfo.isAcquirerActiveStatus())
					.throwIfInvalid(BusinessValidationErrorCodes.ACQUIRER_DISABLED);
			isAccepted(true).test(requestInfo.isAmaActiveStatus())
					.throwIfInvalid(BusinessValidationErrorCodes.ACQUIRER_DISABLED_FOR_MERCHANT);
			isAccepted(true).test(requestInfo.isPosActiveStatus())
					.throwIfInvalid(BusinessValidationErrorCodes.POS_IS_NOT_ACTIVE);
			isAccepted(true).test(requestInfo.isMerchantActiveStatus())
					.throwIfInvalid(BusinessValidationErrorCodes.MERCHANT_IS_NOT_ACTIVE);
			isAccepted(false).test(isInvalid)
					.throwIfInvalid(BusinessValidationErrorCodes.CREDIT_NOTIFICATION_TIMEOUT);
		}

	}

	private void createAndSendFraudwallMsg(CreditNotificationRequest payment, CreditNotificationInfo validatedInfo) {
		APSRequest apsRequest = new APSRequest();
		apsRequest.setTid(validatedInfo.getTid());
		apsRequest.setTransactionId(validatedInfo.getTransactionId());
		apsRequest.setStan(validatedInfo.getStan());
		apsRequest.setMid(validatedInfo.getMid());
		apsRequest.setTargetAmount(validatedInfo.getAmount());
		apsRequest.setRequestMap(new MtiRequestMap("8200", "420000"));
		apsRequest.setApprovalCode(validatedInfo.getApprovalCode());
		apsRequest.addAdditionalData(AdditionalData.CONDITION_CODE, "00");
		apsRequest.addAdditionalData(AdditionalData.PAYMENT_TYPE, validatedInfo.getPaymentType());
		apsRequest.setRetrievalReferenceNo(payment.getRetrievalRef());
		apsRequest.setAcquiringInstitutionIdentificationCode(payment.getInstitutionCode());
		
		APSResponse apsResponse = new APSResponse();
		apsResponse.setStatusCode("00");
		apsResponse.setTransactionId(validatedInfo.getTransactionId());
		
		APSRequestWrapper wrapper = new APSRequestWrapper();
		wrapper.setRequest(apsRequest);
		wrapper.setResponse(apsResponse);
		
		fraudwallClient.put(wrapper);
	}
	
	private Boolean checkTimeOut(long secondsDiff) {
		if (secondsDiff > validityPeriod) {
			return true;
		} else
			return false;
	}

	private void reverseTransaction(CreditNotificationRequest request, CreditNotificationInfo requestInfo) {
		CompletableFuture.runAsync(() -> {
			logger.info("Refund/Reversal Start for RRN:" + request.getRetrievalRef() + "  Institutioncode:"
					+ request.getInstitutionCode());
			try {

				bankAdapterProcessingFactory.getBankAdapter(request.getInstitutionCode())
						.processRefundReversalDuetoException(request);
			} catch (Exception e) {
				logger.info("Refund/Reversal Failed for RRN:" + request.getRetrievalRef() + "  Institutioncode:"
						+ request.getInstitutionCode());
				logger.error(e.getMessage());
				if (e.getMessage() != null) {
					exceptionHandler.handleBusinessException(request, new BaseBusinessException("06",
							e.getMessage().substring(0, Math.min(e.getMessage().length(), 100))));
				} else {
					exceptionHandler.handleBusinessException(request,
							new BaseBusinessException("06", "Static QR Exception Reversal Failed"));

				}

			}
			logger.info("Refund/Reversal End for RRN:" + request.getRetrievalRef() + "  Institutioncode:"
					+ request.getInstitutionCode());
		});
	}

	private CreditNotificationResponse generateResponse(CreditNotificationRequest request,
			CreditNotificationInfo requestInfo) {
		TransactionResponseData txnData = new TransactionResponseData();
		CreditNotificationResponse res = request.createResponse();
		res.setResponseCode(ResponseCodes.SUCCESS);
		txnData.setStan(requestInfo.getStan());
		txnData.setApprovalCode(requestInfo.getApprovalCode());
		res.setTransactionResponseData(txnData);
		return res;
	}

}
