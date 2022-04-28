package com.dygdaya.webhook.main.repository;

import java.util.List;
import java.util.Optional;

import com.dygdaya.webhook.main.model.WebhookPojo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebhookRepository extends MongoRepository<WebhookPojo, String> {

    public List<WebhookPojo> findAll();
    public Optional<WebhookPojo> findById(String id);
    
}
