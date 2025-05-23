package com.project.sr3particles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class SR3ParticleProjection implements ClientModInitializer {

    public static SR3ParticleProjection INSTANCE;
    public static NetworkListener networkListener;
    public static BossEntityManager bossEntityManager;
    public static ParticleRenderer particleRenderer;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        bossEntityManager = new BossEntityManager();
        particleRenderer = new ParticleRenderer();

        // Start WebSocket client to bridge
        try {
            networkListener = new NetworkListener(new java.net.URI("ws://localhost:8765"));
            networkListener.connect(); // This is a blocking call, consider connectBlocking() or a thread
        } catch (java.net.URISyntaxException e) {
            System.err.println("Error creating WebSocket URI: " + e.getMessage());
            e.printStackTrace();
        }

        // Hook into world render to draw particles every frame
        WorldRenderEvents.END.register(context -> particleRenderer.renderParticles(MinecraftClient.getInstance(), context));
    }
}