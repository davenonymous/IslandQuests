package org.dave.islandquests.api;

import net.minecraft.util.math.ChunkPos;

import java.util.List;

public interface IIsland {
    IIslandType getIslandType();
    List<ChunkPos> getIslandChunks();
    int getHeightOffset();
}
