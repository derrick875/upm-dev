package com.nets.nps.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.nets.upos.commons.entity.APSRequestWrapper;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class FraudwallClient {
	private static final ApsLogger logger = new ApsLogger(FraudwallClient.class);
	
	@Value("${solace.paynow.fraudwall.topic.name}")
	String fraudwalTopic;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	public void put(APSRequestWrapper wrapper) throws JmsException {
		logger.info("Sending to topic: " + fraudwalTopic);
		logger.info("Sending fraudwall msg: " + wrapper.log());
		jmsTemplate.convertAndSend(fraudwalTopic, wrapper);
	}
}
