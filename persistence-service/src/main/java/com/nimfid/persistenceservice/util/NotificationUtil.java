package com.nimfid.persistenceservice.util;


import com.nimfid.commons.amqp.RabbitMQMessageProducer;
import com.nimfid.commons.request.UserNotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class NotificationUtil {

    @Autowired
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void publishNotificationToQueue(final UserNotificationRequest userNotificationRequest) {
        rabbitMQMessageProducer.publish(
                userNotificationRequest,
                "internal.exchange",
                "internal.user.notification.routing-key"
        );
    }
}

