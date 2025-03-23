package com.project.worldbridge;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChunkCollector extends BukkitRunnable {

    private final SocketClient socketClient;

    public ChunkCollector(SocketClient client) {
        this.socketClient = client;
    }

    @Override
    public void run() {
        if (!socketClient.isConnected()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            Location playerLoc = player.getLocation();

            JSONArray chunkArray = new JSONArray();
            int radius = 2; // Collect 2 chunks in each direction

            for (int cx = -radius; cx <= radius; cx++) {
                for (int cz = -radius; cz <= radius; cz++) {
                    Chunk chunk = world.getChunkAt(playerLoc.getChunk().getX() + cx, playerLoc.getChunk().getZ() + cz);
                    JSONObject chunkObject = new JSONObject();
                    chunkObject.put("origin", new JSONArray(new int[]{chunk.getX(), chunk.getZ()}));

                    JSONArray blockList = new JSONArray();
                    int yMin = world.getMinHeight();
                    int yMax = world.getMaxHeight();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = yMin; y < yMax; y++) {
                                if (chunk.getBlock(x, y, z).getType().isSolid()) {
                                    JSONObject blockObj = new JSONObject();
                                    blockObj.put("pos", new JSONArray(new int[]{
                                            chunk.getX() * 16 + x,
                                            y,
                                            chunk.getZ() * 16 + z
                                    }));
                                    blockObj.put("solid", true);
                                    blockList.put(blockObj);
                                }
                            }
                        }
                    }

                    chunkObject.put("blocks", blockList);
                    chunkArray.put(chunkObject);
                }
            }

            JSONObject playerBox = new JSONObject();
            Location min = player.getBoundingBox().getMin();
            Location max = player.getBoundingBox().getMax();
            playerBox.put("min", new JSONArray(new double[]{min.getX(), min.getY(), min.getZ()}));
            playerBox.put("max", new JSONArray(new double[]{max.getX(), max.getY(), max.getZ()}));

            JSONObject packet = new JSONObject();
            packet.put("type", "chunk_update");
            packet.put("player_pos", new JSONArray(new double[]{playerLoc.getX(), playerLoc.getY(), playerLoc.getZ()}));
            packet.put("chunks", chunkArray);
            packet.put("mc_player_box", playerBox);

            socketClient.send(packet.toString());
        }
    }
}