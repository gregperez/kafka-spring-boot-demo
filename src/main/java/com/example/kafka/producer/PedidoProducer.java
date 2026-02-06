package com.example.kafka.producer;

import com.example.kafka.model.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.pedidos}")
    private String topic;

    public void enviarPedido(Pedido pedido) {
        log.info("Enviando pedido: {}", pedido.getId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, pedido.getId().toString(), pedido);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Pedido enviado exitosamente: {} con offset: {}",
                        pedido.getId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error al enviar pedido: {}", pedido.getId(), ex);
            }
        });
    }
}
