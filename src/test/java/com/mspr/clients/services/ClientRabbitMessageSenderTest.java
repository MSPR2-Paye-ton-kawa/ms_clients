package com.mspr.clients.services;

import com.mspr.clients.config.messaging.RabbitMQConfig;
import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.ClientMessage;
import com.mspr.clients.models.entities.Company;
import com.mspr.clients.models.enums.RabbitMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRabbitMessageSenderTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ClientRabbitMessageSender clientRabbitMessageSender;

    private Client client;
    private ClientMessage clientMessage;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "username1", "firstname1", "lastname1", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street1", "postalCode1", "city1"), new Company("company1", "email1", "phone1"));
        clientMessage = new ClientMessage(RabbitMessageType.CREATED, client);
    }

    @Test
    void sendMessageInClientQueue_Should_SendMessage() {
        // Given
        RabbitMessageType messageType = RabbitMessageType.CREATED;

        // When
        clientRabbitMessageSender.sendMessageInClientQueue(messageType, client);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(RabbitMQConfig.CLIENTS_QUEUE, clientMessage);
    }
}
