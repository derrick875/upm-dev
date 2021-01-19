package com.nets.nps.upi.service;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;

import java.io.IOException;
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
import com.nets.nps.upi.handlers.TspCommunicationHandler;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.utils.DateUtil;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Service
public class DetokenizationService {

	private static final ApsLogger logger = new ApsLogger(DetokenizationService.class);

	@Autowired
	private TspCommunicationHandler tspCommunicationHandler;

	public String getTokenization(Detokenization detokenizationRequest) throws BaseBusinessException {
		logger.info("Detokenization Service Started");
		String responseString = "";
		try {
			responseString = tspCommunicationHandler.sendAndReceive(detokenizationRequest);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseBusinessException(BusinessValidationErrorCodes.ISSUER_UNAVAILABLE);
		}
		notNull.test(responseString).throwIfInvalid(BusinessValidationErrorCodes.INVALID_NETS_TOKEN_REF_ID_RESPONSE);
		Detokenization token = (Detokenization) UtilComponents.getObjectFromString(responseString,
				Detokenization.class);
		notNull.test(token).throwIfInvalid(BusinessValidationErrorCodes.INVALID_NETS_TOKEN_REF_ID_RESPONSE);
		logger.info("Detokenization Object is: " + token.log());
		ArrayList<DetokenizationSpecificData> detokenizationSpecificDatas = token.getBody().getTxn_specific_data();
		String netsTokenId=null;
		String vc_pan_status="";
		if(detokenizationSpecificDatas!=null&&detokenizationSpecificDatas.size()>0) {
			DetokenizationSpecificData detokenizationSpecificData=detokenizationSpecificDatas.get(0);
			netsTokenId = detokenizationSpecificData.getNets_token_id();
			vc_pan_status = detokenizationSpecificData.getStatus();
		}
		if(!"A".equalsIgnoreCase(vc_pan_status)) {
			throw new BaseBusinessException(BusinessValidationErrorCodes.INACTIVE_UPI_TOKEN);	
		}
		notNull.test(netsTokenId).throwIfInvalid(BusinessValidationErrorCodes.INVALID_NETS_TOKEN_REF_ID_RESPONSE);

		return netsTokenId;
	}
}
