# asb-consumer

Consumer de Azure Service Bus para el topic `prices-updates`. Escucha mensajes de precio usando la subscription `local_02_pos_30`.

## Stack

- Java 19
- Spring Boot 3.3.5
- Azure Messaging ServiceBus 7.17.9
- Application Insights (OpenTelemetry)

## Requisitos

- JDK 19 (`C:\Program Files\Java\jdk-19`)
- Gradle Wrapper (incluido)
- Suscripción `local_02_pos_30` existente en el topic `prices-updates` de ASB

## Ejecutar

### Desde VSCode

1. Abrir la carpeta del proyecto
2. Run > Start Debugging (F5) con la config `AsbConsumerApplication`

### Desde PowerShell

```powershell
.\gradlew.bat bootRun --no-daemon --args="--spring.profiles.active=local --server.port=8082"
```

### Health Check

```
GET http://localhost:8082/actuator/health
```

## Telemetría

Cada mensaje recibido crea un span `PriceConsumer.processMessage` en OpenTelemetry con atributos:
- `message.id`, `message.subject`
- `article.id`, `price.list.id`, `amount.value`

Si configurás `APPLICATIONINSIGHTS_CONNECTION_STRING`, los spans se exportan automáticamente a Application Insights.

## Logs

Todo (conexiones ASB + mensajes recibidos) se loguea en `logger.log` en la raíz del proyecto.

## Variables de entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `ASB_TOPIC_NAME` | Nombre del topic | `prices-updates` |
| `ASB_SUBSCRIPTION_NAME` | Nombre de la subscripción | `local_02_pos_30` |
| `APPLICATIONINSIGHTS_CONNECTION_STRING` | Connection string de Application Insights | (opcional) |
