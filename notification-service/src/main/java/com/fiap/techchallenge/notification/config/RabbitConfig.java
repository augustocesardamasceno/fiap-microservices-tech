package com.fiap.techchallenge.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String APPOINTMENTS_EXCHANGE = "appointments.events";
    public static final String APPOINTMENTS_NOTIFICATIONS_QUEUE = "appointments.notifications.queue";
    public static final String APPOINTMENTS_ROUTING_KEY = "appointments.notifications";

    @Bean
    public TopicExchange appointmentsExchange() {
        return new TopicExchange(APPOINTMENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationsQueue() {
        return new Queue(APPOINTMENTS_NOTIFICATIONS_QUEUE, true);
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, TopicExchange appointmentsExchange) {
        return BindingBuilder.bind(notificationsQueue).to(appointmentsExchange).with(APPOINTMENTS_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
