package org.dave.islandquests.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

import java.util.Map;

public interface IIslandType {
    String getName();

    Biome getBiome();
    IBlockState getTopBlock();
    IBlockState getFillerBlock();
    IBlockState getBedrockBlock();

    Map<IBlockState, Float> getOreWeights();
}
