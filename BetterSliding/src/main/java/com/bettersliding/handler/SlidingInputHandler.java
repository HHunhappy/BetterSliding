package com.bettersliding.handler;

import com.bettersliding.BetterSliding;
import com.bettersliding.network.SlidingPacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BetterSliding.MOD_ID, value = Dist.CLIENT)
public class SlidingInputHandler {

    public static KeyMapping SLIDE_KEY;
    private static boolean wasSlideKeyDown = false;

    public static void initKeyMapping() {
        SLIDE_KEY = new KeyMapping(
                "key.bettersliding.slide",
                GLFW.GLFW_KEY_LEFT_CONTROL,
                "key.categories.bettersliding");
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            wasSlideKeyDown = false;
            return;
        }

        boolean isKeyDown = SLIDE_KEY.isDown();
        if (isKeyDown && !wasSlideKeyDown && canSlideLocally(mc.player)) {
            SlidingPacketHandler.sendStartSlide();
        }
        wasSlideKeyDown = isKeyDown;
    }

    private static boolean canSlideLocally(LocalPlayer player) {
        return player.isSprinting()
                && player.onGround()
                && !player.isFallFlying()
                && !player.isSwimming()
                && !player.isPassenger()
                && !player.getAbilities().flying;
    }
}
