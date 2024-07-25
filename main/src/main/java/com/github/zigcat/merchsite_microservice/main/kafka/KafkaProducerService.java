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
    private final ReplyingKafkaTemplate<String, String, String> authTemplate;
    private final ReplyingKafkaTemplate<String, String, String> loginTemplate;

    @Autowired
    public KafkaProducerService(ReplyingKafkaTemplate<String, String, String> authTemplate,
                                ReplyingKafkaTemplate<String, String, String> loginTemplate) {
        this.authTemplate = authTemplate;
        this.loginTemplate = loginTemplate;
    }

    public String sendUserForLogin(String userJson) throws ExecutionException, InterruptedException {
        String LOGIN_REQUEST_TOPIC = "login-request";
        String LOGIN_REPLY_TOPIC = "login-reply";
        ProducerRecord<String, String> record = new ProducerRecord<>(LOGIN_REQUEST_TOPIC, userJson);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, LOGIN_REPLY_TOPIC.getBytes()));
        RequestReplyFuture<String, String, String> replyFuture = loginTemplate.sendAndReceive(record);
        ConsumerRecord<String, String> consumerRecord = replyFuture.get();
        return consumerRecord.value();
    }

    public String sendUserForAuth(String tokenJson) throws ExecutionException, InterruptedException {
        String AUTH_REQUEST_TOPIC = "auth-request";
        String AUTH_REPLY_TOPIC = "auth-reply";
        ProducerRecord<String, String> record = new ProducerRecord<>(AUTH_REQUEST_TOPIC, tokenJson);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, AUTH_REPLY_TOPIC.getBytes()));
        RequestReplyFuture<String, String, String> replyFuture = authTemplate.sendAndReceive(record);
        ConsumerRecord<String, String> consumerRecord = replyFuture.get();
        return consumerRecord.value();
    }
}
