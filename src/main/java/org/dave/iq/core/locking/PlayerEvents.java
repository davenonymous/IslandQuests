package org.dave.iq.core.locking;


import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.dave.iq.api.IIslandType;
import org.dave.iq.core.client.ClientInfo;
import org.dave.iq.core.islands.*;
import org.dave.iq.core.network.MessageClientInfo;
import org.dave.iq.core.network.PackageHandler;
import org.dave.iq.core.utility.Logz;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlayerEvents {
    private static Map<EntityPlayer, Island> prevIslands = new HashMap<>();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        //event.setCanceled(true);
        //Logz.info("Player right clicked block: %s", state.getBlock().getUnlocalizedName());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(!(event.player instanceof EntityPlayerMP)) {
            return;
        }

        Island previous = prevIslands.get(event.player);
        int chunkX = (int)event.player.posX >> 4;
        int chunkZ = (int)event.player.posZ >> 4;

        Island island = IslandRegistry.instance.getIsland(chunkX, chunkZ);

        if(island != previous) {
            prevIslands.put(event.player, island);

            PackageHandler.instance.sendTo(new MessageClientInfo(island), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {

            Island island = ClientInfo.instance.getCurrentIsland();

            if(island != null) {
                IIslandType islandType = island.getIslandType();
                String startIslandText = "";
                if(island.isStartingIsland()) {
                    startIslandText += " (Spawn Island)";
                }

                if(!island.allChunksGenerated()) {
                    startIslandText += String.format(Locale.ENGLISH, " (Generating: %.1f%%)", island.getGeneratedChunkRatio() * 100);
                }

                String iqDebugText = String.format("Island: %s (Height: %d)%s", islandType.getName(), islandType.getMinimumYLevel() + island.getHeightOffset(), startIslandText);
                event.getLeft().add(iqDebugText);
            }
        }
    }
}
