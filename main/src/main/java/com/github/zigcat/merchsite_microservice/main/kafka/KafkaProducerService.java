package com.github.zigcat.merchsite_microservice.main.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class KafkaProducerService {
    private final String AUTH_REQUEST_TOPIC = "auth-request";
    private final String AUTH_REPLY_TOPIC = "auth-reply";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public String sendUserForAuth(String userJson) throws ExecutionException, InterruptedException {
        ProducerRecord<String, String> record = new ProducerRecord<>(AUTH_REQUEST_TOPIC, userJson);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, AUTH_REPLY_TOPIC.getBytes()));
        RequestReplyFuture<String, String, String> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, String> consumerRecord = replyFuture.get();
        return consumerRecord.value();
    }
}
