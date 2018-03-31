package org.dave.iq.api;

import net.minecraft.util.math.ChunkPos;

import java.util.Set;

public interface IIsland {
    IIslandType getIslandType();
    Set<ChunkPos> getChunks();
    int getHeightOffset();
    boolean isStartingIsland();
    double getAverageChunkNoise(ChunkPos chunkPos);
    IIslandChunk getIslandChunk(ChunkPos chunkPos);
}
