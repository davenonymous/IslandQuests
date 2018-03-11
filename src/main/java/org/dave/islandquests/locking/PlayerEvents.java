package org.dave.islandquests.locking;


import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.dave.islandquests.islands.*;
import org.dave.islandquests.utility.Logz;

public class PlayerEvents {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        event.setCanceled(true);
        Logz.info("Player right clicked block: %s", state.getBlock().getUnlocalizedName());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    }

    @SubscribeEvent
    public static void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {

            int chunkX = (int)Minecraft.getMinecraft().player.posX >> 4;
            int chunkZ = (int)Minecraft.getMinecraft().player.posZ >> 4;

            Island island = IslandRegistry.instance.getIsland(chunkX, chunkZ);
            IslandType islandType = island.getIslandType();

            if(islandType != null) {
                String iqDebugText = String.format("island=%s, biome=%s, height=%d", islandType.name, islandType.biome.getRegistryName(), islandType.minimumYLevel + island.getHeightOffset());
                event.getLeft().add(iqDebugText);
            }
        }
    }
}
