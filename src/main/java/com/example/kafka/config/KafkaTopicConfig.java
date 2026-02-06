package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.usuarios}")
    private String usuariosTopic;

    @Value("${kafka.topics.pedidos}")
    private String pedidosTopic;

    @Value("${kafka.topics.notificaciones}")
    private String notificacionesTopic;

    @Bean
    public NewTopic usuariosTopic() {
        return TopicBuilder.name(usuariosTopic)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000") // 7 días
                .build();
    }

    @Bean
    public NewTopic pedidosTopic() {
        return TopicBuilder.name(pedidosTopic)
                .partitions(5)
                .replicas(1)
                .compact() // Compactación por key
                .build();
    }

    @Bean
    public NewTopic notificacionesTopic() {
        return TopicBuilder.name(notificacionesTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
