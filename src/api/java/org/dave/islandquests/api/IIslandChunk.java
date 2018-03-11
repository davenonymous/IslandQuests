package org.dave.islandquests.api;

import org.dave.islandquests.islands.IslandType;

public interface IIslandChunk {
    IslandType getIslandType();
    int getHeightOffset();
    int getChunkX();
    int getChunkZ();
    boolean isProcessed();
}
