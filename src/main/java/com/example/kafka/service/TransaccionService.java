package com.example.kafka.service;

import com.example.kafka.model.Pedido;
import com.example.kafka.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void procesarPedidoConTransaccion(Pedido pedido, Usuario usuario) {
        try {
            // Iniciar transacción
            kafkaTemplate.executeInTransaction(operations -> {
                // Enviar múltiples mensajes en una sola transacción
                operations.send("pedidos-topic", pedido.getId().toString(), pedido);
                operations.send("usuarios-topic", usuario.getId().toString(), usuario);
                operations.send("auditoria-topic", "audit-" + pedido.getId(),
                        "Pedido y usuario procesados");

                log.info("Transacción completada para pedido: {}", pedido.getId());
                return true;
            });
        } catch (Exception e) {
            log.error("Error en transacción para pedido: {}", pedido.getId(), e);
            throw new RuntimeException("Transacción fallida", e);
        }
    }
}
