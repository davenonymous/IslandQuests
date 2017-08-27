package org.dave.islandquests.islands;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.islandquests.utility.Logz;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IslandType {
    public String name;

    public Biome biome;
    public int minimumYLevel;
    public int yOffsetRange;

    public double weight;

    public Map<IBlockState, Float> oreWeights;

    public IslandType(String name) {
        this.name = name;
        this.biome = Biomes.PLAINS;
        this.minimumYLevel = 60;
        this.yOffsetRange = 40;
        this.weight = 100.0d;

        this.oreWeights = new HashMap<>();
    }

    public void setBiome(String biomeName) {
        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
        if(biome == null) {
            Logz.warn("Could not find biome with name: %s", biomeName);
            Logz.warn("Available biomes are: %s", ForgeRegistries.BIOMES.getKeys().toArray());
            return;
        }

        setBiome(biome);
    }

    public int getRandomYOffset(Random rand) {
        return rand.nextInt(this.yOffsetRange);
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public void setMinimumYLevel(int minimumYLevel) {
        this.minimumYLevel = minimumYLevel;
    }

    public void setRangeYOffset(int yOffsetRange) {
        this.yOffsetRange = yOffsetRange;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void addOreSpawn(String blockName, int meta, float weight) {
        Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if(ore == null) {
            Logz.warn("There is no '%s', skipping ore generation for this block", blockName);
            return;
        }

        IBlockState state = ore.getStateFromMeta(meta);
        oreWeights.put(state, weight);
    }
}
