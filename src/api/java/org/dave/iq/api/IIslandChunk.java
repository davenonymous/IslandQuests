package org.dave.iq.api;

import net.minecraft.util.math.ChunkPos;

public interface IIslandChunk {
    ChunkPos getPosition();
    boolean isGenerated();
    double getAverageNoise();
}
