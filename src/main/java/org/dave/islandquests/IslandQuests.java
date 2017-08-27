package org.dave.islandquests;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.dave.islandquests.init.Dimensions;
import org.dave.islandquests.locking.PlayerEvents;
import org.dave.islandquests.configuration.ConfigurationHandler;
import org.dave.islandquests.islands.IslandChunkRegistry;
import org.dave.islandquests.islands.IslandTypeRegistry;
import org.dave.islandquests.world.VoidIslandsSavedData;

@Mod(modid = IslandQuests.MODID, version = IslandQuests.VERSION, name = "IslandType Quests")
public class IslandQuests {
    public static final String MODID = "islandquests";
    public static final String VERSION = "1.0.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        MinecraftForge.EVENT_BUS.register(ConfigurationHandler.class);
        MinecraftForge.EVENT_BUS.register(VoidIslandsSavedData.class);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        IslandTypeRegistry.init();
        IslandChunkRegistry.init();
        Dimensions.init();
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
    }
}
