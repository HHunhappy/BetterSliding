package com.bettersliding;

import com.bettersliding.handler.SlidingInputHandler;
import com.bettersliding.network.SlidingPacketHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(BetterSliding.MOD_ID)
public class BetterSliding {

    public static final String MOD_ID = "bettersliding";

    public BetterSliding(ModContainer container, IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        container.registerConfig(ModConfig.Type.SERVER, SlidingConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SlidingPacketHandler::register);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SlidingInputHandler::initKeyMapping);
    }
}
