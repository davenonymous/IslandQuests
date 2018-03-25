package org.dave.iq.core.islands;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.iq.api.IIslandCapability;
import org.dave.iq.core.utility.Logz;

import java.lang.reflect.Type;

public class IslandTypeJsonLoader {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(IslandType.class, new IslandTypeSerializer())
            .create();

    private static class IslandTypeSerializer implements JsonDeserializer<IslandType> {
        @Override
        public IslandType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                Logz.info("Invalid island type! Not a json object!");
                return null;
            }


            JsonObject jsonRoot = json.getAsJsonObject();

            // First we check whether we have all the essential information we need
            if(!jsonRoot.has("name") || !jsonRoot.get("name").isJsonPrimitive()) {
                Logz.info("Invalid island type! Missing island type name!");
                return null;
            }

            if(!jsonRoot.has("biome") || !jsonRoot.get("biome").isJsonPrimitive()) {
                Logz.info("Invalid island type: Missing biome!");
                return null;
            }

            // Then we can grab all the primitives for later use
            String name = jsonRoot.get("name").getAsString();
            String biomeName = jsonRoot.get("biome").getAsString();

            if(jsonRoot.has("mods") && jsonRoot.get("mods").isJsonArray()) {
                for(JsonElement element : jsonRoot.getAsJsonArray("mods")) {
                    if(!element.isJsonPrimitive()) {
                        continue;
                    }

                    String mod = element.getAsString();
                    if (mod.length() > 0 && !Loader.isModLoaded(mod)) {
                        Logz.info("Not loading island type '%s'! Required mod '%s' not loaded!", name, mod);
                        return null;
                    }
                }
            }

            IslandType islandType = new IslandType(name);
            islandType.setBiome(biomeName);

            if(jsonRoot.has("weight") && jsonRoot.get("weight").isJsonPrimitive()) {
                islandType.setWeight(jsonRoot.get("weight").getAsInt());
            }

            if(jsonRoot.has("y-level") && jsonRoot.get("y-level").isJsonPrimitive()) {
                islandType.setMinimumYLevel(jsonRoot.get("y-level").getAsInt());
            }

            if(jsonRoot.has("y-range") && jsonRoot.get("y-range").isJsonPrimitive()) {
                islandType.setRangeYOffset(jsonRoot.get("y-range").getAsInt());
            }

            if(jsonRoot.has("hill-height") && jsonRoot.get("hill-height").isJsonPrimitive()) {
                islandType.setMaxHillHeight(jsonRoot.get("hill-height").getAsInt());
            }

            if(jsonRoot.has("floor-height") && jsonRoot.get("floor-height").isJsonPrimitive()) {
                islandType.setMaxFloorHeight(jsonRoot.get("floor-height").getAsInt());
            }

            if(jsonRoot.has("bedrock-edge") && jsonRoot.get("bedrock-edge").isJsonPrimitive()) {
                islandType.setEdgeBedrock(jsonRoot.get("bedrock-edge").getAsBoolean());
            }

            if(jsonRoot.has("blocks") && jsonRoot.get("blocks").isJsonObject()) {
                JsonObject blocks = jsonRoot.getAsJsonObject("blocks");

                if(blocks.has("filler") && blocks.get("filler").isJsonObject()) {
                    JsonObject block = blocks.getAsJsonObject("filler");
                    IBlockState state = getBlockStateFromJSON(block);
                    if(state != null) {
                        islandType.setFillerBlock(state);
                    }
                }

                if(blocks.has("top") && blocks.get("top").isJsonObject()) {
                    JsonObject block = blocks.getAsJsonObject("top");
                    IBlockState state = getBlockStateFromJSON(block);
                    if(state != null) {
                        islandType.setTopBlock(state);
                    }
                }

                if(blocks.has("bedrock") && blocks.get("bedrock").isJsonObject()) {
                    JsonObject block = blocks.getAsJsonObject("bedrock");
                    IBlockState state = getBlockStateFromJSON(block);
                    if(state != null) {
                        islandType.setBedrockBlock(state);
                    }
                }
            }

            if(jsonRoot.has("capabilities") && jsonRoot.get("capabilities").isJsonArray()) {
                for(JsonElement element : jsonRoot.getAsJsonArray("capabilities")) {
                    if(!element.isJsonObject()) {
                        Logz.warn("Invalid capability section: not a json object!");
                        continue;
                    }

                    JsonObject capJson = element.getAsJsonObject();
                    if(!capJson.has("capability") || !capJson.get("capability").isJsonPrimitive()) {
                        Logz.warn("Invalid capability section: missing 'capability' property!");
                        continue;
                    }

                    String capName = capJson.get("capability").getAsString();
                    IIslandCapability capability = IslandCapabilityRegistry.instance.getNewCapabilityInstance(capName);
                    if(capability != null) {
                        if (capJson.has("data") && capJson.get("data").isJsonObject()) {
                            JsonObject capData = capJson.getAsJsonObject("data");
                            capability.readJsonData(capData);
                        }
                        islandType.addCapability(capName, capability);
                    }
                }
            }

            return islandType;
        }

        private static IBlockState getBlockStateFromJSON(JsonObject block) {
            if(!block.has("name") || !block.get("name").isJsonPrimitive()) {
                return null;
            }
            String blockName = block.get("name").getAsString();
            int meta = 0;
            if(block.has("meta") && block.get("meta").isJsonPrimitive()) {
                meta = block.get("meta").getAsInt();
            }

            Block actualblock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
            if(actualblock == null) {
                Logz.warn("There is no '%s', can not set block", blockName);
                return null;
            }

            return actualblock.getStateFromMeta(meta);
        }
    }


}
