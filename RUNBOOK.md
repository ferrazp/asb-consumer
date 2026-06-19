# asb-consumer - Runbook

## Requisitos

- Java 19 (JDK)
- Gradle Wrapper (incluido)
- Conexión a Azure Service Bus (mismo connection string que el producer)

## Configuración

| Variable | Descripción | Default |
|----------|-------------|---------|
| `ASB_CONNECTION_STRING` | Connection string de Azure Service Bus | Requerido |
| `ASB_TOPIC_NAME` | Nombre del topic a suscribirse | `prices-updates` |
| `ASB_SUBSCRIPTION_NAME` | Nombre de la subscripción del topic | `local_02_pos_30` |
| `APPLICATIONINSIGHTS_CONNECTION_STRING` | Connection string de Application Insights | Requerido para telemetría |

> **Importante:** La subscripción debe existir en el topic de Azure Service Bus.
> Si no existe, créala desde Azure Portal o Azure CLI:
> ```powershell
> az servicebus topic subscription create `
>   --resource-group <rg> `
>   --namespace-name <namespace> `
>   --topic-name prices-updates `
>   --name sub-prices-terminal_00010001
> ```

## PowerShell

```powershell
# Setear JAVA_HOME a JDK 19
$env:JAVA_HOME = "C:\Program Files\Java\jdk-19"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Setear connection string (usar el mismo que el producer)
$env:ASB_CONNECTION_STRING = "Endpoint=sb://..."
$env:ASB_TOPIC_NAME = "prices-updates"
$env:ASB_SUBSCRIPTION_NAME = "sub-prices-terminal_00010001"

# Setear Application Insights
$env:APPLICATIONINSIGHTS_CONNECTION_STRING = "InstrumentationKey=..."

# Levantar en puerto 8082
.\gradlew.bat bootRun --no-daemon --args="--spring.profiles.active=local"
```

## VSCode

Agregar a `settings.json`:

```json
{
  "java.configuration.runtimes": [
    {
      "name": "JDK-19",
      "path": "C:\\Program Files\\Java\\jdk-19",
      "default": true
    }
  ],
  "terminal.integrated.env.windows": {
    "JAVA_HOME": "C:\\Program Files\\Java\\jdk-19"
  }
}
```

### Run Configuration (`.vscode/launch.json`):

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "AsbConsumerApplication",
      "request": "launch",
      "mainClass": "com.ferrazp.asbconsumer.AsbConsumerApplication",
      "projectName": "asb-consumer",
      "args": "--spring.profiles.active=local --server.port=8082",
      "env": {
        "JAVA_HOME": "C:\\Program Files\\Java\\jdk-19",
        "APPLICATIONINSIGHTS_CONNECTION_STRING": "InstrumentationKey=..."
      },
      "vmArgs": "-Dlogging.file.name=logger.log -Dapplicationinsights.connection-string=InstrumentationKey=..."
    }
  ]
}
```

## Application Insights

### Configuración

| Variable | Descripción | Default |
|----------|-------------|---------|
| `APPLICATIONINSIGHTS_CONNECTION_STRING` | Connection string de Application Insights | Requerido para telemetría |

La connection string se configura en:
- **Local**: `application-local.yml` (ignorado por git)
- **VSCode**: `launch.json` → `env.APPLICATIONINSIGHTS_CONNECTION_STRING`
- **PowerShell**: `$env:APPLICATIONINSIGHTS_CONNECTION_STRING = "..."`
- **Docker**: `docker-compose.yml` → `APPLICATIONINSIGHTS_CONNECTION_STRING`

> ⚠️ La env var `APPLICATIONINSIGHTS_CONNECTION_STRING` tiene prioridad sobre el YAML. Si está vacía o ausente, el exportador a Azure no se configura y los spans se pierden (se verá el warning `Unable to find the Application Insights connection string` en los logs de arranque).

### Telemetría emitida

Cada mensaje recibido crea un span `PriceConsumer.processMessage` con estos atributos en `customDimensions`:

| Atributo | Descripción |
|----------|-------------|
| `asb.system` | Siempre `AzureServiceBus` |
| `asb.destination` | Topic del mensaje |
| `asb.subscription` | Subscription que consumió el mensaje |
| `asb.message.id` | ID del mensaje ASB |
| `article.id` | ID del artículo |
| `price.list.id` | ID de lista de precios |
| `amount.value` | Valor del precio |
| `consumer.status` | Siempre `consumed` |

### Verificar en Azure Portal

```
Application Insights → Logs → KQL

dependencies
| where timestamp > ago(1h)
| where name == "PriceConsumer.processMessage"
| extend msg = tostring(customDimensions)
| project timestamp, msg
| order by timestamp desc
```

### Correlación producer ↔ consumer

```kusto
dependencies
| where timestamp > ago(1h)
| where name in ("message.send", "PriceConsumer.processMessage")
| extend role = iff(name == "message.send", "producer", "consumer"),
         msg = tostring(customDimensions)
| project timestamp, role, msg
| order by timestamp desc
```

## Health Check

```
GET http://localhost:8082/actuator/health
```
