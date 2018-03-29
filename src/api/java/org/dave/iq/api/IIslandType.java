package org.dave.iq.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

// TODO: Document at least all of the API
// TODO: Add some default methods to all Interfaces that are meant to be implemented by modders
public interface IIslandType extends ICapabilityProvider {
    String getName();

    Biome getBiome();

    IBlockState getTopBlock();
    IBlockState getFillerBlock();
    IBlockState getBedrockBlock();

    double getWeight();

    int getMinimumYLevel();
    int getIslandHeightOffsetRange();

    int getMaxFloorHeight();
    int getMaxHillHeight();

    boolean isEdgeBedrock();
}
