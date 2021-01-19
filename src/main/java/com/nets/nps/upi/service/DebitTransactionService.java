package com.nets.nps.upi.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
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
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.core.entity.AcquirerEntity;
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
import com.nets.upos.core.service.ForeignAcquirerAuthorizationService;
import com.nets.upos.core.service.ForeignAcquirerService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.IssuerPayAdviceService;
import com.nets.upos.core.service.TransactionBasicsService;

@Service
public class DebitTransactionService {

	private static final ApsLogger logger = new ApsLogger(DebitTransactionService.class);

	@Autowired
	PullDebitRequestAdapter pullDebitRequestAdapter;

	@Autowired
	WalletAdapter walletAdapter;

	@Autowired
	CurrencyService currencyService;

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

	@Value("${bank.service.base.url}")
	private String bankUrlPrefix;

	@Value("#{${bank.url}}")
	private Map<String, String> bankUrlSuffixMap;

	@Value("#{${nets.upi.appgateway.instId.map}}")
	private Map<String, String> instIdMap;

	@Value("#{${nets.upi.appgateway.uri.map}}")
	private Map<String, String> sofUriMap;

	private String netsInsId;
	private SOFTypeEntity sofType;
	private AcquirerEntity acquirer;
	private ForeignAcquirerEntity foreignAcquirer;
	private QrQueryDetails queryDetails;
	private QualificationEntity qualification;

	@ServiceActivator
	public String process(String message) throws BaseBusinessException, JsonFormatException {
		logger.info("DebitUpiRequest is: " +message);
		UpiProxyRequest upiProxyRequest = (UpiProxyRequest) UtilComponents.getObjectFromString(message, UpiProxyRequest.class);
		String debitTransactionRequestString = upiProxyRequest.getUpiProxyRequestJsonData();
		DebitTransactionRequest debitTransactionRequest = (DebitTransactionRequest) UtilComponents.getObjectFromString(debitTransactionRequestString, DebitTransactionRequest.class);

		// convert to pull debit req
		PullDebitRequest pullDebitRequest = pullDebitRequestAdapter.convertToPullDebitRequest(debitTransactionRequest);

		// Save details to data base
		// 1. Transaction Basics Entity
		TransactionBasicsEntity txnBasics = createTransactionBasicsEntry(debitTransactionRequest);
		// 2. IDRegistryRequest 
		handleIDRegistry(debitTransactionRequest, txnBasics);
		// 3. ForeignAcquirerAuthorizationEntity
		ForeignAcquirerAuthorizationEntity foreignAcqAuth = createForeignAcquirerAuthDetails(pullDebitRequest.getAcquirerInstitutionCode());
		//4. IssuerPayAdviceEntity
		IssuerPayAdviceEntity issuerPayAdvice = createIssuerPayAdviceEntry(debitTransactionRequest, txnBasics, foreignAcqAuth);

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

		String responseString = createResponseAndChangeToString(upiProxyRequest, debitTransactionResponseString);

		return responseString;
	}

	private IssuerPayAdviceEntity createIssuerPayAdviceEntry(DebitTransactionRequest debitTransactionRequest, TransactionBasicsEntity transactionBasics, ForeignAcquirerAuthorizationEntity foreignAcqAuth) {
//		qualification = (QualificationEntity) entityMap.get(ContextData.QR_QUALIFICATION_DETAILS);
//		queryDetails = (QrQueryDetails) entityMap.get(ContextData.QR_QUERY_DETAILS);
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		IssuerPayAdviceEntity ipe = new IssuerPayAdviceEntity();
		ipe.setTransactionBasicsId(transactionBasics.getId());
//		ipe.setQrQueryId(queryDetails.getId()); means when qr query need to save in query details?
		ipe.setForeignAcquirerAuthorizationId(foreignAcqAuth.getId());
//		ipe.setQualificationId(qualification.getId());
		ipe.setRetrievalRefNo(debitTransactionRequest.getTrxInfo().getRetrivlRefNum());
		ipe.setStan(debitTransactionRequest.getTrxInfo().getTraceNum());
		ipe.setTransactionDate(debitTransactionRequest.getTrxInfo().getTransDatetime().substring(1, 4)); // double check 
		//		ipe.setTransactionTime(transactionTime);
		//		ipe.setPaymentRequesterInstId(request.getAcquiringInstitutionIdentificationCode());
		//		ipe.setForeignAcquirerInstitutionCode(request.getForeignAcquirerInstitutionCode());
		//		ipe.setSofAccountId(sofAccountId); tokenization
		//		ipe.setInvoiceRef(debitTransactionRequest.getTrxInfo().getre);
		//		ipe.setUserData(request.getAdditionalData(AdditionalData.USER_DATA));
		ipe.setCreationDate(currentTimeStamp);
		ipe.setLastUpdatedDate(currentTimeStamp);

		issuerPayAdviceService.save(ipe);

		return ipe;
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

	private TransactionBasicsEntity createTransactionBasicsEntry(DebitTransactionRequest debitTransactionRequest) throws BaseBusinessException {
		DebitTransactionRequestTrxInfo trxInfo = debitTransactionRequest.getTrxInfo();
		TransactionBasicsEntity txnBasics = new TransactionBasicsEntity();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		netsInsId = UtilComponents.getKey(instIdMap, debitTransactionRequest.getMsgInfo().getInsID());
		String sofUri = sofUriMap.get(netsInsId);

		Map<ContextData, Object> entityMap = acquirerService.getAcquirerSofDetails(sofUri);
		sofType = (SOFTypeEntity) entityMap.get(ContextData.SOF_TYPE);
		acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
//		txnBasics.setPosId(Integer.valueOf(trxInfo.getPosEntryModeCode()));  // check this 
//		txnBasics.setMerchantId(Integer.valueOf(trxInfo.getMerchantInfo().getMid())); // double check this 
		txnBasics.setStan(trxInfo.getTraceNum()); // double check
		txnBasics.setAuthorizeDate(currentTimeStamp);
		txnBasics.setState(TransactionState.STATE_PENDING.getState());
				txnBasics.setTranType(TransactionTypes.TYPE_OUTBOUND_PAYMENT_ADVICE); // need to add transaction type in commons?
		txnBasics.setAuthResponseId(trxInfo.getRetrivlRefNum()); // need to check this for duplicate?? if not will keep saving even if same retrieval ref
		//		txnBasics.setAuthResponseCode(trxInfo.getre); 
		//		txnBasics.setAuthApprovalCode(authApprovalCode); check response pull debit response (need to follow upi specs)
		txnBasics.setSourceAmount(new BigDecimal(trxInfo.getTrxAmt()));
		if(trxInfo.getTrxCurrency() != null) {
			txnBasics.setSourceCurrencyId(currencyService.getCurrencyByCurrencyIsoNumber(trxInfo.getTrxCurrency()).getCurrencyId());
		}
		txnBasics.setTargetAmount(new BigDecimal(trxInfo.getBillAmt())); // check 
		txnBasics.setTargetCurrencyId(currencyService.getCurrencyByCurrencyIsoNumber(trxInfo.getBillCurrency()).getCurrencyId());
		txnBasics.setCardTypeId(sofType.getCardTypeId());
		txnBasics.setIsDccTransaction('Y');
		txnBasics.setMid(debitTransactionRequest.getMsgInfo().getMsgId()); // mid is merchant id
		txnBasics.setTid(trxInfo.getMerchantInfo().getTermId()); // double check this
		txnBasics.setAcqMerMappingId(null);
		txnBasics.setAcquirerId(acquirer.getAcquirerId());
		txnBasics.setCreationDate(currentTimeStamp);
		txnBasics.setLastUpdatedDate(currentTimeStamp);
		// tbe.setAcqMerAcctId(null);
		// tbe.setPaymentStatus(null);
		// tbe.setBatchStatus(null);
		transactionBasicsService.save(txnBasics);

		return txnBasics;
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
