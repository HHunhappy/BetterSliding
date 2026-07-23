package com.bettersliding.handler;

import com.bettersliding.BetterSliding;
import com.bettersliding.SlidingConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BetterSliding.MOD_ID)
public class SlidingHandler {

    private static final Map<UUID, SlidingData> SLIDING_PLAYERS = new ConcurrentHashMap<>();

    private static class SlidingData {
        boolean isSliding;
        int slideTick;
        Vec3 direction;
        int cooldownTick;
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        SlidingData data = SLIDING_PLAYERS.get(player.getUUID());
        if (data == null) return;

        if (data.cooldownTick > 0) {
            data.cooldownTick--;
            if (data.cooldownTick == 0 && !data.isSliding) {
                SLIDING_PLAYERS.remove(player.getUUID());
            }
            return;
        }

        if (!data.isSliding) {
            SLIDING_PLAYERS.remove(player.getUUID());
            return;
        }

        // Detect slide jump via player leaving ground during slide
        if (!player.onGround() && player.getDeltaMovement().y > 0.1) {
            performSlideJump(player, data);
            return;
        }

        data.slideTick++;

        boolean shouldEnd = false;
        if (data.slideTick >= SlidingConfig.slideDurationTicks.get()) {
            shouldEnd = true;
        }
        if (!player.onGround() && data.slideTick > 5) {
            shouldEnd = true;
        }
        if (player.isPassenger() || player.isFallFlying() || player.isSwimming()) {
            shouldEnd = true;
        }
        if (shouldEnd) {
            endSlide(player, data);
            return;
        }

        Vec3 currentMotion = player.getDeltaMovement();
        double horizSpeed = currentMotion.horizontalDistance();
        if (horizSpeed < 0.05 && data.slideTick > 3) {
            endSlide(player, data);
            return;
        }

        double targetSpeed = 0.28;
        Vec3 dir = data.direction;
        double newX = currentMotion.x * 0.82 + dir.x * targetSpeed * 0.18;
        double newZ = currentMotion.z * 0.82 + dir.z * targetSpeed * 0.18;
        player.setDeltaMovement(newX, currentMotion.y, newZ);

        if (player.getPose() != Pose.CROUCHING) {
            player.setPose(Pose.CROUCHING);
        }
    }

    private static void performSlideJump(Player player, SlidingData data) {
        Vec3 currentMotion = player.getDeltaMovement();
        Vec3 look = new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize();
        double horizBoost = SlidingConfig.slideJumpHorizontalBoost.get();
        double vertMultiplier = SlidingConfig.slideJumpVerticalMultiplier.get();

        player.setDeltaMovement(
                currentMotion.x + look.x * horizBoost,
                currentMotion.y * vertMultiplier,
                currentMotion.z + look.z * horizBoost);

        player.causeFoodExhaustion(SlidingConfig.slideJumpExhaustionCost.get().floatValue());
        endSlide(player, data);
    }

    public static void startSlide(ServerPlayer player) {
        if (!player.isSprinting()) return;
        if (!player.onGround()) return;
        if (player.isFallFlying() || player.isSwimming() || player.isPassenger()) return;
        if (player.getAbilities().flying) return;

        SlidingData data = SLIDING_PLAYERS.computeIfAbsent(player.getUUID(), u -> new SlidingData());
        if (data.isSliding) return;
        if (data.cooldownTick > 0) return;
        if (player.getFoodData().getFoodLevel() <= 0) return;

        data.isSliding = true;
        data.slideTick = 0;
        data.direction = new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize();

        Vec3 currentMotion = player.getDeltaMovement();
        double initialSpeed = currentMotion.horizontalDistance() * SlidingConfig.slideInitialSpeedMultiplier.get();
        if (initialSpeed < 0.3) initialSpeed = 0.3;

        player.setDeltaMovement(data.direction.x * initialSpeed, currentMotion.y, data.direction.z * initialSpeed);
        player.setPose(Pose.CROUCHING);
        player.causeFoodExhaustion(SlidingConfig.slideExhaustionCost.get().floatValue());
    }

    private static void endSlide(Player player, SlidingData data) {
        data.isSliding = false;
        data.cooldownTick = SlidingConfig.slideCooldownTicks.get();
        if (player.getPose() == Pose.CROUCHING && !player.isShiftKeyDown()) {
            player.setPose(Pose.STANDING);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide) {
            SLIDING_PLAYERS.remove(event.getOriginal().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getEntity().level().isClientSide) {
            SLIDING_PLAYERS.remove(event.getEntity().getUUID());
        }
    }
}
