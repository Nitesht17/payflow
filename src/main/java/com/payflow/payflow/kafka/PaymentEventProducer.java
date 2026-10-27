package com.payflow.payflow.kafka;


import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

/**
 * Publishes payment events to the Kafka 'payment-events' topic.
 * Called after a transaction status changes to SUCCESS or FAILED.
 */
@Component
public class PaymentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);
    private static final String TOPIC = "payment-events";

    private final KafkaTemplate<String , String> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String , String> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a payment event message to Kafka.
     *
     * @param message event details as a JSON string
     */

    public void publishPaymentEvent(String message)
    {
        log.info("Publishing payment event: {}", message);
        kafkaTemplate.send(TOPIC, message);
    }
}
