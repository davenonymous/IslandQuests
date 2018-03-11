package org.dave.islandquests.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import org.dave.islandquests.configuration.WorldGenSettings;
import org.dave.islandquests.islands.IslandChunk;
import org.dave.islandquests.islands.IslandChunkRegistry;
import org.dave.islandquests.islands.IslandType;
import org.dave.islandquests.islands.IslandTypeRegistry;
import org.dave.islandquests.utility.Logz;
import org.dave.islandquests.utility.OpenSimplexNoise;

import java.util.Random;

public class VoidIslandsTerrainGenerator {
    private final World world;
    private final VoidIslandsChunkGenerator provider;

    private final OpenSimplexNoise noise;
    private final OpenSimplexNoise noiseHeight;

    private final Random rand;

    public VoidIslandsTerrainGenerator(World world, VoidIslandsChunkGenerator provider) {
        this.world = world;
        this.provider = provider;

        // TODO: We should be able to reuse the worlds rand
        this.rand = new Random(this.world.getSeed());
        this.noise = new OpenSimplexNoise(rand.nextLong());
        this.noiseHeight = new OpenSimplexNoise(rand.nextLong());
    }

    public boolean isVoid(int chunkX, int chunkZ) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int actualX = (chunkX*16) + x;
                int actualZ = (chunkZ*16) + z;

                double chance = this.noise.eval(actualX / WorldGenSettings.featureSize, actualZ / WorldGenSettings.featureSize);
                if(chance > WorldGenSettings.minimum) {
                    return false;
                }
            }
        }

        return true;
    }

    private void processNeighborChunks(int chunkX, int chunkZ, IslandType islandType, int heightOffset) {
        for(int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                if (z == 0 && x == 0) continue;
                if (isVoid(chunkX + x, chunkZ + z)) continue;

                IslandChunk chunk = IslandChunkRegistry.instance.getIslandChunk(chunkX + x, chunkZ + z);
                if (chunk.isProcessed()) continue;

                chunk.setIslandType(islandType);
                chunk.setProcessed(true);
                chunk.setHeightOffset(heightOffset);

                processNeighborChunks(chunkX + x, chunkZ + z, islandType, heightOffset);
            }
        }
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

        IslandChunk islandChunk;
        IslandType islandType;
        if(!IslandChunkRegistry.instance.isKnownChunk(chunkX, chunkZ)) {
            // First we need to *virtually* find all chunks that belong to the same island
            // and mark them all with the values needed for this island

            islandType = IslandTypeRegistry.instance.getRandomIslandType();
            islandChunk = IslandChunkRegistry.instance.getIslandChunk(chunkX, chunkZ);

            int heightOffset = islandType.getRandomYOffset(this.rand);
            islandChunk.setHeightOffset(heightOffset);
            islandChunk.setIslandType(islandType);
            islandChunk.setProcessed(true);

            processNeighborChunks(chunkX, chunkZ, islandType, heightOffset);

            if(VoidIslandsSavedData.INSTANCE != null) {
                VoidIslandsSavedData.INSTANCE.markDirty();
            }
        } else {
            islandChunk = IslandChunkRegistry.instance.getIslandChunk(chunkX, chunkZ);
            islandType = islandChunk.getIslandType();
        }


        IBlockState topBlock = islandType.getTopBlock();
        IBlockState fillerBlock = islandType.getFillerBlock();
        IBlockState bedrockBlock = islandType.getBedrockBlock();

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int actualX = (chunkX*16) + x;
                int actualZ = (chunkZ*16) + z;

                double chance = this.noise.eval(actualX / WorldGenSettings.featureSize, actualZ / WorldGenSettings.featureSize);

                if(chance > WorldGenSettings.minimum) {
                    double floorHeightRatio = MathHelper.clamp(chance, WorldGenSettings.minimum, WorldGenSettings.maximum);
                    floorHeightRatio -= WorldGenSettings.minimum;
                    floorHeightRatio *= (1.0d / (WorldGenSettings.maximum - WorldGenSettings.minimum));

                    double hillHeight = (this.noiseHeight.eval(actualX / 30.0D, actualZ / 30.0D) + 1.0);
                    double hillHeightRatio = hillHeight * floorHeightRatio;
                    int blockHillHeight = (int)Math.floor(WorldGenSettings.maxHillHeight * hillHeightRatio);

                    int heighestBlockY = islandType.minimumYLevel + islandChunk.getHeightOffset() + blockHillHeight;
                    primer.setBlockState(x, heighestBlockY, z, topBlock);

                    double blockFloorHeight = WorldGenSettings.maxFloorHeight * floorHeightRatio + blockHillHeight;
                    for(int y = 1; y < blockFloorHeight; y++) {
                        primer.setBlockState(x, heighestBlockY - y, z, fillerBlock);
                    }

                    if(bedrockBlock != null) {
                        primer.setBlockState(x, heighestBlockY - (int) blockFloorHeight - 1, z, bedrockBlock);
                    }

                    if(VoidIslandsEvents.isFirstSpawnPointCreation && chance > WorldGenSettings.minimum + 0.15f) {
                        VoidIslandsEvents.isFirstSpawnPointCreation = false;
                        Logz.info("Setting spawnpoint to: %s", new BlockPos(actualX, heighestBlockY+1, actualZ));
                        world.setSpawnPoint(new BlockPos(actualX, heighestBlockY+1, actualZ));
                    }
                }
            }
        }

        return true;
    }
}
