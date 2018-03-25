package org.dave.iq.api;

import net.minecraft.util.math.BlockPos;

public interface IIslandRegistry {
    boolean hasIsland(int chunkX, int chunkZ);
    boolean hasIsland(BlockPos pos);

    IIsland getIsland(int chunkX, int chunkZ);
    IIsland getIsland(BlockPos pos);
}
