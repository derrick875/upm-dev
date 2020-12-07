package com.nets.upos.commons.solace;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.UpiQrcGenerationRequest;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class UpiProxySender {

	private static final ApsLogger logger = new ApsLogger(UpiProxySender.class);
	
	@Value("${solace.processor.to.upiproxy.topic.name}")
	String upiProxyTopic;

	@Autowired
	JmsTemplate jmsTemplate;
	
	public void put(UpiQrcGenerationRequest upiQrGenReq) {
		logger.info("Sending to topic: " + upiProxyTopic);
		logger.info("Sending upiQrGenerationRequest: " + upiQrGenReq.log());
		String upiQrGenReqString = UtilComponents.getStringFromObject(upiQrGenReq);
		getJmsTemplate().send(upiProxyTopic, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException{
				Message message = session.createTextMessage(upiQrGenReqString);
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
