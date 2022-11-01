package com.nimfid.notificationservice.rabbitmq;


import com.nimfid.commons.request.UserNotificationRequest;
import com.nimfid.notificationservice.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queues.user-notification}")
    public void consumer(final UserNotificationRequest userNotificationRequest) {
        log.info("Consumed {} from queue", userNotificationRequest);
        NotificationService.send(userNotificationRequest);

    }
}
