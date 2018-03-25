package org.dave.iq.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.dave.iq.api.IIslandQuestsPlugin;
import org.dave.iq.core.client.ClientInfo;
import org.dave.iq.core.configuration.ConfigurationHandler;
import org.dave.iq.core.init.Dimensions;
import org.dave.iq.core.islands.IslandCapabilityRegistry;
import org.dave.iq.core.islands.IslandRegistry;
import org.dave.iq.core.islands.IslandTypeRegistry;
import org.dave.iq.core.locking.PlayerEvents;
import org.dave.iq.core.network.PackageHandler;
import org.dave.iq.core.utility.AnnotatedInstanceUtil;
import org.dave.iq.core.utility.Logz;
import org.dave.iq.core.world.VoidIslandsEvents;
import org.dave.iq.core.world.VoidIslandsSavedData;

@Mod(modid = IQCore.MODID, version = IQCore.VERSION, name = "Island Quests", acceptedMinecraftVersions = "[1.12,1.13)")
public class IQCore {
    public static final String MODID = "iq-core";
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
        IslandCapabilityRegistry.instance.init();
        IslandTypeRegistry.instance.init();
        IslandRegistry.instance.init();

        Dimensions.init();
        PackageHandler.init();

        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);

        MinecraftForge.TERRAIN_GEN_BUS.register(VoidIslandsEvents.class);
        MinecraftForge.ORE_GEN_BUS.register(VoidIslandsEvents.class);
        MinecraftForge.EVENT_BUS.register(VoidIslandsEvents.class);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        for(IIslandQuestsPlugin plugin : AnnotatedInstanceUtil.getIQPlugins()) {
            plugin.onIslandCapabilityRegistryAvailable(IslandCapabilityRegistry.instance);
            plugin.onIslandTypeRegistryAvailable(IslandTypeRegistry.instance);

            if(event.getSide() == Side.SERVER) {
                plugin.onChunkRegistryAvailable(IslandRegistry.instance);
            }

            if(event.getSide() == Side.CLIENT) {
                plugin.onClientInfoAvailable(ClientInfo.instance);
            }
        }
    }
}
