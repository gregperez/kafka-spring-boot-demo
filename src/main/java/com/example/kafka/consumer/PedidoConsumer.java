package com.example.kafka.consumer;

import com.example.kafka.model.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PedidoConsumer {

    @KafkaListener(
            topics = "${kafka.topics.pedidos}",
            groupId = "pedidos-processor-group"
    )
    public void consumirPedido(
            @Payload Pedido pedido,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("Recibido pedido: {} desde partición: {} offset: {}",
                pedido.getId(), partition, offset);

        try {
            procesarPedido(pedido);

            // Confirmar manualmente después de procesar exitosamente
            acknowledgment.acknowledge();
            log.info("Pedido procesado y confirmado: {}", pedido.getId());

        } catch (Exception e) {
            log.error("Error procesando pedido: {}", pedido.getId(), e);
            // No confirmamos, el mensaje se reprocesará
        }
    }

    private void procesarPedido(Pedido pedido) {
        // Lógica de negocio
        log.info("Procesando pedido de monto: {}", pedido.getMonto());
    }
}
