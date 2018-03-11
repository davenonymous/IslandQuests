package org.dave.islandquests.world;


import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.islandquests.islands.Island;
import org.dave.islandquests.islands.IslandRegistry;
import org.dave.islandquests.islands.IslandType;

public class VoidIslandsEvents {
    public static boolean isFirstSpawnPointCreation = false;

    private static Tuple<Integer, Integer> getSpiralPosition(int offset) {
        if(offset == 0) {
            return new Tuple<>(0, 0);
        }

        --offset;

        int radius = (int)Math.floor((Math.sqrt(offset + 1) - 1.0d) / 2.0d) + 1;

        int p = (8 * radius * (radius-1)) / 2;
        int en = radius * 2;

        int a = (1 + offset - p) % (radius * 8);
        int b = a % en;

        Tuple<Integer, Integer> result = new Tuple<>(0, 0);
        switch ((int)Math.floor((float)a / (float)(radius*2))) {
            case 0: {
                result = new Tuple<>(a - radius, -radius);
                break;
            }

            case 1: {
                result = new Tuple<>(radius, b - radius);
                break;
            }

            case 2: {
                result = new Tuple<>(radius - b, radius);
                break;
            }

            case 3: {
                result = new Tuple<>(-radius, radius - b);
                break;
            }
        }

        return result;
    }

    @SubscribeEvent
    public static void onCreateSpawnPoint(WorldEvent.CreateSpawnPosition event) {
        World world = event.getWorld();
        if(world.isRemote) {
            return;
        }

        if(!(world.getWorldType() instanceof VoidIslandsWorldType)) {
            return;
        }

        VoidIslandsTerrainGenerator terrainGenerator = VoidIslandsDimensionRegistry.instance.getTerrainGenerator(world);

        int maxTries = 1000;
        int tryNum = 0;

        while(tryNum < maxTries) {
            Tuple<Integer, Integer> pos = getSpiralPosition(tryNum);
            if(!terrainGenerator.isVoid(pos.getFirst(), pos.getSecond())) {
                world.setSpawnPoint(new BlockPos(pos.getFirst() << 4, 100, pos.getSecond() << 4));
                event.setCanceled(true);
                break;
            }

            tryNum++;
        }

        isFirstSpawnPointCreation = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGenerateMineable(OreGenEvent.GenerateMinable event) {
        if(!(event.getWorld().getWorldType() instanceof VoidIslandsWorldType)) {
            return;
        }

        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPopulate(PopulateChunkEvent event) {
        if(!(event.getWorld().getWorldType() instanceof VoidIslandsWorldType)) {
            return;
        }

        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInitMapGen(InitMapGenEvent event) {
        event.setNewGen(null);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBiomeDecorate(DecorateBiomeEvent.Decorate event) {
        if (!(event.getWorld().getWorldType() instanceof VoidIslandsWorldType)) {
            return;
        }

        if(!IslandRegistry.instance.hasIsland(event.getPos())) {
            return;
        }

        Island island = IslandRegistry.instance.getIsland(event.getPos());
        IslandType islandType = island.getIslandType();

        if(islandType.blockedDecorationTypes.contains(event.getType())) {
            event.setResult(Event.Result.DENY);
        }
    }


}
