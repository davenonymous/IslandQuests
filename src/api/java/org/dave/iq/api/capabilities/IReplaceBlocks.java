package org.dave.iq.api.capabilities;

import net.minecraft.block.state.IBlockState;

import java.util.Random;

public interface IReplaceBlocks {
    IBlockState getReplacement(IBlockState state, Random rand);
    double getChanceForReplacement(IBlockState state);
}
