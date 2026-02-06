# Kafka Spring Boot Demo - Guía de Uso

## 📋 Descripción
Proyecto de demostración completo de Apache Kafka con Spring Boot que incluye producers, consumers, y ejemplos prácticos.

## 🚀 Inicio Rápido

### 1. Iniciar Kafka con Docker

```bash
# En la raíz del proyecto
docker-compose up -d

# Verificar que los servicios estén corriendo
docker-compose ps

# Ver logs de Kafka
docker-compose logs -f kafka
```

**Servicios disponibles:**
- Kafka: `localhost:9092`
- Zookeeper: `localhost:2181`
- Kafka UI: `http://localhost:8080`

### 2. Compilar el Proyecto

```bash
mvn clean install
```

### 3. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8081`

## 📡 Endpoints de la API

### Health Check
```bash
curl http://localhost:8081/api/kafka/health
```

### Enviar Usuario
```bash
curl -X POST http://localhost:8081/api/kafka/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "pais": "Chile",
    "telefono": "+56912345678",
    "activo": true
  }'
```

### Enviar Pedido
```bash
curl -X POST http://localhost:8081/api/kafka/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1001,
    "usuarioId": 1,
    "monto": 150.50,
    "estado": "PENDIENTE",
    "descripcion": "Compra de productos",
    "cantidad": 3
  }'
```

### Generar Datos de Prueba
```bash
# Genera 10 usuarios y 10 pedidos
curl -X POST "http://localhost:8081/api/kafka/generar-datos?cantidad=10"

# Genera 50 usuarios y 50 pedidos
curl -X POST "http://localhost:8081/api/kafka/generar-datos?cantidad=50"
```

## 🔍 Monitoreo con Kafka UI

1. Abrir navegador en `http://localhost:8080`
2. Explorar topics, particiones, mensajes
3. Ver consumer groups y lag
4. Monitorear brokers

## 📚 Comandos Útiles de Kafka

### Listar Topics
```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Ver Mensajes de un Topic
```bash
# Desde el inicio
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic usuarios-topic \
  --from-beginning

# Solo nuevos mensajes
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic pedidos-topic
```

### Describir un Topic
```bash
docker exec -it kafka kafka-topics \
  --describe \
  --topic usuarios-topic \
  --bootstrap-server localhost:9092
```

### Ver Consumer Groups
```bash
docker exec -it kafka kafka-consumer-groups \
  --list \
  --bootstrap-server localhost:9092
```

### Ver Estado de un Consumer Group
```bash
docker exec -it kafka kafka-consumer-groups \
  --describe \
  --group demo-consumer-group \
  --bootstrap-server localhost:9092
```

## 🏗️ Estructura del Proyecto

```
src/main/java/com/ejemplo/kafka/
├── KafkaDemoApplication.java     # Clase principal
├── config/
│   └── KafkaTopicConfig.java     # Configuración de topics
├── controller/
│   └── KafkaController.java      # REST endpoints
├── model/
│   ├── Usuario.java              # Modelo Usuario
│   └── Pedido.java               # Modelo Pedido
├── producer/
│   ├── UsuarioProducer.java      # Producer de usuarios
│   └── PedidoProducer.java       # Producer de pedidos
└── consumer/
    ├── UsuarioConsumer.java      # Consumer de usuarios
    └── PedidoConsumer.java       # Consumer de pedidos
```

## 📊 Topics Configurados

| Topic | Particiones | Retención | Propósito |
|-------|------------|-----------|-----------|
| usuarios-topic | 3 | 7 días | Eventos de usuarios |
| pedidos-topic | 5 | Compactado | Estados de pedidos |
| notificaciones-topic | 2 | 1 día | Notificaciones temporales |
| eventos-topic | 3 | 30 días | Eventos del sistema |

## 🧪 Pruebas

### Prueba Completa del Flujo

1. **Iniciar Kafka**:
   ```bash
   docker-compose up -d
   ```

2. **Iniciar aplicación**:
   ```bash
   mvn spring-boot:run
   ```

3. **Generar datos de prueba**:
   ```bash
   curl -X POST "http://localhost:8081/api/kafka/generar-datos?cantidad=10"
   ```

4. **Ver logs de la aplicación** para observar:
    - Mensajes enviados por los producers
    - Mensajes recibidos por los consumers
    - Particiones y offsets

5. **Verificar en Kafka UI** (`http://localhost:8080`):
    - Topics creados
    - Mensajes en cada partición
    - Consumer groups activos
    - Lag de consumidores

## ⚙️ Configuración Importante

### Producer
- **acks=all**: Garantiza durabilidad máxima
- **enable.idempotence=true**: Evita duplicados
- **retries=3**: Reintentos automáticos
- **compression=snappy**: Compresión de mensajes

### Consumer
- **enable.auto.commit=false**: Confirmación manual
- **auto.offset.reset=earliest**: Lee desde el inicio
- **max.poll.records=500**: Máximo de registros por poll

## 🛑 Detener Servicios

```bash
# Detener aplicación Spring Boot
Ctrl + C

# Detener Kafka y servicios
docker-compose down

# Detener y eliminar volúmenes (limpia todos los datos)
docker-compose down -v
```

## 🔧 Solución de Problemas

### Kafka no inicia
```bash
# Ver logs
docker-compose logs kafka

# Reiniciar servicios
docker-compose restart
```

### Consumidor no recibe mensajes
- Verificar que el consumer group esté activo
- Revisar configuración de offsets
- Verificar que el topic exista y tenga mensajes

### Error de serialización
- Verificar que los modelos tengan constructores sin argumentos
- Verificar configuración de trusted packages en application.yml

## 📖 Recursos Adicionales

- [Documentación Kafka](https://kafka.apache.org/documentation/)
- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)
- [Kafka UI](https://github.com/provectus/kafka-ui)

## 🎯 Próximos Pasos

1. Implementar Dead Letter Queue (DLQ)
2. Agregar métricas con Micrometer
3. Implementar Kafka Streams
4. Agregar Avro/Schema Registry
5. Implementar testing con Testcontainers
# Health check
curl http://localhost:8081/api/kafka/health

# Generar datos de prueba
curl -X POST "http://localhost:8081/api/kafka/generar-datos?cantidad=5"curl -X POST "http://localhost:8081/api/kafka/generar-datos?cantidad=5"