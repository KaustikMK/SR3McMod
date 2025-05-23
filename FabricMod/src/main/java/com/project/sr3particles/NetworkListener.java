package com.project.sr3particles;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class NetworkListener extends WebSocketClient {

    public NetworkListener(URI serverUri) {
        super(serverUri);
        System.out.println("NetworkListener initialized with URI: " + serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket bridge server: " + getURI());
        JSONObject idMsg = new JSONObject();
        idMsg.put("client", "sr3_fabric_mod"); // Identify client
        idMsg.put("type", "connection_init");
        send(idMsg.toString());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("SR3Mod NetworkListener received message: " + message);
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String messageType = jsonMessage.optString("type");

            // Example: Pass frame data to ParticleRenderer
            if ("camera_frame".equals(messageType) && SR3ParticleProjection.particleRenderer != null) {
                SR3ParticleProjection.particleRenderer.processFrame(jsonMessage);
            } else if ("boss_event".equals(messageType) && SR3ParticleProjection.bossEntityManager != null) {
                // Example: SR3ParticleProjection.bossEntityManager.handleBossEvent(jsonMessage);
                System.out.println("Received boss_event, handler in BossEntityManager would be called.");
            }
            // Add more specific message handlers based on actual protocol
        } catch (Exception e) {
            System.err.println("Error processing message in NetworkListener: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void onMessage(ByteBuffer bytes) {
        System.out.println("SR3Mod NetworkListener received binary message (not currently processed).");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("SR3Mod NetworkListener disconnected from WebSocket: " + reason + " (Code: " + code + ", Remote: " + remote + ")");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("SR3Mod NetworkListener WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    // Note: The connect() call is inherited from WebSocketClient.
    // SR3ParticleProjection.java was already modified to call:
    //   networkListener = new NetworkListener(new java.net.URI("ws://localhost:8765"));
    //   networkListener.connect();
    // This should work as expected with this class structure.
}
