package com.waaw.customer.conf;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, ObservationRegistry observationRegistry) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setObservationEnabled(true); // Enables both metrics and tracing
        return rabbitTemplate;
    }

    // For listener containers
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
            ObservationRegistry observationRegistry) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setObservationEnabled(true);
        return factory;
    }

}