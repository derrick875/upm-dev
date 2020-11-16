package com.nets.upos.commons.solace;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.nets.upos.commons.logger.ApsLogger;

@Component
public class UpiProxyReceiver {
	
	private static final ApsLogger logger = new ApsLogger(UpiProxyReceiver.class);
	
	@Value("${solace.upiproxy.to.qrgeneration.queue.name}")
	private String upiProxyConsume;

	JmsTemplate jmsTemplate;
	
	public String receiveMessage() {
		logger.info("UpiProxyReceiver trying to receive message from queue: " + upiProxyConsume);
		getJmsTemplate().setDefaultDestinationName(upiProxyConsume);
		Message message = getJmsTemplate().receive();
		if (message instanceof TextMessage) {

			TextMessage txtMsg = (TextMessage) message;
			try {
				Object msgTextObj = txtMsg.getText();
				logger.info("msgTextObj         :: "+msgTextObj.toString());
				return msgTextObj.toString();
			} catch (JMSException e) {
				logger.error("UpiProxyReceiver receiving message error");
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
