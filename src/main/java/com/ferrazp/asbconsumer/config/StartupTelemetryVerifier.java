package com.ferrazp.asbconsumer.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTelemetryVerifier {

    private static final Logger log = LoggerFactory.getLogger(StartupTelemetryVerifier.class);

    private final Tracer tracer;
    private final String aiConnectionString;

    public StartupTelemetryVerifier(OpenTelemetry openTelemetry,
                                    @Value("${applicationinsights.connection-string:}") String aiConnectionString) {
        this.tracer = openTelemetry.getTracer("asb-consumer", "0.0.1-SNAPSHOT");
        this.aiConnectionString = aiConnectionString;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (aiConnectionString == null || aiConnectionString.isBlank()) {
            log.warn("Application Insights connection string not configured. " +
                     "Set APPLICATIONINSIGHTS_CONNECTION_STRING env var or applicationinsights.connection-string property.");
            return;
        }

        log.info("Application Insights connection string is configured. Sending test telemetry...");

        Span span = tracer.spanBuilder("ai.startup")
                .setAttribute("service", "asb-consumer")
                .setAttribute("test", "startup-heartbeat")
                .startSpan();

        try {
            span.setStatus(StatusCode.OK);
            log.info("Test span 'ai.startup' emitted successfully. " +
                     "Check Application Insights > Transaction Search for 'ai.startup' to confirm connectivity.");
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            log.error("Failed to emit test span to Application Insights.", e);
        } finally {
            span.end();
        }
    }
}
