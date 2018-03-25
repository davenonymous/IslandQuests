package org.dave.iq.core.islands.capabilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dave.iq.api.IIslandCapability;
import org.dave.iq.api.capabilities.IReplaceBlocks;
import org.dave.iq.core.utility.Logz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReplaceBlocks implements IReplaceBlocks, IIslandCapability {
    private Map<BlockMatcher, Replacement> replacements = new HashMap<>();

    @Override
    public IBlockState getReplacement(IBlockState state, Random rand) {
        for (Map.Entry<BlockMatcher, Replacement> entry : replacements.entrySet()) {
            if(!entry.getKey().doesStateMatch(state)) {
                continue;
            }

            return entry.getValue().getRandomReplacement(rand);
        }

        return null;
    }

    @Override
    public double getChanceForReplacement(IBlockState state) {
        for (Map.Entry<BlockMatcher, Replacement> entry : replacements.entrySet()) {
            if (!entry.getKey().doesStateMatch(state)) {
                continue;
            }

            return entry.getKey().getChance();
        }

        return 0.0d;
    }

    @Override
    public void readJsonData(JsonObject data) {
        if(!data.has("rules") || !data.get("rules").isJsonArray()) {
            return;
        }

        for(JsonElement element : data.getAsJsonArray("rules")) {
            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject rule = element.getAsJsonObject();
            if (!rule.has("original") || !rule.get("original").isJsonObject()) {
                continue;
            }

            if (!rule.has("replacement") || !rule.get("replacement").isJsonObject()) {
                continue;
            }



            // Read in the original object
            JsonObject original = rule.getAsJsonObject("original");
            if (!original.has("name") || !original.get("name").isJsonPrimitive()) {
                continue;
            }
            String originalBlockName = original.get("name").getAsString();

            Block originalBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(originalBlockName));
            if(originalBlock == null) {
                Logz.warn("Invalid block specified in ReplaceBlocks decorator: %s", originalBlockName);
                continue;
            }

            BlockMatcher matcher = new BlockMatcher(originalBlock);
            if (original.has("meta") && original.get("meta").isJsonPrimitive()) {
                String asStr = original.get("meta").getAsString();
                if(asStr.equals("*")) {
                    matcher.setAllowAnyState(true);
                } else {
                    int asInt = original.get("meta").getAsInt();
                    matcher.setWantedMeta(asInt);
                }
            }

            double chance = 1.0d;
            if (rule.has("chance") && rule.get("chance").isJsonPrimitive()) {
                chance = rule.get("chance").getAsDouble();
            }
            matcher.setChance(chance);


            // Read in the replacement object
            JsonObject replacement = rule.getAsJsonObject("replacement");
            if (!original.has("name") || !original.get("name").isJsonPrimitive()) {
                continue;
            }
            String replacementBlockName = replacement.get("name").getAsString();

            Block replacementBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(replacementBlockName));
            if(replacementBlock == null) {
                Logz.warn("Invalid block specified in ReplaceBlocks decorator: %s", replacementBlockName);
                continue;
            }

            Replacement repl = new Replacement(replacementBlock);
            if (replacement.has("meta") && replacement.get("meta").isJsonPrimitive()) {
                repl.setMetasFromString(replacement.get("meta").getAsString());
            }

            this.replacements.put(matcher, repl);
        }

    }

    private static class Replacement {
        Block outputBlock;
        ArrayList<Integer> validMetas;

        public Replacement(Block outputBlock) {
            this.outputBlock = outputBlock;
            this.validMetas = new ArrayList<>();
        }

        public void setMetasFromString(String valid) {
            if(valid.matches("^\\d+-\\d+$")) {
                String[] parts = valid.split("-");
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                for(int i = start; i <= end; i++) {
                    this.validMetas.add(i);
                }
            } else if(valid.matches("^\\d+$")) {
                int value = Integer.parseInt(valid);
                this.validMetas.add(value);
            }
        }

        private int getRandomValidMeta(Random rand) {
            if(validMetas.size() == 0) {
                return 0;
            }

            return validMetas.get(rand.nextInt(validMetas.size()));
        }

        public IBlockState getRandomReplacement(Random rand) {
            return outputBlock.getStateFromMeta(getRandomValidMeta(rand));
        }
    }

    private static class BlockMatcher {
        private Block inputBlock;
        private boolean allowAnyState = false;
        private int wantedMeta = 0;
        private double chance = 1.0f;

        public BlockMatcher(Block inputBlock) {
            this.inputBlock = inputBlock;
        }

        public void setAllowAnyState(boolean allowAnyState) {
            this.allowAnyState = allowAnyState;
        }

        public void setWantedMeta(int wantedMeta) {
            this.wantedMeta = wantedMeta;
        }

        public double getChance() {
            return chance;
        }

        public void setChance(double chance) {
            this.chance = chance;
        }

        public boolean doesStateMatch(IBlockState state) {
            if(state.getBlock() != inputBlock) {
                return false;
            }

            if(allowAnyState) {
                return true;
            }

            int meta = state.getBlock().getMetaFromState(state);
            if(wantedMeta != meta) {
                return false;
            }

            return true;
        }
    }
}
