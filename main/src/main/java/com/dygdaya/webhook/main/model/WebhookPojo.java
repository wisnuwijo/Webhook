package com.dygdaya.webhook.main.model;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("webhook")
public class WebhookPojo {

    @Id
    public String id;
    public Date created_at;
    public String data;

    @Autowired
    public WebhookPojo(Date created_at, String data) {
        this.created_at = created_at;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format(
                "id[id=%s, created_at='%s', data='%s']",
                id, created_at, data);
    }
    
}
