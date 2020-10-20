package com.nets.nps.paynow;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class JmsController {

    private static final ApsLogger logger = new ApsLogger(JmsController.class);

	/*
	 * @Value("${solace.apigw.response.topic.name}") private String
	 * apiGWResponseTopic;
	 */
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Router router;

    @JmsListener(destination = "${solace.apigw.request.queue.name}")
    public void onRequest(Message message) {
        if (message instanceof TextMessage) {
            long start = System.currentTimeMillis();
            try {
                // FIXME choose proper corelation id
                String correlationID = message.getJMSCorrelationID();
                Destination replyTo = message.getJMSReplyTo();
                String text = ((TextMessage) message).getText();
                logger.info("Recieved message: " + text +
                        " with correlation id: " + correlationID);

                ObjectMapper jacksonMapper = new ObjectMapper();
                Request request = jacksonMapper.readValue(text, Request.class);

                logger.info("Request: " + request);
                Response response = router.route(request);
                logger.info("Response: " + response);

                jmsTemplate.convertAndSend(replyTo, response, postProcessorMessage -> {
                    postProcessorMessage.setJMSCorrelationID(correlationID);
                    return postProcessorMessage;
                });
            } catch (Exception e) {
                logger.error("Error to get request message from API Gateway due ta bad(unparsable) data", e);
                // TODO sent Bad Request to API Gateway
            }
            logger.info("Execution Time: " + (System.currentTimeMillis() - start));
        } else {
            logger.error("Invalid message encoding not Text Message.");
            // TODO send Bad Request to API Gateway
        }
    }
}