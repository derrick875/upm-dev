package com.nets.upos.commons.solace;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.nets.upos.commons.logger.ApsLogger;

@Component
public class NpsProxyReceiver {
	
	private static final ApsLogger logger = new ApsLogger(NpsProxyReceiver.class);

	@Value("${solace.upiproxy.to.addproresult.queue.name}")
	private String addProConsume;

	JmsTemplate jmsTemplate;
	
	public String receiveAddProResponse() {
		logger.info("NpsProxyReceiver trying to receive message from queue: " + addProConsume);
 		getJmsTemplate().setDefaultDestinationName(addProConsume);
		Message message = getJmsTemplate().receive();
		
		if (message instanceof TextMessage) {
			TextMessage txtMsg = (TextMessage) message;
			try {
				Object msgTextObj = txtMsg.getText();
				logger.info("msgTextObj         :: "+msgTextObj.toString());
				return msgTextObj.toString();
			} catch (JMSException e) {
				logger.error("NpsProxyReceiver receiving message error");
				e.printStackTrace();
			}
		} else {
			logger.info("Else message is NOT a instance of TextMessage " );
			TextMessage txtMsg = (TextMessage) message;
			try {
				Object msgTextObj = txtMsg.getText();
				logger.info("msgTextObj         :: "+msgTextObj.toString());
				return msgTextObj.toString();
			} catch (JMSException e) {
				logger.error("NpsProxyReceiver receiving message error");
				e.printStackTrace();
			}		}
		return null;
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
}
