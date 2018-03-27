package org.dave.iq.core.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import org.dave.iq.api.IIslandType;
import org.dave.iq.core.configuration.ConfigurationHandler;
import org.dave.iq.core.islands.Island;
import org.dave.iq.core.islands.IslandRegistry;
import org.dave.iq.core.islands.IslandTypeRegistry;
import org.dave.iq.core.utility.Logz;

import java.util.ArrayList;
import java.util.List;

public class VoidIslandsTerrainGenerator {
    private final World world;
    private final VoidIslandsChunkGenerator provider;


    public VoidIslandsTerrainGenerator(World world, VoidIslandsChunkGenerator provider) {
        this.world = world;
        this.provider = provider;

        VoidIslandsNoise.instance.init(world);
    }

    public boolean isVoid(int chunkX, int chunkZ) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int actualX = (chunkX*16) + x;
                int actualZ = (chunkZ*16) + z;

                if(VoidIslandsNoise.instance.isIsland(actualX, actualZ)) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<ChunkPos> addConnectedChunksToList(List<ChunkPos> result, int chunkX, int chunkZ) {
        for(int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                // Don't check the block we are currently looking at, check the neighbors only
                if (z == 0 && x == 0) continue;

                // Void chunks are island borders, skip
                if (isVoid(chunkX + x, chunkZ + z)) continue;

                // There already is a known island on that chunk, skip
                if(IslandRegistry.instance.hasIsland(chunkX, chunkZ)) {
                    continue;
                }

                ChunkPos chunkPos = new ChunkPos(chunkX + x, chunkZ + z);

                // Do not process the same chunk twice
                if(result.contains(chunkPos)) {
                    continue;
                }

                result.add(chunkPos);
                addConnectedChunksToList(result, chunkPos.x, chunkPos.z);
            }
        }

        return result;
    }

    /**
     * Returns true when the chunk is part of an island
     *
     * @param chunkX
     * @param chunkZ
     * @param primer
     * @return
     */
    public boolean generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        if(isVoid(chunkX, chunkZ)) {
            return false;
        }

        Island island;
        IIslandType islandType;
        if(!IslandRegistry.instance.hasIsland(chunkX, chunkZ)) {
            islandType = IslandTypeRegistry.instance.getRandomIslandType(world.rand);
            int heightOffset = world.rand.nextInt(islandType.getIslandHeightOffsetRange());

            island = new Island(islandType, heightOffset);

            island.setChunks(addConnectedChunksToList(new ArrayList<>(), chunkX, chunkZ));

            IslandRegistry.instance.registerNewIsland(island);

            if(VoidIslandsSavedData.INSTANCE != null) {
                VoidIslandsSavedData.INSTANCE.markDirty();
            }
        } else {
            island = IslandRegistry.instance.getIsland(chunkX, chunkZ);
            islandType = island.getIslandType();
        }

        IBlockState topBlock = islandType.getTopBlock();
        IBlockState fillerBlock = islandType.getFillerBlock();
        IBlockState bedrockBlock = islandType.getBedrockBlock();

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int actualX = (chunkX*16) + x;
                int actualZ = (chunkZ*16) + z;

                double chance = VoidIslandsNoise.instance.getNoise(actualX, actualZ);

                if(chance > ConfigurationHandler.WorldGenSettings.minimum) {
                    double floorHeightRatio = MathHelper.clamp(chance, ConfigurationHandler.WorldGenSettings.minimum, ConfigurationHandler.WorldGenSettings.maximum);
                    floorHeightRatio -= ConfigurationHandler.WorldGenSettings.minimum;
                    floorHeightRatio *= (1.0d / (ConfigurationHandler.WorldGenSettings.maximum - ConfigurationHandler.WorldGenSettings.minimum));


                    double hillHeight = (VoidIslandsNoise.instance.getHeightNoise(actualX, actualZ) + 1.0);
                    double hillHeightRatio = hillHeight * floorHeightRatio;
                    int blockHillHeight = (int)Math.floor(islandType.getMaxHillHeight() * hillHeightRatio);

                    int heighestBlockY = islandType.getMinimumYLevel() + island.getHeightOffset() + blockHillHeight;
                    primer.setBlockState(x, heighestBlockY, z, topBlock);

                    double blockFloorHeight = islandType.getMaxFloorHeight() * floorHeightRatio + blockHillHeight;
                    for(int y = 1; y < blockFloorHeight; y++) {
                        primer.setBlockState(x, heighestBlockY - y, z, fillerBlock);
                    }

                    int bedrockYLevel = heighestBlockY - (int) blockFloorHeight;
                    if(islandType.isEdgeBedrock()) {
                        primer.setBlockState(x, bedrockYLevel, z, bedrockBlock);
                    } else {
                        primer.setBlockState(x, bedrockYLevel-1, z, bedrockBlock);
                    }

                    if(VoidIslandsEvents.isFirstSpawnPointCreation && chance > ConfigurationHandler.WorldGenSettings.minimum + 0.15f) {
                        VoidIslandsEvents.isFirstSpawnPointCreation = false;
                        Logz.info("Setting spawnpoint to: %s", new BlockPos(actualX, heighestBlockY+1, actualZ));
                        world.setSpawnPoint(new BlockPos(actualX, heighestBlockY+1, actualZ));
                        island.setStartingIsland(true);
                    }
                }
            }
        }

        return true;
    }
}
