package com.nets.upos.commons.solace;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.nets.upos.commons.logger.ApsLogger;

@Component
public class SolaceMessageReceiver {

	private static final ApsLogger logger = new ApsLogger(SolaceMessageReceiver.class);

	@Value("${solace.tsp.to.processor.queue.name}")
	private String tspConsume;
	
	JmsTemplate jmsTemplate;

	public String receiveMessage(String correlationId) {

		logger.info("SolaceMessageReceiver trying to receive message correlation id:" + correlationId);
		logger.info("Receiving from queue: " + tspConsume);
		String resCorrelationId = "JMSCorrelationID = '" + correlationId + "'";
		logger.info("getJmsTemplate value        ::"+jmsTemplate);
		getJmsTemplate().setDefaultDestinationName(tspConsume);
		Message message = getJmsTemplate().receiveSelected(resCorrelationId);
		if (message instanceof TextMessage) {

			TextMessage txtMsg = (TextMessage) message;
			try {
				Object msgTextObj = txtMsg.getText();
				return msgTextObj.toString();
			} catch (JMSException e) {
				logger.error("SolaceMessageReceiver receiving message error");
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
}
