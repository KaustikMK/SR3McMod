package com.project.worldbridge;

import org.bukkit.plugin.java.JavaPlugin;

public class WorldStreamer extends JavaPlugin {

    private static WorldStreamer instance;
    private SocketClient socketClient;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("MinecraftWorldBridge enabled.");
        socketClient = new SocketClient();
        socketClient.connect();

        // Register commands
        this.getCommand("spawn_sr3_boss").setExecutor(new CommandHandler(socketClient));
        this.getCommand("sr3_status").setExecutor(new CommandHandler(socketClient));

        // Start chunk collector loop
        getServer().getScheduler().runTaskTimer(this, new ChunkCollector(socketClient), 0L, 10L); // every 10 ticks
    }

    @Override
    public void onDisable() {
        getLogger().info("MinecraftWorldBridge disabled.");
        if (socketClient != null) {
            socketClient.disconnect();
        }
    }

    public static WorldStreamer getInstance() {
        return instance;
    }
}