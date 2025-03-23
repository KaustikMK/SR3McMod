package com.project.sr3particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.json.JSONObject;

public class BossEntityManager {

    private Vec3d bossPosition = new Vec3d(0, 100, 0); // Default spawn height
    private Vec3d bossVelocity = Vec3d.ZERO;
    private boolean moving = false;
    private boolean jumping = false;

    private final double speed = 0.1;
    private final double gravity = -0.04;

    public void updateBossPosition(JSONObject data) {
        // Direct position update from SR3 if desired
        var posArray = data.getJSONArray("pos");
        bossPosition = new Vec3d(posArray.getDouble(0), posArray.getDouble(1), posArray.getDouble(2));
    }

    public void handleInput(JSONObject inputData) {
        moving = inputData.getBoolean("moving");
        jumping = inputData.getBoolean("jumping");

        var direction = inputData.getJSONArray("direction");
        double dx = direction.getDouble(0);
        double dz = direction.getDouble(1);

        if (moving) {
            bossVelocity = new Vec3d(dx * speed, bossVelocity.y, dz * speed);
        }

        if (jumping && Math.abs(bossVelocity.y) < 0.01) {
            bossVelocity = bossVelocity.add(0, 0.3, 0);
        }
    }

    public void tick() {
        // Apply gravity
        bossVelocity = bossVelocity.add(0, gravity, 0);
        bossPosition = bossPosition.add(bossVelocity);

        // Simulate ground collision
        if (bossPosition.y < MinecraftClient.getInstance().world.getBottomY() + 5) {
            bossPosition = new Vec3d(bossPosition.x, MinecraftClient.getInstance().world.getBottomY() + 5, bossPosition.z);
            bossVelocity = new Vec3d(bossVelocity.x, 0, bossVelocity.z);
        }
    }

    public Vec3d getBossPosition() {
        return bossPosition;
    }
}