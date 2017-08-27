package org.dave.islandquests.world;


import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.islandquests.utility.Logz;

public class VoidIslandsEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGenerateMineable(OreGenEvent.GenerateMinable event) {
        if(!(event.getWorld().getWorldType() instanceof VoidIslandsWorldType)) {
            return;
        }

        if(event.isCancelable()) {
            Logz.info("Cancelled ore gen event: generator=%s", event.getGenerator());
            event.setCanceled(true);
        }
    }
}
