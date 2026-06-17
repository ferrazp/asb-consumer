package com.ferrazp.asbconsumer.service;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionManager {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionManager.class);

    private final ServiceBusProcessorClient processorClient;
    private volatile boolean enabled;

    public ConsumptionManager(ServiceBusProcessorClient processorClient) {
        this.processorClient = processorClient;
        this.enabled = false;
    }

    @PostConstruct
    public void init() {
        enable();
    }

    public synchronized void enable() {
        if (enabled) {
            log.info("Consumption already enabled");
            return;
        }
        processorClient.start();
        enabled = true;
        log.info("Consumption enabled - processor started");
    }

    public synchronized void disable() {
        if (!enabled) {
            log.info("Consumption already disabled");
            return;
        }
        processorClient.stop();
        enabled = false;
        log.info("Consumption disabled - processor stopped");
    }

    public synchronized boolean toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
