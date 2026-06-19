package com.ferrazp.asbconsumer.controller;

import com.ferrazp.asbconsumer.service.ConsumptionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/consumption")
public class ConsumptionController {

    private final ConsumptionManager consumptionManager;
    private final String topicName;
    private final String subscriptionName;

    public ConsumptionController(ConsumptionManager consumptionManager,
                                 @Value("${azure.servicebus.topic-name}") String topicName,
                                 @Value("${azure.servicebus.subscription-name}") String subscriptionName) {
        this.consumptionManager = consumptionManager;
        this.topicName = topicName;
        this.subscriptionName = subscriptionName;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "enabled", consumptionManager.isEnabled(),
                "consumption", consumptionManager.isEnabled() ? "ACTIVE" : "PAUSED"
        ));
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggle() {
        boolean nowEnabled = consumptionManager.toggle();
        return ResponseEntity.ok(Map.of(
                "enabled", nowEnabled,
                "consumption", nowEnabled ? "ACTIVE" : "PAUSED"
        ));
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String ui() {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>ASB Consumer - Control</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        background: #0f172a; color: #e2e8f0; display: flex;
                        justify-content: center; align-items: center; min-height: 100vh;
                    }
                    .card {
                        background: #1e293b; border-radius: 16px; padding: 40px;
                        box-shadow: 0 25px 50px rgba(0,0,0,0.4); text-align: center;
                        max-width: 400px; width: 90%;
                    }
                    h1 { font-size: 1.5rem; margin-bottom: 8px; color: #f1f5f9; }
                    .subtitle { font-size: 0.85rem; color: #64748b; margin-bottom: 32px; }
                    .status-badge {
                        display: inline-block; padding: 8px 20px; border-radius: 20px;
                        font-weight: 600; font-size: 0.9rem; margin-bottom: 24px;
                    }
                    .status-badge.active { background: #065f46; color: #6ee7b7; }
                    .status-badge.paused { background: #7f1d1d; color: #fca5a5; }
                    .toggle-btn {
                        background: #3b82f6; color: white; border: none; padding: 14px 40px;
                        border-radius: 10px; font-size: 1rem; font-weight: 600;
                        cursor: pointer; transition: background 0.2s, transform 0.1s;
                    }
                    .toggle-btn:hover { background: #2563eb; }
                    .toggle-btn:active { transform: scale(0.97); }
                    .toggle-btn.paused { background: #ef4444; }
                    .toggle-btn.paused:hover { background: #dc2626; }
                    .info { margin-top: 24px; font-size: 0.8rem; color: #475569; }
                    .spinner { display: none; margin: 16px auto; width: 24px; height: 24px;
                        border: 3px solid #334155; border-top-color: #3b82f6;
                        border-radius: 50%; animation: spin 0.8s linear infinite; }
                    @keyframes spin { to { transform: rotate(360deg); } }
                </style>
                </head>
                <body>
                <div class="card">
                    <h1>ASB Consumer</h1>
                    <p class="subtitle">topic: __TOPIC__ / sub: __SUB__</p>
                    <div id="badge" class="status-badge">---</div>
                    <div class="spinner" id="spinner"></div>
                    <button class="toggle-btn" id="toggleBtn" onclick="toggle()">---</button>
                    <p class="info">Los mensajes quedan en la subscripci&oacute;n cuando est&aacute; pausado.</p>
                </div>
                <script>
                    async function fetchStatus() {
                        const r = await fetch('/api/consumption/status');
                        return r.json();
                    }
                    async function updateUI() {
                        const s = await fetchStatus();
                        const badge = document.getElementById('badge');
                        const btn = document.getElementById('toggleBtn');
                        const active = s.enabled;
                        badge.textContent = active ? 'ACTIVE' : 'PAUSED';
                        badge.className = 'status-badge ' + (active ? 'active' : 'paused');
                        btn.textContent = active ? 'Pause Consumption' : 'Resume Consumption';
                        btn.className = 'toggle-btn ' + (active ? '' : 'paused');
                    }
                    async function toggle() {
                        document.getElementById('spinner').style.display = 'block';
                        document.getElementById('toggleBtn').disabled = true;
                        await fetch('/api/consumption/toggle', { method: 'POST' });
                        await updateUI();
                        document.getElementById('spinner').style.display = 'none';
                        document.getElementById('toggleBtn').disabled = false;
                    }
                    updateUI();
                </script>
                </body>
                </html>
                """.replace("__TOPIC__", topicName).replace("__SUB__", subscriptionName);
    }
}
