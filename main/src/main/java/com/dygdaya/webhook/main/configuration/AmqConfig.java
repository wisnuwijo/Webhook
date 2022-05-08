package com.dygdaya.webhook.main.configuration;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class AmqConfig {
    @Value("#{${spring.artemis.user}}")
    String amqUser;
    @Value("#{${spring.artemis.password}}")
    String amqPassword;
    @Value("#{${spring.artemis.broker-url}}")
    String amqbroker;
    @Value("#{${amq.connectioncache}}")
    Integer connectioncache;
    @Value("#{${amq.messagePerTask}}")
    Integer messagePerTask;
    @Value("#{${amq.concurrency}}")
    String concurrency;
    @Value("#{${amq.connectionTransacted}}")
    String connectionTransacted;

    @Bean
    public ConnectionFactory connectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(amqbroker);
        connectionFactory.setUser(amqUser);
        connectionFactory.setPassword(amqPassword);

        return connectionFactory;
    }

    @Bean
    public ConnectionFactory cachingConnectionFactory() throws JMSException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setTargetConnectionFactory(connectionFactory());
        connectionFactory.setSessionCacheSize(connectioncache);
        connectionFactory.setCacheProducers(true);
        connectionFactory.setReconnectOnException(true);
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrency(concurrency);
        factory.setSessionTransacted(Boolean.valueOf(connectionTransacted));
        factory.setMaxMessagesPerTask(messagePerTask);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(cachingConnectionFactory());
        template.setExplicitQosEnabled(true);
        return template;
    }
}
