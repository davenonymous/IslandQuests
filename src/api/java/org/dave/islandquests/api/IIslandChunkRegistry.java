package org.dave.islandquests.api;

import net.minecraft.util.math.BlockPos;

public interface IIslandChunkRegistry {
    IIslandType getIslandType(int chunkX, int chunkZ);
    IIslandType getIslandType(BlockPos pos);

    boolean isKnownChunk(int chunkX, int chunkZ);
    boolean isKnownChunk(BlockPos pos);

    IIslandChunk getIslandChunk(int chunkX, int chunkZ);
    IIslandChunk getIslandChunk(BlockPos pos);

}
