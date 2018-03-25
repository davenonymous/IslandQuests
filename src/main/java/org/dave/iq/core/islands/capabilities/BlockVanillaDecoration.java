package org.dave.iq.core.islands.capabilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import org.dave.iq.api.IIslandCapability;
import org.dave.iq.api.capabilities.IBlockVanillaDecoration;

import java.util.HashSet;
import java.util.Set;

public class BlockVanillaDecoration implements IBlockVanillaDecoration, IIslandCapability {
    private Set<DecorateBiomeEvent.Decorate.EventType> blockedDecorations = new HashSet<>();

    public BlockVanillaDecoration() {
    }

    public void addDecorationToBlockList(DecorateBiomeEvent.Decorate.EventType decorationType) {
        blockedDecorations.add(decorationType);
    }

    @Override
    public boolean isTypeBlocked(DecorateBiomeEvent.Decorate.EventType decorationType) {
        return blockedDecorations.contains(decorationType);
    }

    @Override
    public void readJsonData(JsonObject data) {
        if(!data.has("blocked-types") || !data.get("blocked-types").isJsonArray()) {
            return;
        }

        for(JsonElement element : data.getAsJsonArray("blocked-types")) {
            if(!element.isJsonPrimitive()) {
                continue;
            }

            String blockedType = element.getAsString();
            DecorateBiomeEvent.Decorate.EventType type = DecorateBiomeEvent.Decorate.EventType.valueOf(blockedType);
            if(type != null) {
                blockedDecorations.add(type);
            }
        }
    }
}
