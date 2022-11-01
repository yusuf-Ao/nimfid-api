package com.nimfid.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.user-notification}")
    private String userNotificationQueue;

    @Value("${rabbitmq.routing-keys.internal-user-notification}")
    private String internalUserNotificationRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue userNotificationQueue() {
        return new Queue(this.userNotificationQueue);
    }

    @Bean
    public Binding internalToUserNotificationBinding() {
        return BindingBuilder
                .bind(userNotificationQueue())
                .to(internalTopicExchange())
                .with(this.internalUserNotificationRoutingKey);
    }

    public String getInternalExchange() {
        return internalExchange;
    }

    public String getUserNotificationQueue() {
        return userNotificationQueue;
    }

    public String getInternalUserNotificationRoutingKey() {
        return internalUserNotificationRoutingKey;
    }

}
