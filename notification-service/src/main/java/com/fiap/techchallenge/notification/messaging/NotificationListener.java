package com.fiap.techchallenge.notification.messaging;

import com.fiap.techchallenge.notification.config.RabbitConfig;
import com.fiap.techchallenge.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitConfig.APPOINTMENTS_NOTIFICATIONS_QUEUE)
    public void receive(@Payload AppointmentNotificationMessage message) {
        LOGGER.info("Received appointment event {} for patient {}", message.eventType(), message.patientId());
        notificationService.processAppointmentEvent(message);
    }
}
