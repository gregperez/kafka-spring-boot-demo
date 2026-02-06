package com.example.kafka.controller;

import com.example.kafka.model.Pedido;
import com.example.kafka.model.Usuario;
import com.example.kafka.producer.PedidoProducer;
import com.example.kafka.producer.UsuarioProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final UsuarioProducer usuarioProducer;
    private final PedidoProducer pedidoProducer;

    private static final List<String> NOMBRES = Arrays.asList(
            "Juan", "María", "Pedro", "Ana", "Carlos", "Laura", "Diego", "Sofia",
            "Miguel", "Carmen", "Luis", "Patricia", "Jorge", "Isabel", "Roberto"
    );

    private static final List<String> APELLIDOS = Arrays.asList(
            "García", "Rodríguez", "Martínez", "López", "González", "Pérez", "Sánchez",
            "Ramírez", "Torres", "Flores", "Rivera", "Gómez", "Díaz", "Cruz"
    );

    private static final List<String> PAISES = Arrays.asList(
            "Chile", "Argentina", "México", "Colombia", "Perú", "España", "Uruguay", "Venezuela"
    );

    private static final List<String> ESTADOS_PEDIDO = Arrays.asList(
            "PENDIENTE", "PROCESANDO", "ENVIADO", "ENTREGADO", "CANCELADO"
    );

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "Kafka Demo Application");

        log.info("Health check realizado");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Map<String, Object>> enviarUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuario.getFechaCreacion() == null) {
                usuario.setFechaCreacion(LocalDateTime.now());
            }

            usuarioProducer.enviarUsuario(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Usuario enviado a Kafka exitosamente");
            response.put("usuario", usuario);
            response.put("timestamp", LocalDateTime.now());

            log.info("Usuario enviado vía API: {}", usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error al enviar usuario vía API", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error al enviar usuario: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> enviarPedido(@RequestBody Pedido pedido) {
        try {
            if (pedido.getFechaPedido() == null) {
                pedido.setFechaPedido(LocalDateTime.now());
            }

            if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
                pedido.setEstado("PENDIENTE");
            }

            pedidoProducer.enviarPedido(pedido);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Pedido enviado a Kafka exitosamente");
            response.put("pedido", pedido);
            response.put("timestamp", LocalDateTime.now());

            log.info("Pedido enviado vía API: {}", pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error al enviar pedido vía API", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error al enviar pedido: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/generar-datos")
    public ResponseEntity<Map<String, Object>> generarDatosPrueba(
            @RequestParam(defaultValue = "10") int cantidad) {

        try {
            if (cantidad < 1 || cantidad > 1000) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "La cantidad debe estar entre 1 y 1000");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            log.info("Generando {} usuarios y {} pedidos de prueba", cantidad, cantidad);

            List<Long> usuariosIds = new ArrayList<>();
            int usuariosEnviados = 0;
            int pedidosEnviados = 0;

            // Generar usuarios
            for (int i = 0; i < cantidad; i++) {
                Usuario usuario = generarUsuarioAleatorio();
                usuarioProducer.enviarUsuario(usuario);
                usuariosIds.add(usuario.getId());
                usuariosEnviados++;
            }

            // Pequeña pausa para permitir que los usuarios se procesen
            Thread.sleep(100);

            // Generar pedidos asociados a los usuarios creados
            for (int i = 0; i < cantidad; i++) {
                Long usuarioId = usuariosIds.get(ThreadLocalRandom.current().nextInt(usuariosIds.size()));
                Pedido pedido = generarPedidoAleatorio(usuarioId);
                pedidoProducer.enviarPedido(pedido);
                pedidosEnviados++;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Datos de prueba generados exitosamente");
            response.put("usuariosEnviados", usuariosEnviados);
            response.put("pedidosEnviados", pedidosEnviados);
            response.put("timestamp", LocalDateTime.now());

            log.info("Datos de prueba generados: {} usuarios, {} pedidos",
                    usuariosEnviados, pedidosEnviados);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al generar datos de prueba", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error al generar datos: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private Usuario generarUsuarioAleatorio() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String nombre = NOMBRES.get(random.nextInt(NOMBRES.size()));
        String apellido = APELLIDOS.get(random.nextInt(APELLIDOS.size()));
        String nombreCompleto = nombre + " " + apellido;

        String email = nombre.toLowerCase() + "." +
                       apellido.toLowerCase() +
                       random.nextInt(1000) +
                       "@example.com";

        String pais = PAISES.get(random.nextInt(PAISES.size()));

        return Usuario.builder()
                .id(random.nextLong(1, 100000))
                .nombre(nombreCompleto)
                .email(email)
                .pais(pais)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private Pedido generarPedidoAleatorio(Long usuarioId) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        BigDecimal monto = BigDecimal.valueOf(random.nextDouble(10.0, 5000.0))
                .setScale(2, RoundingMode.HALF_UP);

        String estado = ESTADOS_PEDIDO.get(random.nextInt(ESTADOS_PEDIDO.size()));

        return Pedido.builder()
                .id(random.nextLong(1000, 999999))
                .usuarioId(usuarioId)
                .monto(monto)
                .estado(estado)
                .fechaPedido(LocalDateTime.now())
                .build();
    }
}
