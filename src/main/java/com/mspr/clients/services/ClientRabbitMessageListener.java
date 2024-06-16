package com.mspr.clients.services;

import com.mspr.clients.config.messaging.RabbitMQConfig;
import com.mspr.clients.models.entities.ClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClientRabbitMessageListener {
//    static final Logger logger = LoggerFactory.getLogger(ClientRabbitMessageListener.class);
//
//    @RabbitListener(queues = RabbitMQConfig.CLIENTS_QUEUE)
//    public void receiveMessage(ClientMessage clientMessage) {
//        logger.info("Received client message: " + clientMessage);
//    }
}
