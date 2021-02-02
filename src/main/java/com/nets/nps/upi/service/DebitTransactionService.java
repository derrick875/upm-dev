package com.nets.nps.upi.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.DebitTransactionRequest;
import com.nets.nps.upi.entity.DebitTransactionRequestTrxInfo;
import com.nets.nps.upi.entity.DebitTransactionResponse;
import com.nets.nps.upi.entity.DiscountDetail;
import com.nets.nps.upi.entity.MessageResponse;
import com.nets.nps.upi.entity.PullDebitRequest;
import com.nets.nps.upi.entity.PullDebitResponse;
import com.nets.nps.upi.entity.UpiProxyRequest;
import com.nets.nps.upi.service.impl.PullDebitRequestAdapter;
import com.nets.nps.upi.service.impl.WalletAdapter;
import com.nets.upos.commons.constants.IDRegistryKeyTypes;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.enums.ContextData;
import com.nets.upos.commons.enums.ForeignAcqAuthState;
import com.nets.upos.commons.enums.QrQueryState;
import com.nets.upos.commons.enums.QualificationState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import com.nets.upos.core.entity.AcquirerEntity;
import com.nets.upos.core.entity.CurrencyEntity;
import com.nets.upos.core.entity.DebitRequestDetails;
import com.nets.upos.core.entity.ForeignAcquirerAuthorizationEntity;
import com.nets.upos.core.entity.ForeignAcquirerEntity;
import com.nets.upos.core.entity.IDRegistryRequest;
import com.nets.upos.core.entity.IssuerPayAdviceEntity;
import com.nets.upos.core.entity.QrQueryDetails;
import com.nets.upos.core.entity.QualificationEntity;
import com.nets.upos.core.entity.SOFTypeEntity;
import com.nets.upos.core.entity.TransactionBasicsEntity;
import com.nets.upos.core.service.AcquirerService;
import com.nets.upos.core.service.CurrencyService;
import com.nets.upos.core.service.DebitRequestDetailService;
import com.nets.upos.core.service.ForeignAcquirerAuthorizationService;
import com.nets.upos.core.service.ForeignAcquirerService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.IssuerPayAdviceService;
import com.nets.upos.core.service.QrQueryDetailsService;
import com.nets.upos.core.service.QualificationService;
import com.nets.upos.core.service.TransactionBasicsService;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;

@Service
public class DebitTransactionService {

	private static final ApsLogger logger = new ApsLogger(DebitTransactionService.class);

	@Autowired
	private PullDebitRequestAdapter pullDebitRequestAdapter;

	@Autowired
	private WalletAdapter walletAdapter;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private AcquirerService acquirerService;

	@Autowired
	private TransactionBasicsService transactionBasicsService;

	@Autowired
	private IDRegistryService idRegistryService;

	@Autowired
	private ForeignAcquirerAuthorizationService foreignAcquirerAuthorizationService;

	@Autowired
	private IssuerPayAdviceService issuerPayAdviceService;

	@Autowired
	private ForeignAcquirerService foreignAcquirerService;

	@Autowired
	private QrQueryDetailsService qrQueryDetailsService;
	
	@Autowired
	private QualificationService qualificationService;
	
	@Autowired
	private DebitRequestDetailService debitService;
	
	@Value("${bank.service.base.url}")
	private String bankUrlPrefix;

	@Value("#{${bank.url}}")
	private Map<String, String> bankUrlSuffixMap;

	@Value("#{${nets.upi.appgateway.instId.map}}")
	private Map<String, String> instIdMap;

	@Value("#{${nets.upi.appgateway.uri.map}}")
	private Map<String, String> sofUriMap;

	@Value("${promptQrQuery.timeout.seconds:300}")
	private Integer qrQueryTimeoutInSeconds;
	
	private String netsInsId;
	private SOFTypeEntity sofType;
	private AcquirerEntity acquirer;
	private ForeignAcquirerEntity foreignAcquirer;

	@ServiceActivator
	public String process(String message) throws BaseBusinessException, JsonFormatException {
		logger.info("DebitUpiRequest is: " +message);
		UpiProxyRequest upiProxyRequest = (UpiProxyRequest) UtilComponents.getObjectFromString(message, UpiProxyRequest.class);
		String debitTransactionRequestString = upiProxyRequest.getUpiProxyRequestJsonData();
		DebitTransactionRequest debitTransactionRequest = (DebitTransactionRequest) UtilComponents.getObjectFromString(debitTransactionRequestString, DebitTransactionRequest.class);

		PullDebitRequest pullDebitRequest = pullDebitRequestAdapter.convertToPullDebitRequest(debitTransactionRequest);

		// Save details to data base
		// 1. Transaction Basics Entity
		TransactionBasicsEntity txnBasics = createTransactionBasicsEntry(debitTransactionRequest);
		// 2. IDRegistryRequest 
		handleIDRegistry(debitTransactionRequest, txnBasics);
		// 3. ForeignAcquirerAuthorizationEntity
		ForeignAcquirerAuthorizationEntity foreignAcqAuth = createForeignAcquirerAuthDetails(pullDebitRequest.getAcquirerInstitutionCode());
		// qr query details
		QrQueryDetails qrQueryDetails = createAndSaveQrQueryDetails(debitTransactionRequest, pullDebitRequest.getAcquirerInstitutionCode());
		// qualification
		QualificationEntity qualification = createAndSaveQualificationEntity(debitTransactionRequest, pullDebitRequest);
		//4. IssuerPayAdviceEntity
		IssuerPayAdviceEntity issuerPayAdvice = createIssuerPayAdviceEntry(debitTransactionRequest, txnBasics, foreignAcqAuth, qrQueryDetails, qualification);
		//5. Debit Request Details
		DebitRequestDetails debitDetails = createAndSaveDebitRequestDetails(debitTransactionRequest);
		
		//TODO tokenized

		String postUrl = getPostUrl(debitTransactionRequest.getMsgInfo().getMsgType());
		HttpEntity<String> entity = convertRequestToHttpEntity(pullDebitRequest);
		ResponseEntity<String> bankResponse = null;
		bankResponse = walletAdapter.sendAndReceiveFromBank(postUrl, entity);
		// check null for bank response

		PullDebitResponse pullDebitResponse = (PullDebitResponse) UtilComponents.getObjectFromString(bankResponse.getBody(), PullDebitResponse.class);
		DebitTransactionResponse debitTransactionResponse = createDebitTransactionResponse(debitTransactionRequest, pullDebitResponse);
		String debitTransactionResponseString = UtilComponents.getStringFromObject(debitTransactionResponse);
		logger.info(debitTransactionResponseString);

		// update db
		updateForeignAcquirerAuthDetails(foreignAcqAuth, pullDebitResponse);
//		updateIssuerPayAdviceEntity();
//		updateTbe();
		
		String responseString = createResponseAndChangeToString(upiProxyRequest, debitTransactionResponseString);

		return responseString;
	}
	
//	private void updateTransactionBasicsEntry(APSRequestWrapper wrapper, TransactionBasicsEntity tbEntity) {
//		tbEntity.setState(wrapper.getResponse().getTransactionState()); i dont have transaction state in pull debit response
//		TransactionBasicsEntity txnBasics = transactionBasicsService.save(tbEntity);
//		wrapper.addToContext(ContextData.TRANSACTION_BASIC, txnBasics);
//
//	}
//	private void updateIssuerPayAdviceEntity() {
//		if (wrapper.getResponse().getAcquirerSettlementDate() != null) { // i dont have acquirer settlement date?
//			ipadvice.setExternalSettlementDate(wrapper.getResponse().getAcquirerSettlementDate());
//		}
//		issuerPayAdviceService.save(ipadvice);
//	}

	private void updateForeignAcquirerAuthDetails(ForeignAcquirerAuthorizationEntity faae, PullDebitResponse pullDebitResponse) {
		faae.setState(ForeignAcqAuthState.COMPLETED.getState());
		faae.setRetrievalRefNo(pullDebitResponse.getRetrievalRef()); //check
		faae.setAcquirerTransactionRef(pullDebitResponse.getDebitApprovalRetrievalRef()); // check
		faae.setAcquirerProcessingTime(pullDebitResponse.getTransmissionTime());
		faae.setApprovalCode(pullDebitResponse.getDebitApprovalCode());
//		faae.setActionCode(actionCode); what this
		faae.setResponseCode(pullDebitResponse.getResponseCode());
		foreignAcquirerAuthorizationService.save(faae);
		
	}

	private TransactionBasicsEntity createTransactionBasicsEntry(DebitTransactionRequest debitTransactionRequest) throws BaseBusinessException {
		DebitTransactionRequestTrxInfo trxInfo = debitTransactionRequest.getTrxInfo();
		TransactionBasicsEntity txnBasics = new TransactionBasicsEntity();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		netsInsId = UtilComponents.getKey(instIdMap, debitTransactionRequest.getMsgInfo().getInsID());
		String sofUri = sofUriMap.get(netsInsId);

		Map<ContextData, Object> entityMap = acquirerService.getAcquirerSofDetails(sofUri);
		sofType = (SOFTypeEntity) entityMap.get(ContextData.SOF_TYPE);
		acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		txnBasics.setStan(trxInfo.getTraceNum());
		txnBasics.setAuthorizeDate(currentTimeStamp);
		txnBasics.setState(TransactionState.STATE_PENDING.getState());
		txnBasics.setTranType(TransactionTypes.TYPE_OUTBOUND_PAYMENT_ADVICE); 
		txnBasics.setAuthResponseId(trxInfo.getRetrivlRefNum()); 
		//		txnBasics.setAuthResponseCode(trxInfo.getre); 
		//		txnBasics.setAuthApprovalCode(authApprovalCode); check response pull debit response (need to follow upi specs)
		txnBasics.setSourceAmount(new BigDecimal(trxInfo.getTrxAmt()));
		
		CurrencyEntity sourceCurrency = currencyService.getCurrencyByCurrencyIsoNumber(trxInfo.getTrxCurrency());
		notNull.test(sourceCurrency).throwIfInvalid(BusinessValidationErrorCodes.AMOUNT_CURRENCY_NOT_FOUND);

		if(trxInfo.getTrxCurrency() != null) {
			txnBasics.setSourceCurrencyId(sourceCurrency.getCurrencyId());
		}
		txnBasics.setTargetAmount(new BigDecimal(trxInfo.getBillAmt()));
		
		CurrencyEntity targetCurrency = currencyService.getCurrencyByCurrencyIsoNumber(trxInfo.getBillCurrency());
		notNull.test(targetCurrency).throwIfInvalid(BusinessValidationErrorCodes.CURRENCY_NOT_FOUND);

		txnBasics.setTargetCurrencyId(targetCurrency.getCurrencyId());
		txnBasics.setCardTypeId(sofType.getCardTypeId());
		txnBasics.setIsDccTransaction('Y');
		txnBasics.setMid(trxInfo.getMerchantInfo().getMid());
		txnBasics.setTid(trxInfo.getMerchantInfo().getTermId());
		txnBasics.setAcqMerMappingId(null);
		txnBasics.setAcquirerId(acquirer.getAcquirerId());
		txnBasics.setCreationDate(currentTimeStamp);
		txnBasics.setLastUpdatedDate(currentTimeStamp);
		transactionBasicsService.save(txnBasics);

		return txnBasics;
	}
	
	private void handleIDRegistry(DebitTransactionRequest debitTransactionRequest, TransactionBasicsEntity tbe) throws BaseBusinessException {
		try {
			createIdRegistry(debitTransactionRequest.getTrxInfo().getRetrivlRefNum());
		} catch (DataIntegrityViolationException ex) {
			tbe.setState(TransactionState.STATE_FAILED.getState());
			transactionBasicsService.save(tbe);
			throw new BaseBusinessException("68",
					"outbound payment inquiry reach first OR duplicate outbound paymentadvice for the same bank rrn.");
		}
	}

	private void createIdRegistry(String rrn) {
		IDRegistryRequest idRegReq = new IDRegistryRequest();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());

		idRegReq.setKeyType(IDRegistryKeyTypes.OUTBOUND_QR_PAYMENT);
		StringBuffer keyDataBuffer = new StringBuffer();
		keyDataBuffer.append(netsInsId).append("_").append(rrn);
		idRegReq.setKeyData(keyDataBuffer.toString());

		idRegReq.setCreationDate(currentTimeStamp);

		idRegistryService.saveIDRegistry(idRegReq);
	}
	
	private ForeignAcquirerAuthorizationEntity createForeignAcquirerAuthDetails(String acquirerInsCode) {
		foreignAcquirer = foreignAcquirerService.getForeignAcquirer(acquirerInsCode);
		ForeignAcquirerAuthorizationEntity faae = new ForeignAcquirerAuthorizationEntity();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		faae.setForeignAcquirerId(foreignAcquirer.getId());
		faae.setState(ForeignAcqAuthState.PROCESSING.getState());
		faae.setCreationDate(currentTimeStamp);
		foreignAcquirerAuthorizationService.save(faae);
		return faae;
	}
	
	private QrQueryDetails createAndSaveQrQueryDetails(DebitTransactionRequest debitTransactionRequest, String acquirerInsCode) {
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		QrQueryDetails qqd = new QrQueryDetails();
		qqd.setRetrievalRefNo(debitTransactionRequest.getTrxInfo().getRetrivlRefNum());
//		qqd.setPaymentRequesterInstitutionId(paymentRequesterInstitutionId); // institution code??
		qqd.setForeignAcquirerInstitutionCode(acquirerInsCode); // double check
		qqd.setState(QrQueryState.PROCESSING.getState());
		qqd.setForeignAcquirerId(foreignAcquirer.getId());
		qqd.setTransmissionTime(debitTransactionRequest.getTrxInfo().getTransDatetime());
		qqd.setCardTypeId(sofType.getCardTypeId());
		qqd.setAcquirerId(acquirer.getAcquirerId());
		qqd.setCreationDate(currentTimeStamp);
		qqd.setLastUpdatedDate(currentTimeStamp);
		qqd.setQueryTimeout(qrQueryTimeoutInSeconds);
		
		qrQueryDetailsService.save(qqd);
		logger.info("QrQueryDetails saved, " + qqd);
		return qqd;
	}

	private QualificationEntity createAndSaveQualificationEntity(DebitTransactionRequest debitTransactionRequest, PullDebitRequest pullDebitRequest) throws BaseBusinessException {
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());

		QualificationEntity qualification = new QualificationEntity();
		qualification.setRetrievalRefNo(debitTransactionRequest.getTrxInfo().getRetrivlRefNum());
		qualification.setState(QualificationState.PROCESSING.getState());
		
		CurrencyEntity sourceCurrency = currencyService.getCurrencyByCurrencyIsoNumber(debitTransactionRequest.getTrxInfo().getTrxCurrency()); 
		qualification.setConvertCurrencyId(sourceCurrency.getCurrencyId());
		
		if(notEmpty.test(sourceCurrency.getUnits()).isvalid()) {
			qualification.setConvertCurrencyMinorUnit(Integer.parseInt(sourceCurrency.getUnits()));
		}
		
		CurrencyEntity targetCurrency = currencyService.getCurrencyByCurrencyIsoNumber(debitTransactionRequest.getTrxInfo().getBillCurrency());
		qualification.setAmountCurrencyId(targetCurrency.getCurrencyId());
		
		if(notEmpty.test(targetCurrency.getUnits()).isvalid()) {
			qualification.setAmountCurrencyMinorUnit(Integer.parseInt(targetCurrency.getUnits()));
		}
		
		qualification.setAmount(new BigDecimal(debitTransactionRequest.getTrxInfo().getBillAmt()));
		qualification.setInstitutionCode(debitTransactionRequest.getMsgInfo().getInsID());
		qualification.setForeignAcquirerInstitutionCode(pullDebitRequest.getAcquirerInstitutionCode());
		qualification.setCardTypeId(sofType.getCardTypeId());//check
		qualification.setAcquirerId(acquirer.getAcquirerId());
		qualification.setTid(debitTransactionRequest.getTrxInfo().getMerchantInfo().getTermId());
		qualification.setMid(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMid());
		qualification.setTransmissionTime(debitTransactionRequest.getTrxInfo().getTransDatetime());
//		qualification.setVerificationToken(verificationToken); check what this
		qualification.setCreationDate(currentTimeStamp);
		qualification.setLastUpdatedDate(currentTimeStamp);
		qualificationService.save(qualification);
		logger.info("Qualification saved", qualification);
		return qualification;
	}
	
	private IssuerPayAdviceEntity createIssuerPayAdviceEntry(DebitTransactionRequest debitTransactionRequest, TransactionBasicsEntity transactionBasics, ForeignAcquirerAuthorizationEntity foreignAcqAuth, QrQueryDetails qrQueryDetails, QualificationEntity qualification) {
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		IssuerPayAdviceEntity ipe = new IssuerPayAdviceEntity();
		ipe.setTransactionBasicsId(transactionBasics.getId());
		ipe.setQrQueryId(qrQueryDetails.getId());
		ipe.setForeignAcquirerAuthorizationId(foreignAcqAuth.getId());
		ipe.setForeignAcquirerId(foreignAcquirer.getId());
		ipe.setQualificationId(qualification.getId());
		ipe.setRetrievalRefNo(debitTransactionRequest.getTrxInfo().getRetrivlRefNum());
		ipe.setStan(debitTransactionRequest.getTrxInfo().getTraceNum());
		ipe.setTransactionDate(debitTransactionRequest.getTrxInfo().getTransDatetime().substring(0, 4)); 
		ipe.setTransactionTime(debitTransactionRequest.getTrxInfo().getTransDatetime().substring(4, 10));
		ipe.setPaymentRequesterInstId(debitTransactionRequest.getMsgInfo().getInsID()); // check
		ipe.setForeignAcquirerInstitutionCode(qrQueryDetails.getForeignAcquirerInstitutionCode());
		//		ipe.setSofAccountId(sofAccountId); tokenization
		//		ipe.setInvoiceRef(debitTransactionRequest.getTrxInfo().getre);
		//		ipe.setUserData(request.getAdditionalData(AdditionalData.USER_DATA));
		ipe.setCreationDate(currentTimeStamp);
		ipe.setLastUpdatedDate(currentTimeStamp);

		issuerPayAdviceService.save(ipe);

		return ipe;
	}
	
	private DebitRequestDetails createAndSaveDebitRequestDetails(DebitTransactionRequest debitTransactionRequest) {
		DebitTransactionRequestTrxInfo trxInfo = debitTransactionRequest.getTrxInfo();
		
		DebitRequestDetails debitDetails = new DebitRequestDetails();
		debitDetails.setMessageID(debitTransactionRequest.getMsgInfo().getMsgId());
		debitDetails.setCorrelationId("correlation id"); // check this
		debitDetails.setMessageType(debitTransactionRequest.getMsgInfo().getMsgType());
		debitDetails.setBillAmt(new BigDecimal(trxInfo.getBillAmt()));
		debitDetails.setBillCurrency(Integer.valueOf(trxInfo.getBillCurrency()));
		debitDetails.setBillConvRate(new BigDecimal(trxInfo.getBillConvRate()));
		debitDetails.setConvDate(trxInfo.getConvDate());
		debitDetails.setDebitAccountInfo(trxInfo.getDebitAccountInfo());
		debitDetails.setFeeAmt(new BigDecimal(trxInfo.getFeeAmt()));
		debitDetails.setFwdIIN(trxInfo.getMerchantInfo().getFwdIIN());
		debitDetails.setMarkupAmt(new BigDecimal(trxInfo.getMarkupAmt()));
		debitDetails.setMcc(trxInfo.getMerchantInfo().getMcc());
		debitDetails.setMerchantCountry(trxInfo.getMerchantInfo().getMerchantCountry());
		debitDetails.setMerchantName(trxInfo.getMerchantInfo().getMerchantName());
		debitDetails.setMid(trxInfo.getMerchantInfo().getMid());
		debitDetails.setTrxAmt(new BigDecimal(trxInfo.getTrxAmt()));
		debitDetails.setTrxCurrency(trxInfo.getTrxCurrency());
		debitDetails.setSettAmt(new BigDecimal(trxInfo.getSettAmt()));
		debitDetails.setSettDate(trxInfo.getSettDate());
		debitDetails.setPosEntryModeCode(trxInfo.getPosEntryModeCode());
		debitDetails.setRetreiveRefNum(trxInfo.getRetrivlRefNum());
		BigDecimal discount = null ;
		List<DiscountDetail> discountList= trxInfo.getDiscountDetails();
		if(discountList.size()>0) {
			discount=new BigDecimal(discountList.get(0).getDiscountAmt());
		}
		debitDetails.setDiscountAmt(discount);
		
		debitService.save(debitDetails);
		return debitDetails;
	}
	
	private String getPostUrl(String msgType) {
		String bankSuffix = bankUrlSuffixMap.get(msgType);
		String postUrl = bankUrlPrefix + bankSuffix;
		logger.info("Sending to url: " + postUrl);
		return postUrl;
	}

	private HttpEntity<String> convertRequestToHttpEntity(PullDebitRequest pullDebitRequest) {
		String pullDebitRequestString = UtilComponents.getStringFromObject(pullDebitRequest);
		HttpEntity<String> entity = new HttpEntity<String>(pullDebitRequestString);
		return entity;
	}

	private String createResponseAndChangeToString(UpiProxyRequest upiProxyRequest, String debitTransactionResponseString) {
		UpiProxyRequest response = new UpiProxyRequest();
		response.setBankId(upiProxyRequest.getBankId());
		response.setBankType(upiProxyRequest.getBankType());
		response.setCorrelationId(upiProxyRequest.getCorrelationId());
		response.setInstCode(upiProxyRequest.getInstCode());
		response.setTransactionType(upiProxyRequest.getTransactionType());
		response.setUpiProxyRequestJsonData(debitTransactionResponseString);

		String responseString = UtilComponents.getStringFromObject(response);
		return responseString;
	}

	private DebitTransactionResponse createDebitTransactionResponse(DebitTransactionRequest debitTransactionRequest, PullDebitResponse pullDebitResponse) {
		DebitTransactionResponse debitTransactionResponse = new DebitTransactionResponse();
		debitTransactionResponse.setMsgInfo(debitTransactionRequest.getMsgInfo());
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode(pullDebitResponse.getResponseCode());
		if(pullDebitResponse.getResponseCode().equals("00")) {
			msgResponse.setResponseMsg("Approved");
		}
		debitTransactionResponse.setMsgResponse(msgResponse);

		return debitTransactionResponse;
	}
}
