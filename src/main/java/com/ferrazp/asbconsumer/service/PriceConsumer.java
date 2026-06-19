package com.ferrazp.asbconsumer.service;

import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.core.util.BinaryData;
import com.ferrazp.asbconsumer.model.PriceMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PriceConsumer {

    private static final Logger log = LoggerFactory.getLogger(PriceConsumer.class);
    private final ObjectMapper objectMapper;
    private final Tracer tracer;
    private final String topicName;
    private final String subscriptionName;

    public PriceConsumer(ObjectMapper objectMapper, OpenTelemetry openTelemetry,
                         @Value("${azure.servicebus.topic-name}") String topicName,
                         @Value("${azure.servicebus.subscription-name}") String subscriptionName) {
        this.objectMapper = objectMapper;
        this.tracer = openTelemetry.getTracer("asb-consumer", "0.0.1-SNAPSHOT");
        this.topicName = topicName;
        this.subscriptionName = subscriptionName;
    }

    public void processMessage(ServiceBusReceivedMessageContext messageContext) {
        ServiceBusReceivedMessage message = messageContext.getMessage();
        Span span = tracer.spanBuilder("PriceConsumer.processMessage")
                .startSpan();

        try (Scope ignored = span.makeCurrent()) {
            String json = message.getBody().toString();
            PriceMessage price = objectMapper.readValue(json, PriceMessage.class);

            span.setAttribute("asb.system", "AzureServiceBus");
            span.setAttribute("asb.destination", topicName);
            span.setAttribute("asb.subscription", subscriptionName);
            span.setAttribute("asb.message.id", message.getMessageId());
            span.setAttribute("article.id", price.getArticleId());
            span.setAttribute("price.list.id", price.getPriceListId());
            span.setAttribute("amount.value", price.getAmountValue());
            span.setAttribute("consumer.status", "consumed");

            log.info("Received message. messageId={}, subject={}, articleId={}, priceListId={}, amountValue={}",
                    message.getMessageId(), message.getSubject(),
                    price.getArticleId(), price.getPriceListId(), price.getAmountValue());

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            log.error("Error processing message. messageId={}", message.getMessageId(), e);
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
            messageContext.abandon();
        } finally {
            span.end();
        }
    }

    public void processError(ServiceBusErrorContext errorContext) {
        log.error("Error in Service Bus processor. source={}, error={}",
                errorContext.getErrorSource(), errorContext.getException().getMessage());
    }
}
