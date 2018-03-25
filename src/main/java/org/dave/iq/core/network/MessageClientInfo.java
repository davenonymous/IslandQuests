package org.dave.iq.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.dave.iq.core.islands.Island;

public class MessageClientInfo implements IMessage {
    Island island;

    public MessageClientInfo() {
    }

    public MessageClientInfo(Island island) {
        this.island = island;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.readBoolean()) {
            NBTTagCompound islandTag = ByteBufUtils.readTag(buf);
            this.island = new Island(islandTag);
        } else {
            this.island = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(this.island != null) {
            buf.writeBoolean(true);
            ByteBufUtils.writeTag(buf, this.island.createTagCompound());
        } else {
            buf.writeBoolean(false);
        }
    }
}
