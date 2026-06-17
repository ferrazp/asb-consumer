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

> **Importante:** La subscripción debe existir en el topic de Azure Service Bus.
> Si no existe, créala desde Azure Portal o Azure CLI:
> ```powershell
> az servicebus topic subscription create `
>   --resource-group <rg> `
>   --namespace-name <namespace> `
>   --topic-name prices-updates `
>   --name prices-consumer-dev
> ```

## PowerShell

```powershell
# Setear JAVA_HOME a JDK 19
$env:JAVA_HOME = "C:\Program Files\Java\jdk-19"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Setear connection string (usar el mismo que el producer)
$env:ASB_CONNECTION_STRING = "Endpoint=sb://..."
$env:ASB_TOPIC_NAME = "prices-updates"
$env:ASB_SUBSCRIPTION_NAME = "prices-consumer-dev"

# Levantar en puerto 8081
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
      "args": "--spring.profiles.active=local",
      "env": {
        "JAVA_HOME": "C:\\Program Files\\Java\\jdk-19",
        "ASB_CONNECTION_STRING": "Endpoint=sb://...",
        "ASB_TOPIC_NAME": "prices-updates",
        "ASB_SUBSCRIPTION_NAME": "prices-consumer-dev"
      }
    }
  ]
}
```

## Health Check

```
GET http://localhost:8081/actuator/health
```
