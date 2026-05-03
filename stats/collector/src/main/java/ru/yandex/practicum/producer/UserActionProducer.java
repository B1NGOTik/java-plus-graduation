package ru.yandex.practicum.producer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.config.KafkaClient;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProducer {
    private final KafkaClient kafkaClient;
    private Producer<Long, SpecificRecordBase> producer;

    public void sendUserAction(UserActionAvro userAction) {
        if (userAction == null) {
            log.warn("Попытка отправить null UserAction");
            return;
        }

        Producer<Long, SpecificRecordBase> kafkaProducer = getProducer();
        if (kafkaProducer == null) {
            log.error("Producer недоступен");
            return;
        }

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(
                kafkaClient.getTopicsProperties().getUserActions(),
                null,
                userAction.getTimestamp().toEpochMilli(),
                userAction.getUserId(),
                userAction
        );

        try {
            RecordMetadata metadata = kafkaProducer.send(record).get(30, TimeUnit.SECONDS);
            log.debug("Действие отправлено: userId={}, topic={}, offset={}",
                    userAction.getUserId(), metadata.topic(), metadata.offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Прервана отправка действия userId={}", userAction.getUserId(), e);
            throw new IllegalStateException("Отправка в Kafka прервана", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("Ошибка отправки действия userId={}: {}",
                    userAction.getUserId(), cause.getMessage(), cause);
            throw new IllegalStateException("Ошибка записи в Kafka", cause);
        } catch (TimeoutException e) {
            log.error("Таймаут отправки в Kafka для userId={}", userAction.getUserId(), e);
            throw new IllegalStateException("Таймаут записи в Kafka", e);
        }
    }

    @PreDestroy
    public void close() {
        if (producer != null) {
            try {
                producer.flush();
                producer.close(Duration.ofSeconds(30));
                log.info("Producer успешно закрыт");
            } catch (Exception e) {
                log.error("Ошибка при закрытии producer", e);
            } finally {
                producer = null;
            }
        }
    }

    private Producer<Long, SpecificRecordBase> getProducer() {
        if (producer == null) {
            producer = kafkaClient.getProducer();
        }
        return producer;
    }
}