package com.ferrazp.asbconsumer.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsbConfig {

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${azure.servicebus.topic-name}")
    private String topicName;

    @Value("${azure.servicebus.subscription-name}")
    private String subscriptionName;

    @Bean
    public ServiceBusClientBuilder serviceBusClientBuilder() {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString);
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public ServiceBusProcessorClient serviceBusProcessorClient(
            ServiceBusClientBuilder clientBuilder,
            com.ferrazp.asbconsumer.service.PriceConsumer processor) {

        return clientBuilder
                .processor()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .processMessage(processor::processMessage)
                .processError(processor::processError)
                .buildProcessorClient();
    }
}
