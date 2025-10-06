package com.fiap.techchallenge.scheduling.service;

import static com.fiap.techchallenge.scheduling.config.RabbitConfig.APPOINTMENTS_ROUTING_KEY;

import com.fiap.techchallenge.scheduling.messaging.AppointmentNotificationPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange appointmentsExchange;

    public NotificationPublisher(RabbitTemplate rabbitTemplate, TopicExchange appointmentsExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.appointmentsExchange = appointmentsExchange;
    }

    public void publish(AppointmentNotificationPayload payload) {
        try {
            rabbitTemplate.convertAndSend(appointmentsExchange.getName(), APPOINTMENTS_ROUTING_KEY, payload);
            LOGGER.info("Published appointment notification event for appointment {}", payload.appointmentId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish appointment notification", e);
        }
    }
}
