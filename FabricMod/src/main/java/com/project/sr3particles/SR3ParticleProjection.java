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

        networkListener = new NetworkListener();
        bossEntityManager = new BossEntityManager();
        particleRenderer = new ParticleRenderer();

        // Start WebSocket client to bridge
        networkListener.connect("ws://localhost:8765");

        // Hook into world render to draw particles every frame
        WorldRenderEvents.END.register(context -> particleRenderer.renderParticles(MinecraftClient.getInstance(), context));
    }
}