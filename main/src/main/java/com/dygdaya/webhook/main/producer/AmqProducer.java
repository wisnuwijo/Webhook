package com.dygdaya.webhook.main.producer;

import com.dygdaya.webhook.main.controller.WebhookController;
import com.dygdaya.webhook.main.model.WebhookPojo;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class AmqProducer {

    @Autowired
    JmsTemplate jmsTemplate;

    Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Value("#{${webhook.dst-queue}}")
    String dstQueue;

    public void sendMessage(WebhookPojo webhookPojo) {
        try {
            logger.debug("Sending incoming webhook to queue");
            jmsTemplate.convertAndSend(dstQueue, new Gson().toJson(webhookPojo));
            logger.debug("Success put webhook message to queue");
        } catch (Exception e) {
            logger.error("Failed to send incoming webhook to queue. Got exception : " + e.getMessage().toString());
        }
    }
}
