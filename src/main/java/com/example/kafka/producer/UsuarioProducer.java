package com.example.kafka.producer;

import com.example.kafka.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.usuarios}")
    private String topic;

    public void enviarUsuario(Usuario usuario) {
        log.info("Enviando usuario: {}", usuario);
        kafkaTemplate.send(topic, usuario.getId().toString(), usuario);
    }
}
