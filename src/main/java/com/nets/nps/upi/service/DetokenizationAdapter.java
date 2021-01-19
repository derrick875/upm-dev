package com.nets.nps.upi.service;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.Detokenization;
import com.nets.nps.upi.entity.DetokenizationBody;
import com.nets.nps.upi.entity.DetokenizationHeader;
import com.nets.nps.upi.entity.DetokenizationSpecificData;
import com.nets.nps.upi.entity.QrcGenerationRequest;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.utils.DateUtil;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Service
public class DetokenizationAdapter {

	private static final ApsLogger logger = new ApsLogger(DetokenizationAdapter.class);

	@Value("${product_indicator}")
	private String product_indicator;

	@Value("${release_number}")
	private String release_number;

	@Value("${status}")
	private String status;

	@Value("${originator_code}")
	private String originator_code;

	@Value("${responder_code}")
	private String responder_code;

	@Value("${mti}")
	private String mti;

	@Value("${nets_tag_1}")
	private String nets_tag_1;

	@Value("${nets_tag_2}")
	private String nets_tag_2;

	@Value("${processing_code}")
	private String processing_code;

	@Value("${sub_length}")
	private String sub_length;

	@Value("${scheme_network_id}")
	private String scheme_network_id;

	@Value("${entry_mode}")
	private String entry_mode;

	@Value("${condition_code}")
	private String condition_code;

	@Value("${terminal_id}")
	private String terminal_id;

	@Value("${retailer_id}")
	private String retailer_id;

	@Value("${nets_bank_fiid}")
	private String nets_bank_fiid;

	@Value("${retailer_info}")
	private String retailer_info;

	@Value("${sub_id}")
	private String sub_id;
	
	@Autowired
	private DetokenizationService detokenizationService;

	public QrcGenerationRequest detokenizationQrcGenerationRequest(QrcGenerationRequest qrcGenerationRequest) throws BaseBusinessException {
		logger.info("Detokenization Adapter Started");
		Detokenization detokenizationRequest = getDetokenRequest(qrcGenerationRequest);
		logger.info("Detokenization object is: " + detokenizationRequest.log());
		String netsTokenId = detokenizationService.getTokenization(detokenizationRequest);
		
		qrcGenerationRequest.setSofAccountId(netsTokenId);

		logger.info("QrcGenerationRequest: " + qrcGenerationRequest.log());
		return qrcGenerationRequest;
	}

	private Detokenization getDetokenRequest(QrcGenerationRequest qrGenerationRequest) throws BaseBusinessException {

		notNull.test(qrGenerationRequest.getSofAccountId()).throwIfInvalid(BusinessValidationErrorCodes.INVALID_NETS_TOKEN_REF_ID);

		Detokenization request = new Detokenization();
		DetokenizationHeader header = new DetokenizationHeader();
		DetokenizationBody body = new DetokenizationBody();

		header.setMti(mti);
		header.setNets_tag_1(nets_tag_1);
		header.setOriginator_code(originator_code);
		header.setProduct_indicator(product_indicator);
		header.setRelease_number(release_number);
		header.setResponder_code(responder_code);
		header.setStatus(status);
		request.setHeader(header);
		body.setNets_tag_2(nets_tag_2);
		body.setProcessing_code(processing_code);
		LocalDateTime date = DateUtil.parseLocalDateTime(qrGenerationRequest.getTransmissionTime());	
		//body.setTrxn_amount(getAmountWithPadding(qrGenerationRequest.getTransactionDomainData().get);
		body.setStan(UtilComponents.getTraceNum());
		body.setTrxn_time(DateUtil.getDisplayTimeHHMMSS(date));
		body.setTrxn_date(DateUtil.getDisplayDateMMDD(date));
		body.setXmit_datetime(DateUtil.getDisplayDateMMDD(date)+DateUtil.getDisplayTimeHHMMSS(date));
		body.setEntry_mode(entry_mode);
		body.setCondition_code(condition_code);
		body.setRet_ref_num(getRefWithPadding(qrGenerationRequest.getRetrievalRef()));
		//body.setTerminal_id(apsRequest.getTid());
		body.setRetailer_id(retailer_id);
		body.setRetailer_info(retailer_info);
		ArrayList<DetokenizationSpecificData> txn_specific_datas= new ArrayList<DetokenizationSpecificData>();  
		DetokenizationSpecificData txn_specific_data= new DetokenizationSpecificData();  
		txn_specific_data.setSub_id(sub_id);
		txn_specific_data.setSub_length(sub_length);
		txn_specific_data.setNets_bank_fiid(nets_bank_fiid);
		txn_specific_data.setNets_token_id(addRightPadding(qrGenerationRequest.getSofAccountId()));
		txn_specific_data.setScheme_network_id(scheme_network_id);
		txn_specific_datas.add(txn_specific_data);
		body.setTxn_specific_data(txn_specific_datas);
		request.setBody(body);
		return request;
	}

	public String getRefWithPadding(String ref) {
		String amountStrWithLeftPad = String.format("%012d", Long.valueOf(ref));
		return amountStrWithLeftPad;
	}

	public static String addRightPadding(String tokenId) {
		return StringUtils.rightPad(tokenId, 20);
	}
}
