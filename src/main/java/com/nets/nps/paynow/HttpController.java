package com.nets.nps.paynow;

import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Message;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class HttpController {
	
	private static final ApsLogger logger = new ApsLogger(HttpController.class);

	public static final String HEADER_NETS_MTI = "NETS-MTI";
	public static final String HEADER_NETS_PROCESSCODE = "NETS-ProcessCode";

	@Autowired
	Router router;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
			     produces = MediaType.APPLICATION_JSON_VALUE)
	private Response process(@RequestHeader(value = HEADER_NETS_MTI) String mti,
			                  @RequestHeader(value = HEADER_NETS_PROCESSCODE) String processCode,
			                  @RequestBody Request request) {
		logger.info(request);
		Response response = router.route(mti, processCode, request);
		logger.info(response);
		return response;
	}

	
}
