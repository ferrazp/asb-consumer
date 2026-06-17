package com.ferrazp.asbconsumer.config;

import com.ferrazp.asbconsumer.service.ConsumptionManager;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ConsumptionHealthIndicator implements HealthIndicator {

    private final ConsumptionManager consumptionManager;

    public ConsumptionHealthIndicator(ConsumptionManager consumptionManager) {
        this.consumptionManager = consumptionManager;
    }

    @Override
    public Health health() {
        if (consumptionManager.isEnabled()) {
            return Health.up()
                    .withDetail("consumption", "ACTIVE")
                    .build();
        }
        return Health.status("PAUSED")
                .withDetail("consumption", "PAUSED")
                .build();
    }
}
