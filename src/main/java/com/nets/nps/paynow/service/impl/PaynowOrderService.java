package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.BooleanValidationHelpers.isAccepted;
import static com.nets.upos.commons.validations.helper.ListValidationHelpers.notEmptyList;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nets.nps.core.service.PaymentService;
import com.nets.nps.paynow.entity.OrderInfo;
import com.nets.nps.paynow.entity.OrderRequest;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.utils.DynamicPaynowQrImageUtil;
import com.nets.nps.paynow.utils.PaynowUtil;
import com.nets.upos.commons.constants.IDRegistryKeyTypes;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.enums.BankConfigAttributeName;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import com.nets.upos.core.entity.AcqMerAccountEntity;
import com.nets.upos.core.entity.BankCfgAttrEntity;
import com.nets.upos.core.entity.BankEntity;
import com.nets.upos.core.entity.CodesEntity;
import com.nets.upos.core.entity.IDRegistryRequest;
import com.nets.upos.core.entity.MerchantCfgAttrEntity;
import com.nets.upos.core.entity.PhysicalPosEntity;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.service.AcqMerAccountService;
import com.nets.upos.core.service.BankService;
import com.nets.upos.core.service.CodesService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.MerchantService;
import com.nets.upos.core.service.PhysicalPosService;
import com.nets.upos.core.service.RequestService;

@Service
public class PaynowOrderService implements PaymentService<OrderRequest, OrderInfo> {

	private static final ApsLogger logger = new ApsLogger(PaynowOrderService.class);
	public static final char HYPHEN = '-';

	@Value("${paynow.order.timeout.seconds:45}")
	private Integer payNowOrderTimeout;

	@Value("${paynow.dynamicQr.image.size.in.pixels:250}")
	private Integer qrImageSize;

	@Autowired
	private IDRegistryService idRegistryService;

	@Autowired
	private CodesService codesService;

	@Autowired
	private PhysicalPosService posService;

	@Autowired
	private AcqMerAccountService acqMerAccountService;

	@Autowired
	private PaynowTransactionDataService paynowTransactionDataService;

	@Autowired
	private RequestService requestService;

	@Autowired
	private BankService bankService;

	@Autowired
	private MerchantService merchantService;

	@Override
	public OrderInfo process(OrderRequest order) throws Exception {
		checkDuplicateRetrivalRef(order);
		OrderInfo orderInf = ValidatePaynowOrder(order);
		saveRequest(order, orderInf);
		OrderInfo updatedOrderInfo = generatePaynowQr(order, orderInf);

		return updatedOrderInfo;

	}

	private void checkDuplicateRetrivalRef(OrderRequest order) throws BaseBusinessException {
		try {
			createIdRegistry(order.getRetrievalRef(), order.getInstitutionCode());
		} catch (RuntimeException ex) {
			throw new BaseBusinessException(BusinessValidationErrorCodes.ORDER_PREVIOUSLY_RECEIVED);
		}
	}

	private void createIdRegistry(String institutionCode, String rrn) {
		IDRegistryRequest idRegReq = new IDRegistryRequest();
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		idRegReq.setKeyType(IDRegistryKeyTypes.PAYNOW_QR_ORDER);

		StringBuffer keyDataBuffer = new StringBuffer().append(institutionCode).append("_").append(rrn);
		idRegReq.setKeyData(keyDataBuffer.toString());

		idRegReq.setCreationDate(currentTimeStamp);
		idRegistryService.saveIDRegistry(idRegReq);
	}

	private OrderInfo ValidatePaynowOrder(OrderRequest orderReq) throws Exception {
		OrderInfo orderInfo = new OrderInfo();
		Optional.ofNullable(orderReq.getTransactionDomainData().getPaymentAmount()).ifPresent(s -> orderInfo
				.setAmount(new BigDecimal(s).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN))));
		isAccepted(true).test(orderInfo.getAmount().compareTo(BigDecimal.ZERO)>0).throwIfInvalid(BusinessValidationErrorCodes.INVALID_AMOUNT);
		PhysicalPosEntity pos = posService.findAndValidatePos(orderReq.getTransactionDomainData().getHostTid());
		orderInfo.setPosId(pos.getPosId());
		orderInfo.setMerchantId(pos.getMerchant().getMerchantId());
		orderInfo.setMid(pos.getRid());
		// Short Name,MPAN retrieval
		List<CodesEntity> codesEntityList = codesService.findByCodeName("PAYNOW_ACQUIRER");
		List<Integer> acqIdList = new ArrayList<Integer>();
		for (CodesEntity a : codesEntityList) {
			acqIdList.add(a.getIntegRefValue());
		}
		List<AcqMerAccountEntity> ama = acqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(acqIdList,
				orderInfo.getPosId(), orderReq.getTransactionDomainData().getPaymentCurrency());
		notEmptyList.test(ama).throwIfInvalid(BusinessValidationErrorCodes.ACQUIRER_MAPPING_NOT_FOUND);
		orderInfo.setmPan(ama.get(0).getEcMid());
		notEmpty.and(isEqualLength(16)).test(orderInfo.getmPan())
		.throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_MPAN);

		isAccepted(true).test(ama.get(0).getAcquirerMerchantMapping().isActive())
		.throwIfInvalid(BusinessValidationErrorCodes.ACQUIRER_DISABLED_FOR_MERCHANT);

		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = merchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(orderInfo.getMerchantId(), "PAYNOW_SHORT_NAME");
		notEmptyList.test(merchantCfgAttrEntityList)
		.throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_SHORTNAME);
		String merchantShortNameOrig = merchantCfgAttrEntityList.get(0).getAttributeValue();
		notEmpty.test(merchantShortNameOrig)
		.throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_SHORTNAME);

		String merchantShortName = StringUtils.rightPad(merchantShortNameOrig.substring(0, Math.min(merchantShortNameOrig.length(), 8)), 8);
		
		orderInfo.setMerchantShortName(merchantShortName);
		logger.debug("Merchant Short Name:" + orderInfo.getMerchantShortName());

		notEmpty.and(isEqualLength(8)).test(orderInfo.getMerchantShortName())
		.throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_SHORTNAME);
		orderInfo.setStan(String.format("%06d",
				Integer.parseInt(paynowTransactionDataService.getSequenceNumber("paynow_dynamic_stan"))));
		orderInfo.setSeqNum(String.format("%08d",
				Integer.parseInt(paynowTransactionDataService.getSequenceNumber("paynow_dynamic_seqnum"))));
		logger.debug("stan :" + orderInfo.getStan() + "    Seqnum:" + orderInfo.getSeqNum());

		if (payNowOrderTimeout > Integer.parseInt(orderReq.getTransactionDomainData().getValidityTime())) {
			orderInfo.setQrValidityTime(Integer.parseInt(orderReq.getTransactionDomainData().getValidityTime()));
		} else {
			orderInfo.setQrValidityTime(payNowOrderTimeout);
		}
		logger.debug("Validity Time :" + orderInfo.getQrValidityTime());
		orderInfo.setCreationDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		Timestamp later = new Timestamp(
				orderInfo.getCreationDate().getTime() + (orderInfo.getQrValidityTime() * 1000L));
		orderInfo.setQrExpiryTime(new SimpleDateFormat("yyyyMMddHHmmss").format(later.getTime()));
		logger.debug("Expiry Time :" + orderInfo.getQrExpiryTime());
		orderInfo
		.setDynamicCode(
				new StringBuffer()
				.append(orderInfo.getSeqNum()).append(
						PaynowUtil
						.hashSHA2String(new StringBuffer().append(orderInfo.getmPan())
								.append(orderReq.getTransactionDomainData().getPaymentAmount())
								.append(orderReq.getTransactionDomainData()
										.getPaymentCurrency())
								.append(orderInfo.getStan()).append(orderInfo.getQrExpiryTime())
								.toString().toUpperCase())
						.substring(0, 8).toUpperCase())
				.toString());

		logger.debug("dynamic Code :" + orderInfo.getDynamicCode());
		orderInfo.setMerchantRef(new StringBuffer().append(orderInfo.getDynamicCode()).append("-")
				.append(orderInfo.getMerchantShortName()).toString());
		logger.debug("Merchant Reference Number : " + orderInfo.getMerchantRef());
		logger.debug("Search id :" + orderInfo.getMerchantRef().substring(0,16));

		BankEntity bank = bankService.findByAcquirerAndValidate(acqIdList.get(0));
		if(Objects.isNull(bank)) {
			throw new BaseBusinessException("15", "Bank not found");
		}
		logger.info("BankId:" + bank.getBankId());
		String bankExt = getBankExtension(bank.getBankCfgAttr());

		List<CodesEntity> codesEntity = codesService.findByCodeName("NETS_PAYNOW_UEN");
		String netsPaynowUen = codesEntity.get(0).getCodeValue();
		orderInfo.setProxyValue(netsPaynowUen+bankExt);
		return orderInfo;
	}

	private void saveRequest(OrderRequest orderReq, OrderInfo orderInfo) {

		Request request = new Request(orderInfo.getDynamicCode(), orderReq.getTransactionDomainData().getHostTid(),
				orderInfo.getMid(), orderInfo.getStan(), orderReq.getTransmissionTime());
		request.setRetrievalRefNo(orderReq.getRetrievalRef());
		request.setTransactionDate(orderReq.getTransactionDomainData().getTransactionDate());
		request.setTransactionTime(orderReq.getTransactionDomainData().getTransactionTime());
		request.setState(RequestState.WAITING.getState());
		request.setTargetCurrency(orderReq.getTransactionDomainData().getPaymentCurrency());
		request.setTargetAmount(orderInfo.getAmount());
		request.setMerchantId(orderInfo.getMerchantId());
		request.setPosId(orderInfo.getPosId());
		request.setRequestType(TransactionTypes.TYPE_ORDER);
		request.setCreationDate(orderInfo.getCreationDate());
		request.setOrderInstitutionId(orderReq.getInstitutionCode());
		request.setCaptureDate(orderReq.getTransactionDomainData().getTransactionDate());
		request.setOrderTimeout(orderInfo.getQrValidityTime());
		request.setInvoiceRef(orderReq.getTransactionDomainData().getInvoiceRef());

		Request savedRequest = requestService.save(request);

		logger.info("Order Process Completed", savedRequest);
	}

	private OrderInfo generatePaynowQr(OrderRequest orderReq, OrderInfo orderInfo) {

		StringBuffer dataBuf = new StringBuffer();
		dataBuf.append("0009SG.PAYNOW");
		dataBuf.append("01012");
		dataBuf.append("02" + orderInfo.getProxyValue().length() + orderInfo.getProxyValue());
		dataBuf.append("03010");
		//dataBuf.append("0414" + orderInfo.getQrExpiryTime());
		dataBuf.append("0525" + orderInfo.getMerchantRef());
		StringBuffer datawithTag26 = new StringBuffer();
		datawithTag26.append("26");
		datawithTag26.append(dataBuf.toString().length());
		datawithTag26.append(dataBuf.toString());
		StringBuffer datawithTag00 = new StringBuffer();
		datawithTag00.append("000201");
		StringBuffer datawithTag01 = new StringBuffer();
		datawithTag01.append("010212");
		StringBuffer datawithTag52 = new StringBuffer();
		datawithTag52.append("52040000");
		StringBuffer datawithTag53 = new StringBuffer();
		datawithTag53.append("5303702");
		StringBuffer datawithTag54 = new StringBuffer();
		String transactionAmount = String.format("%.2f", Double.parseDouble(orderReq.getTransactionDomainData().getPaymentAmount())/100);
		datawithTag54.append("54");
		String transactionAmountLength = StringUtils.leftPad(Integer.toString(transactionAmount.length()), 2, "0");
		datawithTag54.append(transactionAmountLength);
		datawithTag54.append(transactionAmount);
		StringBuffer datawithTag58 = new StringBuffer();
		datawithTag58.append("5802SG");
		StringBuffer datawithTag59 = new StringBuffer();
		datawithTag59.append("5902NA");
		StringBuffer datawithTag60 = new StringBuffer();
		datawithTag60.append("6009Singapore");

		StringBuffer paynowData = new StringBuffer();
		paynowData.append(datawithTag00);
		paynowData.append(datawithTag01);
		paynowData.append(datawithTag26);
		paynowData.append(datawithTag52);
		paynowData.append(datawithTag53);
		paynowData.append(datawithTag54);
		paynowData.append(datawithTag58);
		paynowData.append(datawithTag59);
		paynowData.append(datawithTag60);
		paynowData.append("6304");
		
		String msgCRC = calCRC16CCITT(paynowData.toString());
		paynowData.append(msgCRC);
		
		logger.debug("paynowTag26Data:" + datawithTag26.toString());	
		logger.debug("paynowTag54Data:" + datawithTag54.toString());
		logger.debug("paynow msgCRC:" + msgCRC.toString());
		logger.debug("paynowData:" + paynowData.toString());
		
		orderInfo.setQrImage(new DynamicPaynowQrImageUtil().generateQrImage(paynowData.toString(), qrImageSize));
		//logger.info("QR Image:"+orderInfo.getQrImage());

		return orderInfo;
	}

	private static String calCRC16CCITT(String input) {
		int crc = 0xFFFF;          // initial value
		int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

		byte[] bytes = input.getBytes();

		for (byte b : bytes) {
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b   >> (7-i) & 1) == 1);
				boolean c15 = ((crc >> 15    & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit) crc ^= polynomial;
			}
		}

		crc &= 0xffff;

		return String.format("%04x", crc & 0xffff).toUpperCase();
	}

	private String getBankExtension(Set<BankCfgAttrEntity> bankConfigs) {

		for (BankCfgAttrEntity bankConfig : bankConfigs) {

			if (BankConfigAttributeName.BANK_EXT.name().equals(bankConfig.getCfgAttrName())) {
				return bankConfig.getCfgAttrValue();
			}
		}

		return "";
	}
}
