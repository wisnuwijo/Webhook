package com.dygdaya.webhook.main.controller;

import java.util.Date;

import com.dygdaya.webhook.main.model.WebhookPojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class WebhookController {

    private MongoTemplate mongoTemplate;
    Logger logger = LoggerFactory.getLogger(WebhookController.class);

    public WebhookController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Value("spring.data.mongodb.database")
    private String databaseName;

    @RequestMapping(value = "/webhook", method = RequestMethod.GET)
    public String webhook(@RequestBody String request) {
        logger.info("Received request : " + request.toString());
        try {
            logger.info("inserting document ...");
            mongoTemplate.insert(new WebhookPojo(new Date(), "Holaaa"));
            logger.warn("insert succeeded");
            
            return "Success";
        } catch (Exception e) {
            logger.error("this is a error message");

            return "Failed";
        }
    }
    
}
