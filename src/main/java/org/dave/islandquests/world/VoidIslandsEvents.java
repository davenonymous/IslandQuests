package org.dave.islandquests.world;


import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.islandquests.islands.IslandChunkRegistry;
import org.dave.islandquests.islands.IslandType;
import org.dave.islandquests.utility.Logz;

public class VoidIslandsEvents {
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

        if(!IslandChunkRegistry.isKnownChunk(event.getPos())) {
            Logz.info("Decorating biome of unknown island chunk");
            return;
        }

        IslandType type = IslandChunkRegistry.getIslandType(event.getPos());
        if(type == null) {
            Logz.info("No island type for biome decoration");
            return;
        }

        if(type.blockedDecorationTypes.contains(event.getType())) {
            event.setResult(Event.Result.DENY);
        }
    }


}
