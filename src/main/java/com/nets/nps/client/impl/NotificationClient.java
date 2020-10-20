package com.nets.nps.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.nets.upos.commons.entity.JsonNotification;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class NotificationClient {
	private static final ApsLogger logger = new ApsLogger(NotificationClient.class);

	@Value("${solace.paynow.notification.topic.name}")
	String notificationTopic;

	@Autowired
	JmsTemplate jmsTemplate;

	public void put(JsonNotification message) throws JmsException {
		logger.info("Sending to topic: " + notificationTopic);
		jmsTemplate.convertAndSend(notificationTopic, message);
	}

}
