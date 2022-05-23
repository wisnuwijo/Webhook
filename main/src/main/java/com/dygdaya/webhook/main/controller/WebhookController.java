package com.dygdaya.webhook.main.controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.dygdaya.webhook.main.model.WebhookPojo;

import com.dygdaya.webhook.main.producer.AmqProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@Component
public class WebhookController {

    private MongoTemplate mongoTemplate;
    Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    AmqProducer producer;

    // webhook token
    @Value("${webhook.token}")
    private String WEBHOOK_TOKEN;

    public WebhookController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Value("spring.data.mongodb.database")
    private String databaseName;

    @RequestMapping(value = "/webhook", method = {RequestMethod.POST, RequestMethod.GET})
    public String webhook(@RequestBody(required = false) String request, @RequestParam(required = false) Map<String,String> params) {
        UUID transactionId = UUID.randomUUID();

        // [start] process Facebook webhook verification
        if (request != null) logger.info(transactionId.toString() + " - Received requestbody : " + request.toString());
        if (params != null) logger.info(transactionId.toString() + " - Received requestparam : " + params.toString());
        if (request == null && params == null) return "Bad request";

        String mode = "";
        String token = "";
        String challenge = "";

        if (params.containsKey("hub.mode")) mode = (String) params.get("hub.mode");
        if (params.containsKey("hub.verify_token")) token = (String) params.get("hub.verify_token");
        if (params.containsKey("hub.challenge")) challenge = (String) params.get("hub.challenge");

        if (!mode.isEmpty() && !token.isEmpty()) {
            logger.info(transactionId.toString() + " - received Facebook webhook verification request");
            logger.info(transactionId.toString() + " - received token : " + token);
            logger.info(transactionId.toString() + " - our token : " + WEBHOOK_TOKEN);
            if (mode.equals("subscribe") && token.equals(WEBHOOK_TOKEN)) {
                logger.info(transactionId.toString() + " - Facebook webhook challenge value : " + challenge);
                return challenge;
            }
        }
        // [end] process Facebook webhook verification

        try {
            logger.info(transactionId.toString() + " - inserting document to mongodb ...");
            mongoTemplate.insert(new WebhookPojo(transactionId.toString(), new Date(), request.toString().replace(" ","")));
            logger.info(transactionId.toString() + " - mongodb insert succeeded");

            producer.sendMessage(new WebhookPojo(transactionId.toString(), new Date(), request.toString().replace(" ","")));

            return "Success";
        } catch (Exception e) {
            if (e.getMessage() != null) {
                logger.error(transactionId.toString() + " - WebhookController failed, exception : " + e.getMessage().toString());
            } else {
                logger.error(transactionId.toString() + " - WebhookController failed, exception : " + e.toString());
            }

            return "Failed";
        }
    }
    
}
