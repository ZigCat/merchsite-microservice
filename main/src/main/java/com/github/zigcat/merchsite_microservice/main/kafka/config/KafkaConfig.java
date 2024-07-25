package com.github.zigcat.merchsite_microservice.main.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVER;

    @Bean
    public ProducerFactory<String, String> producerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "merchsite");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
            ProducerFactory<String, String> producerFactory
    ){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> containerFactory(
            ConsumerFactory<String, String> consumerFactory
    ){
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> loginReplyContainer(
            ConsumerFactory<String, String> consumerFactory
    ){
        ContainerProperties properties =
                new ContainerProperties("login-reply");
        return new KafkaMessageListenerContainer<>(consumerFactory, properties);
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> authReplyContainer(
            ConsumerFactory<String, String> consumerFactory
    ){
        ContainerProperties properties =
                new ContainerProperties("auth-reply");
        return new KafkaMessageListenerContainer<>(consumerFactory, properties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> loginTemplate(
        ProducerFactory<String, String> producerFactory,
        KafkaMessageListenerContainer<String, String> loginReplyContainer
    ){
        return new ReplyingKafkaTemplate<>(producerFactory, loginReplyContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> authTemplate(
            ProducerFactory<String, String> producerFactory,
            KafkaMessageListenerContainer<String, String> authReplyContainer
    ){
        return new ReplyingKafkaTemplate<>(producerFactory, authReplyContainer);
    }
}
