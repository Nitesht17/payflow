package com.payflow.payflow.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the 'payment-events' Kafka topic and processes payment events.
 * In a production system this would trigger emails, webhooks, audit logs, etc.
 * Currently logs the event to demonstrate async processing.
 */
@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);

    /**
     * Automatically triggered when a new message arrives on 'payment-events' topic.
     *
     * @param message the event payload received from Kafka
     */
    @KafkaListener(topics = "payment-events" , groupId = "payflow-group")
    public void handelPaymentEvent(String message)
    {
        log.info("Payment event received: {}", message);
        // Future: send email, call webhook, update analytics
    }
}
