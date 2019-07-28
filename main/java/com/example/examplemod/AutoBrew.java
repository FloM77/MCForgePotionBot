package com.example.examplemod;

import com.example.examplemod.util.ExecuteAfter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml

@Mod("autobrew")
public class AutoBrew
{
    private static final Logger LOGGER = LogManager.getLogger();

    public AutoBrew() {
        MinecraftForge.EVENT_BUS.register(MainHandler.class);
        MainHandler.init();
    }
}