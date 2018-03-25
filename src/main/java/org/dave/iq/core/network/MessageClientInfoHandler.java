package org.dave.iq.core.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.dave.iq.core.client.ClientInfo;

public class MessageClientInfoHandler implements IMessageHandler<MessageClientInfo, MessageClientInfo> {
    @Override
    public MessageClientInfo onMessage(MessageClientInfo message, MessageContext ctx) {
        ClientInfo.instance.setCurrentIsland(message.island);
        return null;
    }
}
