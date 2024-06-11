package com.mspr.clients.config.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CLIENTS_EXCHANGE = "clients.exchange";
    public static final String CLIENTS_QUEUE = "clients.queue";
    public static final String CLIENTS_DEAD_QUEUE = "clients.dead.queue";

    @Bean
    public TopicExchange clientsExchange() {
        return ExchangeBuilder.topicExchange(CLIENTS_EXCHANGE).build();
    }

    @Bean
    public Queue clientsQueue() {
        return QueueBuilder.durable(CLIENTS_QUEUE).build();
    }

    @Bean
    public Queue clientsDeadQueue() {
        return QueueBuilder.durable(CLIENTS_DEAD_QUEUE).build();
    }

    @Bean
    public Binding binding(Queue clientsQueue, TopicExchange clientsExchange) {
        return BindingBuilder.bind(clientsQueue).to(clientsExchange).with(CLIENTS_QUEUE);
    }
    
}
