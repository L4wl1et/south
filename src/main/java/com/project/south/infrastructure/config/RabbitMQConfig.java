package com.project.south.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "beta.registration.exchange";

    public static final String QUEUE_REQUESTED = "beta.registration.requested";
    public static final String QUEUE_CANCELLED = "beta.registration.cancelled";

    public static final String ROUTING_KEY_REQUESTED = "registration.requested";
    public static final String ROUTING_KEY_CANCELLED = "registration.cancelled";

    @Bean
    public TopicExchange registrationExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue registrationRequestedQueue() {
        return QueueBuilder.durable(QUEUE_REQUESTED).build();
    }

    @Bean
    public Queue registrationCancelledQueue() {
        return QueueBuilder.durable(QUEUE_CANCELLED).build();
    }

    @Bean
    public Binding bindingRequested() {
        return BindingBuilder
                .bind(registrationRequestedQueue())
                .to(registrationExchange())
                .with(ROUTING_KEY_REQUESTED);
    }

    @Bean
    public Binding bindingCancelled() {
        return BindingBuilder
                .bind(registrationCancelledQueue())
                .to(registrationExchange())
                .with(ROUTING_KEY_CANCELLED);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setDefaultRequeueRejected(false);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}