package com.bettersliding;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SlidingConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue slideExhaustionCost;
    public static final ModConfigSpec.DoubleValue slideJumpExhaustionCost;
    public static final ModConfigSpec.IntValue slideDurationTicks;
    public static final ModConfigSpec.IntValue slideCooldownTicks;
    public static final ModConfigSpec.DoubleValue slideInitialSpeedMultiplier;
    public static final ModConfigSpec.DoubleValue slideJumpHorizontalBoost;
    public static final ModConfigSpec.DoubleValue slideJumpVerticalMultiplier;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("BetterSliding Configuration").push("general");

        slideExhaustionCost = builder
                .comment("Exhaustion added when starting a slide")
                .defineInRange("slideExhaustionCost", 1.5D, 0.0D, 10.0D);

        slideJumpExhaustionCost = builder
                .comment("Exhaustion added when performing a slide jump")
                .defineInRange("slideJumpExhaustionCost", 0.8D, 0.0D, 10.0D);

        slideDurationTicks = builder
                .comment("Maximum slide duration in ticks (20 ticks = 1 second)")
                .defineInRange("slideDurationTicks", 15, 5, 60);

        slideCooldownTicks = builder
                .comment("Cooldown after slide ends (in ticks)")
                .defineInRange("slideCooldownTicks", 8, 0, 40);

        slideInitialSpeedMultiplier = builder
                .comment("Multiplier for initial slide speed relative to current sprint speed")
                .defineInRange("slideInitialSpeedMultiplier", 1.2D, 0.5D, 3.0D);

        slideJumpHorizontalBoost = builder
                .comment("Horizontal velocity added when slide jumping")
                .defineInRange("slideJumpHorizontalBoost", 0.35D, 0.0D, 1.0D);

        slideJumpVerticalMultiplier = builder
                .comment("Vertical velocity multiplier when slide jumping")
                .defineInRange("slideJumpVerticalMultiplier", 1.1D, 0.5D, 2.0D);

        builder.pop();

        SPEC = builder.build();
    }
}
