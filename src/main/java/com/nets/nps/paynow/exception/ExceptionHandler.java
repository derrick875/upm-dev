package com.nets.nps.paynow.exception;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.constants.ResponseCodes;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.core.entity.ErrorEventEntity;
import com.nets.upos.core.service.ErrorEventService;

@Component
public class ExceptionHandler {

	public static final String ERROR_MSG = "Error";

	public static final String JSON_FORMAT_EXCEPTION = "JsonFormatException";
	
	public static final String BASE_BUSINESS_EXCEPTION = "BaseBusinessException";
	
	public static final String GENERIC_EXCEPTION = "GenericException";

	private static final ApsLogger logger = new ApsLogger(ExceptionHandler.class);
	
	@Autowired
	private ErrorEventService errorEventService;
	
	@Value("${module}")
	private String module;
	
	@Value("${exception.message.size}")
	private int exceptionMessageSize;

	public Response handleJsonFormatException(Request request, JsonFormatException formatEx) {
		logger.info(request);
		logger.error("Request format error: ", formatEx);
		Response response = request.createResponse();
		response.setResponseCode(formatEx.getCode());
		response.setResponseMsg(formatEx.getErrorMessage());
		logger.info(response);

		String requestType = MtiRequestMapper.find(request.getMti(), request.getProcessCode()).get().getRequestType();
		createErrorEvent(response, requestType, JSON_FORMAT_EXCEPTION);

		return response;
	}

	public Response handleBusinessException(Request request, BaseBusinessException businessEx) {
		logger.info(request);
		logger.error("Business error: ", businessEx);
		Response response = request.createResponse();
		response.setResponseCode(businessEx.getCode());
		response.setResponseMsg(businessEx.getErrorMessage());
		logger.info(response);
		
		String requestType = MtiRequestMapper.find(request.getMti(), request.getProcessCode()).get().getRequestType();
		createErrorEvent(response, requestType, BASE_BUSINESS_EXCEPTION);
		
		return response;
	}

	public Response handleGenericException(Request request, Exception ex) {
		logger.info(request);
		logger.error("Unexpected error: ", ex);
		Response response = request.createResponse();
		response.setResponseCode(ResponseCodes.ERROR);
		response.setResponseMsg(Optional.ofNullable(ex.getMessage()).orElse(ERROR_MSG));
		logger.info(response);
		
		String requestType = MtiRequestMapper.find(request.getMti(), request.getProcessCode()).get().getRequestType();
		createErrorEvent(response, requestType, GENERIC_EXCEPTION, ex.getMessage());
		
		return response;
	}

	private void createErrorEvent(Response response, String requestType, String errorType) {
		createErrorEvent(response, requestType, errorType, null);
	}
	
	private void createErrorEvent(Response response, String requestType, String errorType, String exceptionMsg) {
		ErrorEventEntity errorEvent = new ErrorEventEntity();
		String hostname = null;
		String transactionRef = null;
		
		errorEvent.setModule(module);
		errorEvent.setErrorType(errorType);
		
		try {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				hostname = System.getenv("COMPUTERNAME");
			} else if (os.contains("nix") || os.contains("nux")) {
				hostname = System.getenv("HOSTNAME");
			}
		} catch (Exception e) {
			logger.error("Exception while retrieving hostname:" + e.getMessage());
		}
		errorEvent.setHostname(hostname);

		if (response != null) {
			errorEvent.setMti(response.getMti());
			errorEvent.setProcessCode(response.getProcessCode());
			
			if(response.getRetrievalRef() != null) {
				transactionRef = response.getRetrievalRef();
			}
			
			errorEvent.setTransactionRef(transactionRef);

			if (response != null) {
				errorEvent.setErrorCode(response.getResponseCode());
				errorEvent.setErrorMessage(response.getResponseMsg());

				if (exceptionMsg != null) {
					if (exceptionMsg.length() > exceptionMessageSize) {
						errorEvent.setExceptionMessage(StringUtils.abbreviate(exceptionMsg, exceptionMessageSize));
					} else {
						errorEvent.setExceptionMessage(exceptionMsg);
					}
				}
			}
			
			errorEventService.save(errorEvent);
			
		}
	}

}
