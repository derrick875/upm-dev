package com.nets.nps.upi.service;

import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.nets.nps.paynow.utils.UtilComponents;

@Service
public class WalletExchangeService {
	private Logger logger = LoggerFactory.getLogger(WalletExchangeService.class);
	
	@Value("${bank.service.base.url}")
	private String walletUrl;

	@Value("#{${nets.wallet.geturl.instId.map}}")
	private Map<String, String> bankInstIdUrlMap;
	
	@Value("#{${nets.upi.appgateway.instId.map}}")
	private Map<String, String> instIdUrlMap;

	@Autowired
	@Qualifier("oneWay")
	private RestTemplate restTemplate;

	public String getWalletResponse(String req, String bankInstitutionCode) {
		logger.info("getWalletResponse bankInstitutionCode :  " + bankInstitutionCode );
		String insID = UtilComponents.getKey(instIdUrlMap, bankInstitutionCode) ;
		
//		String insID = bankInstIdUrlMap.get(bankInstitutionCode) ;
		// url : "/scis/switch/cvmcheckresponse" //combine both to one string
//		String addCvmUrl = "";
//		if (instIdUrlMap.get(insID) != null) {
//			addCvmUrl = instIdUrlMap.get(insID);
//			logger.info("addCvmUrl :  " + addCvmUrl);
//		}
		logger.info("getWalletResponse insID :  " + insID );
		
		String addCvmUrl = bankInstIdUrlMap.get(insID);
		logger.info("addCvmUrl :  " + addCvmUrl);
		
		String getCvmUrl = walletUrl + addCvmUrl;
		logger.info("WalletExchangeService getWalletResponse method start");
		logger.info("getCvmUrl :  " + getCvmUrl);
		logger.info("Cvm Check req :  " + req);
		ResponseEntity<String> walletCvmResponseEntity;

		try {
			HttpEntity<String> requestEntity = new HttpEntity<String>(req, generateHeaders());
			logger.info("requestEntity  :  " + requestEntity);

			walletCvmResponseEntity = restTemplate.exchange(getCvmUrl, HttpMethod.POST, requestEntity, String.class);
//			walletCvmResponseEntity= restTemplate.exchange("https://localhost:8443/scis/switch/cvmcheckresponse", HttpMethod.POST, requestEntity, String.class);
			logger.info("walletCvmResponse: " + walletCvmResponseEntity);
			logger.info("walletCvmResponse: " + walletCvmResponseEntity.getBody()
					+ walletCvmResponseEntity.getStatusCode());

			return walletCvmResponseEntity.getBody();
		} catch (RestClientResponseException ex) {
			// TODO time out , return back to the wallet....
			throw new RuntimeException(ex);
		}
	}

	private HttpHeaders generateHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		logger.info("headers string: " + headers.toString());
		return headers;
	}

}
