package com.project.worldbridge;

import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClient {
    private WebSocketClient client;
    private boolean connected = false;

    public void connect() {
        try {
            client = new WebSocketClient(new URI("ws://localhost:8765")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    connected = true;
                    Bukkit.getLogger().info("[WorldBridge] Connected to Bridge Server.");
                    // Identify as Minecraft client
                    send("{\"client\": \"minecraft\"}");
                }

                @Override
                public void onMessage(String message) {
                    // Handle any bridge-to-Minecraft responses if needed
                    Bukkit.getLogger().info("[WorldBridge] Received: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    connected = false;
                    Bukkit.getLogger().warning("[WorldBridge] Disconnected from Bridge Server: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    connected = false;
                    Bukkit.getLogger().severe("[WorldBridge] WebSocket error: " + ex.getMessage());
                }
            };

            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        if (client != null && connected) {
            client.send(message);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}