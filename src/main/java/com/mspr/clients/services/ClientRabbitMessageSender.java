package com.mspr.clients.services;

import com.mspr.clients.config.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientRabbitMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessageInClientQueue(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.CLIENTS_QUEUE, message);
    }
}
