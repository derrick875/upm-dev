package com.nets.nps.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.nets.upos.commons.logger.ApsLogger;

@Service
public class NonStopClient {
	
	private static final ApsLogger logger = new ApsLogger(NonStopClient.class);
	
	@Value("${solace.paynow.txnstate.topic.name}") 
	String txnStateTopic;
	
	@Autowired
	JmsTemplate jmsTemplate;

	public void put(TransactionStateMsg message) throws JmsException {
		logger.info("NonStopClient put transaction state msg: " + message);
		String topic = txnStateTopic.concat(message.getTopicSuffix());
		logger.info("Sending to topic: " + topic);
		
		jmsTemplate.convertAndSend(topic, message);
	}
}
