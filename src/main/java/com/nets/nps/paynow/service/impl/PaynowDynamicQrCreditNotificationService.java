package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.BooleanValidationHelpers.isAccepted;
import static com.nets.upos.commons.validations.helper.ListValidationHelpers.notEmptyList;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
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
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import com.nets.upos.core.entity.AcqMerAccountEntity;
import com.nets.upos.core.entity.AcquirerEntity;
import com.nets.upos.core.entity.BankCfgAttrEntity;
import com.nets.upos.core.entity.BankEntity;
import com.nets.upos.core.entity.IDRegistryRequest;
import com.nets.upos.core.entity.MerchantEntity;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.PhysicalPosEntity;
import com.nets.upos.core.entity.PosNotificationEntity;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.entity.SOFTypeEntity;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.repository.projection.PosInfoOnly;
import com.nets.upos.core.service.AcqMerAccountService;
import com.nets.upos.core.service.AcquirerService;
import com.nets.upos.core.service.BankService;
import com.nets.upos.core.service.CodesService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.MDRService;
import com.nets.upos.core.service.PhysicalPosService;
import com.nets.upos.core.service.PosNotificationService;
import com.nets.upos.core.service.RequestService;
import com.nets.upos.core.service.SOFTypeService;
import com.nets.upos.core.service.TransactionService;

@Service
public class PaynowDynamicQrCreditNotificationService
		implements PaymentService<CreditNotificationRequest, CreditNotificationResponse> {

	private static final ApsLogger logger = new ApsLogger(PaynowDynamicQrCreditNotificationService.class);
	@Autowired
	private IDRegistryService idRegistryService;

	@Autowired
	private AcquirerService acquirerService;

	@Autowired
	private PhysicalPosService posService;

	@Autowired
	private BankService bankService;

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
	private AcqMerAccountService acqMerAccountService;

	@Autowired
	private RequestService requestService;

	@Autowired
	private BankAdapterProcessingFactory bankAdapterProcessingFactory;

	@Autowired
	ExceptionHandler exceptionHandler;
	
	@Autowired
	private FraudwallClient fraudwalClient;

	@Override
	public CreditNotificationResponse process(CreditNotificationRequest payment) throws BaseBusinessException {

		checkDuplicateCreditNotification(payment);
		CreditNotificationInfo validatedInfo = validatePaynowDynamicCreditNotification(payment);
		validatedInfo = createTransaction(payment, validatedInfo);
		createPaynowTransactionData(payment, validatedInfo);
		validateAndReverseTransaction(payment, validatedInfo);
		updateOrderRequest(validatedInfo);
		createAndSendTransactionStateMessage(payment, validatedInfo);
		createAndSendCreditNotification(payment, validatedInfo);
		createAndSendFraudwallMsg(payment, validatedInfo);
		
		return generateResponse(payment, validatedInfo);

	}

	private void checkDuplicateCreditNotification(CreditNotificationRequest payment) throws BaseBusinessException {
		try {
			createIdRegistry(payment.getInstitutionCode(),
					payment.getTransactionDomainData().getAdditionalBankReference());
		} catch (RuntimeException e) {
			throw new BaseBusinessException(BusinessValidationErrorCodes.CREDIT_NOTIFICATION_PREVIOUSLY_RECEIVED);
		}
	}

	private void createIdRegistry(String institutionCode, String additionalBankRef) {
		IDRegistryRequest idRegReq = new IDRegistryRequest();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		idRegReq.setKeyType(IDRegistryKeyTypes.PAYNOW_QR_PAYMENT);

		StringBuffer keyDataBuffer = new StringBuffer().append(institutionCode).append("_").append(additionalBankRef);
		idRegReq.setKeyData(keyDataBuffer.toString());

		idRegReq.setCreationDate(currentTimeStamp);
		idRegistryService.saveIDRegistry(idRegReq);
	}

	private CreditNotificationInfo validatePaynowDynamicCreditNotification(CreditNotificationRequest payment)
			throws BaseBusinessException {

		Request originalOrderRequest = Optional
				.ofNullable(
						requestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0, 16)))
				.orElseThrow(() -> new BaseBusinessException(BusinessValidationErrorCodes.ORIGINAL_ORDER_CANNOT_FIND));

		CreditNotificationInfo creditNotificationInfo = new CreditNotificationInfo();
		creditNotificationInfo.setMerchantRefNumber(payment.getTransactionDomainData().getMerchantReferenceNumber());
		creditNotificationInfo.setOriginalOrderState(originalOrderRequest.getState());
		creditNotificationInfo.setTransactionDate(originalOrderRequest.getTransactionDate());
		creditNotificationInfo.setTransactionTime(originalOrderRequest.getTransactionTime());
		creditNotificationInfo.setTimedOutStatus(originalOrderRequest.isTimedOut());
		logger.info("order creation time " + originalOrderRequest.getCreationDate());

		Optional.ofNullable(payment.getTransactionDomainData().getCreditAmount()).ifPresent(s -> creditNotificationInfo
				.setAmount(new BigDecimal(s).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN))));

		isAccepted(true).test(creditNotificationInfo.getAmount().compareTo(originalOrderRequest.getTargetAmount())== 0)
				.throwIfInvalid(BusinessValidationErrorCodes.INVALID_AMOUNT);

		creditNotificationInfo.setPosId(originalOrderRequest.getPosId());
		logger.info("POS ID:" + originalOrderRequest.getPosId());
		creditNotificationInfo.setStan(originalOrderRequest.getStan());
		logger.info("STAN:" + originalOrderRequest.getStan());
		PosInfoOnly posInfo = Optional.ofNullable(posService.getPosInfoOnlyByPosId(originalOrderRequest.getPosId()))
				.orElseThrow(() -> new BaseBusinessException(BusinessValidationErrorCodes.MERCHANT_NOT_FOUND));
		creditNotificationInfo.setTid(posInfo.getName());
		logger.info("NETS TID:" + posInfo.getName());
		creditNotificationInfo.setMid(posInfo.getRid());
		logger.info("NETS MID:" + posInfo.getRid());

		Map<ContextData, Object> entityMap = Optional
				.ofNullable(acquirerService.getAcquirerSofDetails(payment.getSofUri()))
				.orElseThrow(() -> new BaseBusinessException(BusinessValidationErrorCodes.ACQUIRER_NOT_FOUND));
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		creditNotificationInfo.setAcquirerId(acquirer.getAcquirerId());
		logger.info("ACQUIRER ID" + acquirer.getAcquirerId());
		creditNotificationInfo.setAcquirerActiveStatus(acquirer.isActive());
		SOFTypeEntity sofType = (SOFTypeEntity) entityMap.get(ContextData.SOF_TYPE);
		creditNotificationInfo.setCardTypeId(sofType.getCardTypeId());
		logger.info("CARD TYPE ID:" + sofType.getCardTypeId());

		List<AcqMerAccountEntity> amaList = acqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(
				creditNotificationInfo.getPosId(), acquirer.getAcquirerId(),
				payment.getTransactionDomainData().getCreditCurrency().toUpperCase());
		notEmptyList.test(amaList).throwIfInvalid(BusinessValidationErrorCodes.ACQUIRER_MAPPING_NOT_FOUND);
		creditNotificationInfo.setAmaActiveStatus(amaList.get(0).getAcquirerMerchantMapping().isActive());
		creditNotificationInfo.setAcqMerAccountId(amaList.get(0).getAcqMerAccountId());
		creditNotificationInfo.setBankTid(amaList.get(0).getTid());
		creditNotificationInfo.setAcqMerMappingId(amaList.get(0).getAcquirerMerchantMapping().getMerMappingId());
		creditNotificationInfo.setEcMid(amaList.get(0).getEcMid());

		BankEntity bank = bankService.findByAcquirerAndValidate(acquirer.getAcquirerId());
		logger.info("BankID:" + bank.getBankId());

		Set<BankCfgAttrEntity> bankConfigs = bank.getBankCfgAttr();
		logger.info("Bank config size:" + bankConfigs.size());
		creditNotificationInfo.setPaymentType(getPaymentType(bankConfigs));
		logger.info("Payment Type" + creditNotificationInfo.getPaymentType());

		creditNotificationInfo.setBankSettleDate(
				bankAdapterProcessingFactory.getBankAdapter(payment.getInstitutionCode()).getSettlementDate());
		creditNotificationInfo.setApprovalCode(RandomStringUtils.randomNumeric(6).toUpperCase());

		return creditNotificationInfo;
	}

	@Transactional
	private CreditNotificationInfo createTransaction(CreditNotificationRequest payment,
			CreditNotificationInfo requestInfo) throws BaseBusinessException {

		TransactionEntity transaction = new TransactionEntity();
		PhysicalPosEntity pos = posService.findByNetsTid(requestInfo.getTid());
		SOFTypeEntity sofType = sofTypeService.getCardTypeById(requestInfo.getCardTypeId());
		MerchantEntity merchant = pos.getMerchant();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setAcqApprovalCode(requestInfo.getApprovalCode());
		transaction.setAcquirerId(requestInfo.getAcquirerId());

		transaction.setAcqMerAcctId(requestInfo.getAcqMerAccountId());
		transaction.setAcqMerMappingId(requestInfo.getAcqMerMappingId());

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
		transaction.setTranType(TransactionTypes.TYPE_PAYNOW_DYNAMIC_CREDIT_NOTIFICATION);
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

	private String getPaymentType(Set<BankCfgAttrEntity> bankConfigs) {

		for (BankCfgAttrEntity bankConfig : bankConfigs) {

			if (BankConfigAttributeName.PAYMENT_TYPE.name().equals(bankConfig.getCfgAttrName())) {
				return bankConfig.getCfgAttrValue();
			}
		}

		return "";
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

	private void validateAndReverseTransaction(CreditNotificationRequest request, CreditNotificationInfo requestInfo)
			throws BaseBusinessException {

		if (!requestInfo.isAcquirerActiveStatus() || !requestInfo.isAmaActiveStatus()
				|| !requestInfo.isPosActiveStatus() || !requestInfo.isMerchantActiveStatus()
				|| requestInfo.isTimedOutStatus()
				|| requestInfo.getOriginalOrderState().equals(RequestState.REVERSED.getState())
				|| requestInfo.getOriginalOrderState().equals(RequestState.COMPLETED.getState())) {

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
			isAccepted(false).test(requestInfo.isTimedOutStatus())
					.throwIfInvalid(BusinessValidationErrorCodes.ORIGINAL_ORDER_TIMEOUT);

			if (requestInfo.getOriginalOrderState().equals(RequestState.REVERSED.getState())) {
				throw new BaseBusinessException(BusinessValidationErrorCodes.ORIGINAL_ORDER_TIMEOUT);
			} else if (requestInfo.getOriginalOrderState().equals(RequestState.COMPLETED.getState())) {
				throw new BaseBusinessException(BusinessValidationErrorCodes.CREDIT_NOTIFICATION_PREVIOUSLY_RECEIVED);
			}
		}
	}

	@Transactional
	private void updateOrderRequest(CreditNotificationInfo requestInfo) throws BaseBusinessException {
		Request originalOrderRequest = requestService.findBySearchId(requestInfo.getMerchantRefNumber().substring(0, 16));
		originalOrderRequest.setTransactionId(requestInfo.getTransactionId());
		originalOrderRequest.setState(RequestState.COMPLETED.getState());
		originalOrderRequest.setPaymentType(requestInfo.getPaymentType());
		originalOrderRequest
				.setExternalSettlementDate(new SimpleDateFormat("yyyyMMdd").format(requestInfo.getBankSettleDate()));

		try {
			requestService.save(originalOrderRequest);
		} catch (OptimisticLockingFailureException e) {
			logger.info("Inside ObjectOptimisticLockingFailureException");
			originalOrderRequest = requestService.findBySearchId(requestInfo.getMerchantRefNumber().substring(0,16));
			if (originalOrderRequest.isReversed()) {
				throw new BaseBusinessException(BusinessValidationErrorCodes.ORIGINAL_ORDER_TIMEOUT);
			}
		}
	}

	private void createAndSendTransactionStateMessage(CreditNotificationRequest payment,
			CreditNotificationInfo requestInfo) {

		TransactionStateMsg txnStateMsg = new TransactionStateMsg();
		txnStateMsg.setSofUri(payment.getSofUri());
		txnStateMsg.setAmount(payment.getTransactionDomainData().getCreditAmount());
		txnStateMsg.setTransmissionTime(payment.getTransmissionTime());
		txnStateMsg.setStan(requestInfo.getStan());
		txnStateMsg.setTransactionTime(requestInfo.getTransactionTime());
		txnStateMsg.setTransactionDate(requestInfo.getTransactionDate());
		txnStateMsg.setSettlementDate(PaynowUtil.getTandemSettlementDate(requestInfo.getBankSettleDate()));
		txnStateMsg.setInstitutionCode(payment.getInstitutionCode());
		txnStateMsg.setConditionCode("00");
		txnStateMsg.setResponseCode(ResponseCodes.SUCCESS);
		txnStateMsg.setHostTid(requestInfo.getTid());
		txnStateMsg.setHostMid(requestInfo.getMid());
		txnStateMsg.setTransactionType("PNDYNAMICCN");
		txnStateMsg.setPaymentType(requestInfo.getPaymentType());
		txnStateMsg.setApprovalCode(requestInfo.getApprovalCode());
		txnStateMsg.setStatus(TransactionState.STATE_SETTLED.getState());
		txnStateMsg.setInstitutionCode(payment.getInstitutionCode());
		txnStateMsg.setRetrievalRef(payment.getRetrievalRef());
		txnStateMsg.setTransactionId(String.valueOf(requestInfo.getTransactionId()));
		nonStopClient.put(txnStateMsg);
	}

	private void createAndSendCreditNotification(CreditNotificationRequest payment,
			CreditNotificationInfo requestInfo) {

		List<PosNotificationEntity> posNotification = posNotificationService.findByPosId(requestInfo.getPosId());

		if (CollectionUtils.isEmpty(posNotification)) {
			logger.info("posNotification data does not exist for tid " + requestInfo.getTid());
			return;
		}
		Gson gson = new Gson();
		NotificationRequest notification = new NotificationRequest();
		notification.setRetrievalRef(payment.getRetrievalRef());
		notification.setMti("8200");
		notification.setProcessCode("420000");
		notification.setStan(requestInfo.getStan());
		notification.setTransactionTime(requestInfo.getTransactionTime());
		notification.setTransactionDate(requestInfo.getTransactionDate());
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
		
		fraudwalClient.put(wrapper);
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
							new BaseBusinessException("06", "Dynamic QR Exception Reversal Failed"));

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