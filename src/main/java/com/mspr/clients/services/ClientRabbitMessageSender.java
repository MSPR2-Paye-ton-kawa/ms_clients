package com.mspr.clients.services;

import com.mspr.clients.config.messaging.RabbitMQConfig;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.ClientMessage;
import com.mspr.clients.models.enums.RabbitMessageType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientRabbitMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessageInClientQueue(RabbitMessageType messageType, Client client) {

        rabbitTemplate.convertAndSend(RabbitMQConfig.CLIENTS_QUEUE, new ClientMessage(messageType, client));
    }
}
