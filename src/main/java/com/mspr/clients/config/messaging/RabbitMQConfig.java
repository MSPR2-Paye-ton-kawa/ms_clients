package com.mspr.clients.config.messaging;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {
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

    //    To send rabbit message as JSON
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //   To receive rabbit message as JSON
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }
    
}
