package com.example.examplemod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml

@Mod("beehelper")
public class ExampleMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ExampleMod() {
        // Register the setup method for modloading
        MinecraftForge.EVENT_BUS.register(MainHandler.class);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onContainer);
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call

}