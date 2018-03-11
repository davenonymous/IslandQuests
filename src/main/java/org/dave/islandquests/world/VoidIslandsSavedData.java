package org.dave.islandquests.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.islandquests.islands.IslandRegistry;
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

        Logz.info("Clearing island registry for next world");
        IslandRegistry.instance.init();
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
        NBTTagList list = nbt.getTagList("islands", Constants.NBT.TAG_COMPOUND);

        if(list != null) {
            IslandRegistry.instance.loadFromTagList(list);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("islands", IslandRegistry.instance.createTagList());
        return compound;
    }
}
