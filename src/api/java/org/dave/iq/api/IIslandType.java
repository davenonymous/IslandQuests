package org.dave.iq.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

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
