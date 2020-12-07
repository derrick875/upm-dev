package com.nets.upos.commons.solace;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.nets.upos.commons.logger.ApsLogger;

@Component
public class SolaceMessageSender {

	private static final ApsLogger logger = new ApsLogger(SolaceMessageSender.class);

	@Value("${solace.tsp.to.processor.topic.name}")
	private String replyToTopic;

	@Value("${solace.processor.to.tsp.topic.name}")
	private String destination;

	@Autowired
	JmsTemplate jmsTemplate;

	public void put(String detokenization, String correlationId) throws JmsException {
		logger.info("SolaceMessageSender is sending message with correlationId: "+ correlationId);
		logger.info("Sending to topic: " + destination);
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(detokenization);
				message.setJMSCorrelationID(correlationId);
				Topic jmsReplyDestination = session.createTopic(replyToTopic);
				message.setJMSReplyTo(jmsReplyDestination);
				
				return message;
			}
		});
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
}
