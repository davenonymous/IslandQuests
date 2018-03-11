package org.dave.islandquests;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.dave.islandquests.api.IIslandQuestsPlugin;
import org.dave.islandquests.configuration.ConfigurationHandler;
import org.dave.islandquests.init.Dimensions;
import org.dave.islandquests.islands.IslandRegistry;
import org.dave.islandquests.islands.IslandTypeRegistry;
import org.dave.islandquests.locking.PlayerEvents;
import org.dave.islandquests.utility.AnnotatedInstanceUtil;
import org.dave.islandquests.utility.Logz;
import org.dave.islandquests.world.VoidIslandsEvents;
import org.dave.islandquests.world.VoidIslandsSavedData;

@Mod(modid = IslandQuests.MODID, version = IslandQuests.VERSION, name = "Island Quests", acceptedMinecraftVersions = "[1.12,1.13)")
public class IslandQuests {
    public static final String MODID = "islandquests";
    public static final String VERSION = "1.0.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Logz.logger = event.getModLog();
        AnnotatedInstanceUtil.setAsmData(event.getAsmData());

        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        MinecraftForge.EVENT_BUS.register(ConfigurationHandler.class);
        MinecraftForge.EVENT_BUS.register(VoidIslandsSavedData.class);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        IslandTypeRegistry.instance.init();
        IslandRegistry.instance.init();

        Dimensions.init();
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);

        MinecraftForge.TERRAIN_GEN_BUS.register(VoidIslandsEvents.class);
        MinecraftForge.ORE_GEN_BUS.register(VoidIslandsEvents.class);
        MinecraftForge.EVENT_BUS.register(VoidIslandsEvents.class);
    }

    public void postInit(FMLPostInitializationEvent event) {
        for(IIslandQuestsPlugin plugin : AnnotatedInstanceUtil.getIQPlugins()) {
            plugin.onChunkRegistryAvailable(IslandRegistry.instance);
        }
    }
}
