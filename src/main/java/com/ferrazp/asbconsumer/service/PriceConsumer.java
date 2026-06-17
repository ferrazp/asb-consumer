package com.ferrazp.asbconsumer.service;

import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.core.util.BinaryData;
import com.ferrazp.asbconsumer.model.PriceMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PriceConsumer {

    private static final Logger log = LoggerFactory.getLogger(PriceConsumer.class);
    private final ObjectMapper objectMapper;

    public PriceConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void processMessage(ServiceBusReceivedMessageContext messageContext) {
        ServiceBusReceivedMessage message = messageContext.getMessage();
        BinaryData body = message.getBody();

        try {
            String json = body.toString();
            PriceMessage price = objectMapper.readValue(json, PriceMessage.class);

            log.info("Received message. messageId={}, subject={}, articleId={}, priceListId={}, amountValue={}",
                    message.getMessageId(), message.getSubject(),
                    price.getArticleId(), price.getPriceListId(), price.getAmountValue());

        } catch (Exception e) {
            log.error("Error processing message. messageId={}", message.getMessageId(), e);
            messageContext.abandon();
        }
    }

    public void processError(ServiceBusErrorContext errorContext) {
        log.error("Error in Service Bus processor. source={}, error={}",
                errorContext.getErrorSource(), errorContext.getException().getMessage());
    }
}
