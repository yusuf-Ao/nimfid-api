package com.nimfid.commons.amqp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@AllArgsConstructor
public class RabbitMQMessageProducer {

    @Autowired
    private final AmqpTemplate amqpTemplate;

    //publish message in queue to notification service for distribution and consumption
    public void publish(final Object payload, final String exchange, final String routingKey) {
        log.info("Publishing to {} using routingKey {}. Payload: {}",
                exchange, routingKey, payload);
        amqpTemplate.convertAndSend(exchange, routingKey, payload);
        log.info("Published to {} using routingKey {}. Payload: {}",
                exchange, routingKey, payload);
    }
}
