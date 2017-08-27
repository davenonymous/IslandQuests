package org.dave.islandquests.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.islandquests.islands.IslandChunkRegistry;
import org.dave.islandquests.utility.Logz;

public class VoidIslandsSavedData extends WorldSavedData {
    public static VoidIslandsSavedData INSTANCE;

    public VoidIslandsSavedData(String name) {
        super(name);
    }

    @SubscribeEvent
    public static void unloadWorld(WorldEvent.Unload event) {
        if(event.getWorld().provider.getDimension() != 0) {
            return;
        }

        Logz.info("Clearing island chunk registry for next world");
        IslandChunkRegistry.init();
    }

    @SubscribeEvent
    public static void loadWorld(WorldEvent.Load event) {
        if(event.getWorld().isRemote || !(event.getWorld().provider.getDimension() != 0)) {
            return;
        }

        VoidIslandsSavedData visd = (VoidIslandsSavedData)event.getWorld().getMapStorage().getOrLoadData(VoidIslandsSavedData.class, "VoidIslandsSavedData");
        if(visd == null) {
            visd = new VoidIslandsSavedData("VoidIslandsSavedData");
            visd.markDirty();
        }

        INSTANCE = visd;
        event.getWorld().getMapStorage().setData("VoidIslandsSavedData", visd);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("islandchunks", 10);

        if(list != null) {
            IslandChunkRegistry.loadFromTagList(list);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("islandchunks", IslandChunkRegistry.createTagList());

        return compound;
    }
}
