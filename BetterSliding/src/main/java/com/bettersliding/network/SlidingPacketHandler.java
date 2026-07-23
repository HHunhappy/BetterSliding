package com.bettersliding.network;

import com.bettersliding.BetterSliding;
import com.bettersliding.handler.SlidingHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class SlidingPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BetterSliding.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, StartSlidePacket.class,
                StartSlidePacket::encode,
                StartSlidePacket::decode,
                StartSlidePacket::handle);
    }

    public static void sendStartSlide() {
        CHANNEL.sendToServer(new StartSlidePacket());
    }

    public record StartSlidePacket() {
        public static StartSlidePacket decode(FriendlyByteBuf buf) {
            return new StartSlidePacket();
        }
        public void encode(FriendlyByteBuf buf) {}

        public static void handle(StartSlidePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player != null) {
                    SlidingHandler.startSlide(player);
                }
            });
            ctx.setPacketHandled(true);
        }
    }
}
