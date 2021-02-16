package com.nets.upos.commons.solace;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingResultRequest;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class NpsProxySender {
	
	private static final ApsLogger logger = new ApsLogger(NpsProxySender.class);


	@Value("${solace.addproresult.to.upiproxy.topic.name}")
	String addProTopic;
	
	JmsTemplate jmsTemplate;

	public void putAddRequest(AdditionalProcessingResultRequest addReq) {
		logger.info("NpsProxySender Sending to topic: " + addProTopic);
		logger.info("NpsProxySender Sending AdditionalProcessingResultRequest : " + addReq.toString() );
		String addReqString = UtilComponents.getStringFromObject(addReq);
		getJmsTemplate().send(addProTopic, new MessageCreator() { //send the string upiQrGenReqString in the message
			public Message createMessage(Session session) throws JMSException{
				Message message = session.createTextMessage(addReqString);
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
