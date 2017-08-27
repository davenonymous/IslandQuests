package org.dave.islandquests.configuration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.islandquests.IslandQuests;
import org.dave.islandquests.utility.JarExtract;
import org.dave.islandquests.utility.Logz;

import java.io.File;

public class ConfigurationHandler {
    public static Configuration configuration;

    public static File configDir;
    public static File islandDir;
    public static File questDir;

    private static final String CATEGORY_WORLDGEN = "WorldGen";

    public static void init(File configFile) {
        if(configuration != null) {
            return;
        }

        configDir = new File(configFile.getParentFile(), IslandQuests.MODID);
        if(!configDir.exists()) {
            configDir.mkdirs();
        }

        islandDir = new File(configDir, "islands");
        if(islandDir.exists()) {
            //islandDir.mkdirs();

            int count = JarExtract.copy("assets/islandquests/config/islands", islandDir);
            Logz.info("Extracted %d islands configs", count);
        }

        questDir = new File(configDir, "quests");
        if(!questDir.exists()) {
            questDir.mkdirs();

            int count = JarExtract.copy("assets/islandquests/config/quests", questDir);
            Logz.info("Extracted %d quests configs", count);
        }

        configuration = new Configuration(new File(configDir, "settings.cfg"), null);
        loadConfiguration();
    }

    @SubscribeEvent
    public static void onConfigurationChanged(ConfigChangedEvent event) {
        if(!event.getModID().equalsIgnoreCase(IslandQuests.MODID)) {
            return;
        }

        loadConfiguration();
    }

    private static void loadConfiguration() {
        WorldGenSettings.maxFloorHeight = configuration.getInt(
            "maxFloorHeight",
            CATEGORY_WORLDGEN,
            16, 1, 64,
            "Maximum height of the ground in blocks"
        );

        WorldGenSettings.maxHillHeight = configuration.getInt(
            "maxHillHeight",
            CATEGORY_WORLDGEN,
            8, 0, 64,
            "Maximum height of hills on top of islands"
        );

        WorldGenSettings.featureSize = configuration.getFloat(
            "featureSize",
            CATEGORY_WORLDGEN,
            100.0f, 1.0f, 1024.0f,
            "Simplex noise feature size, i.e. the scale of the generated noise"
        );

        WorldGenSettings.minimum = configuration.getFloat(
            "minimumTreshold",
            CATEGORY_WORLDGEN,
            0.4f, 0.0f, 1.0f,
            "Simplex noise minimum treshold. This is used to determine the island shapes."
        );

        WorldGenSettings.maximum = configuration.getFloat(
            "maximumTreshold",
            CATEGORY_WORLDGEN,
            0.9f, 0.0f, 1.0f,
            "Simplex noise maximum treshold. This is used to determine the island shapes."
        );

        if(configuration.hasChanged()) {
            configuration.save();
        }
    }




}
