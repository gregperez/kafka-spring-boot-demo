package com.example.kafka.consumer;

import com.example.kafka.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioConsumer {

    @KafkaListener(
            topics = "${kafka.topics.usuarios}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumirUsuario(Usuario usuario) {
        log.info("Usuario recibido: {}", usuario);
        // Procesar el usuario
        procesarUsuario(usuario);
    }

    private void procesarUsuario(Usuario usuario) {
        // Lógica de negocio
        log.info("Procesando usuario: {}", usuario.getNombre());
    }
}
