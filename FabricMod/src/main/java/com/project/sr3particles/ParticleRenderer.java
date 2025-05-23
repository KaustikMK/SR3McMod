package com.project.sr3particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.json.JSONObject;

import java.util.Base64;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParticleRenderer {

    private final ConcurrentLinkedQueue<FramePacket> frameQueue = new ConcurrentLinkedQueue<>();
    private static final int FRAME_WIDTH = 128;
    private static final int FRAME_HEIGHT = 128;
    private static final float scale = 0.05f;

    public void processFrame(JSONObject frameData) {
        String camId = frameData.getString("cam_id");
        String base64Data = frameData.getString("frame_data");
        byte[] rawRGB = Base64.getDecoder().decode(base64Data);

        frameQueue.add(new FramePacket(camId, rawRGB));
    }

    public void renderParticles(MinecraftClient client, WorldRenderer worldRenderer) {
        if (client.world == null) return;

        SR3ParticleProjection.bossEntityManager.tick();
        Vec3d bossPos = SR3ParticleProjection.bossEntityManager.getBossPosition();

        // Render all queued frames as particles
        while (!frameQueue.isEmpty()) {
            FramePacket packet = frameQueue.poll();
            renderFrameAsParticles(packet, bossPos);
        }
    }

    private void renderFrameAsParticles(FramePacket packet, Vec3d centerPos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        // Each frame projects on a different plane relative to boss center
        Vec3d planeOffset = getPlaneOffset(packet.camId, centerPos);

        byte[] rgbData = packet.rgbData;
        for (int y = 0; y < FRAME_HEIGHT; y += 8) {
            for (int x = 0; x < FRAME_WIDTH; x += 8) {
                int idx = (y * FRAME_WIDTH + x) * 3;
                float r = (rgbData[idx] & 0xFF) / 255.0f;
                float g = (rgbData[idx + 1] & 0xFF) / 255.0f;
                float b = (rgbData[idx + 2] & 0xFF) / 255.0f;

                if (r + g + b > 0.1f) {
                    Vec3d particlePos = planeOffset.add(x * scale - (FRAME_WIDTH * scale) / 2, y * scale, 0);
                    mc.world.addParticle(new DustParticleEffect(new org.joml.Vector3f(r, g, b), 1.0f),
                            particlePos.x, particlePos.y, particlePos.z,
                            0, 0, 0);
                }
            }
        }
    }

    private Vec3d getPlaneOffset(String camId, Vec3d center) {
        // Offset planes around boss center based on cam
        switch (camId) {
            case "front":
                return center.add(0, 0, -1);
            case "back":
                return center.add(0, 0, 1);
            case "left":
                return center.add(-1, 0, 0);
            case "right":
                return center.add(1, 0, 0);
            default:
                return center;
        }
    }

    private static class FramePacket {
        String camId;
        byte[] rgbData;

        public FramePacket(String camId, byte[] rgbData) {
            this.camId = camId;
            this.rgbData = rgbData;
        }
    }
}