package org.dave.iq.core.islands;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.iq.api.IIslandCapability;
import org.dave.iq.api.IIslandType;
import org.dave.iq.core.utility.Logz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IslandType implements IIslandType {
    public String name;

    public Biome biome;
    public int minimumYLevel;
    public int yOffsetRange;

    public int maxHillHeight;
    public int maxFloorHeight;

    public double weight;

    public IBlockState topBlock;
    public IBlockState fillerBlock;
    public IBlockState bedrockBlock;

    public boolean isEdgeBedrock;

    public Map<String, IIslandCapability> capabilities = new HashMap<>();


    public IslandType(String name) {
        this.name = name;
        this.biome = Biomes.PLAINS;
        this.minimumYLevel = 60;
        this.maxHillHeight = 8;
        this.maxFloorHeight = 16;
        this.yOffsetRange = 40;
        this.weight = 100.0d;
        this.isEdgeBedrock = false;
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

    public void setMaxHillHeight(int maxHillHeight) {
        this.maxHillHeight = maxHillHeight;
    }

    public void setMaxFloorHeight(int maxFloorHeight) {
        this.maxFloorHeight = maxFloorHeight;
    }

    public int getRandomYOffset(Random rand) {
        return rand.nextInt(this.yOffsetRange);
    }

    public void setEdgeBedrock(boolean edgeBedrock) {
        this.isEdgeBedrock = edgeBedrock;
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

    public void addCapability(String capName, IIslandCapability capability) {
        capabilities.put(capName, capability);
    }

    public void setTopBlock(IBlockState state) {
        this.topBlock = state;
    }

    @SuppressWarnings("deprecation")
    public void setTopBlock(String blockName, int meta) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if(block == null) {
            Logz.warn("There is no '%s', can not set top block for island '%s'", blockName, name);
            return;
        }

        IBlockState state = block.getStateFromMeta(meta);
        this.topBlock = state;
    }

    public void setFillerBlock(IBlockState state) {
        this.fillerBlock = state;
    }

    @SuppressWarnings("deprecation")
    public void setFillerBlock(String blockName, int meta) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if(block == null) {
            Logz.warn("There is no '%s', can not set filler block for island '%s'", blockName, name);
            return;
        }

        IBlockState state = block.getStateFromMeta(meta);
        this.fillerBlock = state;
    }

    public void setBedrockBlock(IBlockState state) {
        this.bedrockBlock = state;
    }

    @SuppressWarnings("deprecation")
    public void setBedrockBlock(String blockName, int meta) {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if(block == null) {
            Logz.warn("There is no '%s', can not set bedrock block for island '%s'", blockName, name);
            return;
        }

        IBlockState state = block.getStateFromMeta(meta);
        this.bedrockBlock = state;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Biome getBiome() {
        return biome;
    }

    @Override
    public IBlockState getTopBlock() {
        return topBlock != null ? topBlock : biome.topBlock;
    }

    @Override
    public IBlockState getFillerBlock() {
        return fillerBlock != null ? fillerBlock : biome.fillerBlock;
    }

    @Override
    public IBlockState getBedrockBlock() {
        return bedrockBlock != null ? bedrockBlock : null;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int getIslandHeightOffsetRange() {
        return yOffsetRange;
    }

    @Override
    public int getMinimumYLevel() {
        return minimumYLevel;
    }

    @Override
    public int getMaxFloorHeight() {
        return maxFloorHeight;
    }

    @Override
    public int getMaxHillHeight() {
        return maxHillHeight;
    }

    @Override
    public boolean isEdgeBedrock() {
        return isEdgeBedrock;
    }


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        String capName = capability.getName();
        if(capName.contains(".")) {
            String[] parts = capName.split("\\.");
            capName = parts[parts.length - 1];
        }

        return capabilities.containsKey(capName);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        String capName = capability.getName();
        if(capName.contains(".")) {
            String[] parts = capName.split("\\.");
            capName = parts[parts.length - 1];
        }

        return (T) capabilities.get(capName);
    }
}
