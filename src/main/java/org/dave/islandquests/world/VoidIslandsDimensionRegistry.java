package org.dave.islandquests.world;

import net.minecraft.world.World;

import java.util.HashMap;

public class VoidIslandsDimensionRegistry {
    public static VoidIslandsDimensionRegistry instance = new VoidIslandsDimensionRegistry();

    private HashMap<Integer, VoidIslandsTerrainGenerator> terrainGenMap = new HashMap<>();

    public void registerTerrainGen(World world, VoidIslandsTerrainGenerator gen) {
        registerTerrainGen(world.provider.getDimension(), gen);
    }

    public void registerTerrainGen(int dimensionId, VoidIslandsTerrainGenerator gen) {
        terrainGenMap.put(dimensionId, gen);
    }

    public VoidIslandsTerrainGenerator getTerrainGenerator(World world) {
        return terrainGenMap.get(world.provider.getDimension());
    }

    public VoidIslandsTerrainGenerator getTerrainGenerator(int dimensionId) {
        return terrainGenMap.get(dimensionId);
    }
}
