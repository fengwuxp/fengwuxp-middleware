package com.wind.logging.logback.kafaka.delivery;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @since 0.0.1
 */
public class AsynchronousDeliveryStrategy implements DeliveryStrategy {

    @Override
    public <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> producerRecord, final E event,
                                  final FailedDeliveryCallback<E> failedDeliveryCallback) {
        try {
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception != null) {
                    failedDeliveryCallback.onFailedDelivery(event, exception);
                }
            });
            return true;
        } catch (Exception e) {
            failedDeliveryCallback.onFailedDelivery(event, e);
            return false;
        }
    }

}
