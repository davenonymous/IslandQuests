package org.dave.iq.api;

public interface IIslandNoiseMap {
    boolean isIsland(int x, int z);

    double getNoise(int x, int z);
    double getHeightNoise(int x, int z);

    double getLowThreshold();
    double getHighTreshold();
}
