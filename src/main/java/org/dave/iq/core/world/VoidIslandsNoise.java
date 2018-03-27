package org.dave.iq.core.world;

import net.minecraft.world.World;
import org.dave.iq.api.IIslandNoiseMap;
import org.dave.iq.api.IIslandQuestsPlugin;
import org.dave.iq.core.configuration.ConfigurationHandler;
import org.dave.iq.core.utility.OpenSimplexNoise;
import org.dave.iq.core.init.PluginRegistry;

import java.util.Random;

public class VoidIslandsNoise implements IIslandNoiseMap {
    public static VoidIslandsNoise instance = new VoidIslandsNoise();
    private static World world;

    private OpenSimplexNoise noise;
    private OpenSimplexNoise noiseHeight;

    private Random rand;

    public void init(World world) {
        if(this.world == world) {
            return;
        }

        this.world = world;
        this.rand = new Random(this.world.getSeed());
        this.noise = new OpenSimplexNoise(rand.nextLong());
        this.noiseHeight = new OpenSimplexNoise(rand.nextLong());

        for(IIslandQuestsPlugin plugin : PluginRegistry.getIQPlugins()) {
            plugin.onIslandNoiseMapReady(VoidIslandsNoise.instance);
        }

    }

    @Override
    public double getNoise(int x, int z) {
        return this.noise.eval((double)x / ConfigurationHandler.WorldGenSettings.featureSize, (double)z / ConfigurationHandler.WorldGenSettings.featureSize);
    }

    @Override
    public double getHeightNoise(int x, int z) {
        return this.noiseHeight.eval((double)x / ConfigurationHandler.WorldGenSettings.heightFeatureSize, (double)z / ConfigurationHandler.WorldGenSettings.heightFeatureSize);
    }

    @Override
    public double getLowThreshold() {
        return ConfigurationHandler.WorldGenSettings.minimum;
    }

    @Override
    public double getHighTreshold() {
        return ConfigurationHandler.WorldGenSettings.maximum;
    }

    @Override
    public boolean isIsland(int x, int z) {
        return getNoise(x, z) > getLowThreshold();
    }
}
