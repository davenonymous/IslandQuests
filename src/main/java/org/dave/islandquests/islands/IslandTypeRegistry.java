package org.dave.islandquests.islands;

import org.dave.islandquests.configuration.ConfigurationHandler;
import org.dave.islandquests.utility.Logz;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class IslandTypeRegistry {
    public static Map<String, IslandType> islandTypes;
    private static double totalWeight = 0.0;

    public static void init() {
        reload();
    }

    public static void reload() {
        islandTypes = new HashMap<>();

        if(!ConfigurationHandler.islandDir.exists()) {
            return;
        }

        for(File file : ConfigurationHandler.islandDir.listFiles()) {
            Logz.info(" > Loading island types from file: '%s'", file.getName());

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                engine.eval(new FileReader(file));
                Invocable invocable = (Invocable) engine;

                invocable.invokeFunction("main");
            } catch (ScriptException e) {
                Logz.warn("Could not compile+eval script=%s: %s", file.getName(), e);
                continue;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Logz.warn("Script %s is missing a method: %s", file.getName(), e);
            }
        }

        if(islandTypes.size() == 0) {
            Logz.warn("No island types registered. This is bad, the game will probably crash!");
        } else {
            Logz.info("Loaded %d island types.", islandTypes.size());
        }
    }

    public static void registerIslandType(IslandType type) {
        if(islandTypes.containsKey(type.name)) {
            Logz.warn("Overwriting island type: %s (weight=%.1f, biome=%s)", type.name, type.weight, type.biome.getRegistryName());
        } else {
            Logz.info("Registering island type: %s (weight=%.1f, biome=%s)", type.name, type.weight, type.biome.getRegistryName());
        }

        totalWeight += type.weight;
        islandTypes.put(type.name, type);
    }

    public static IslandType getRandomIslandType() {
        double treshold = Math.random() * totalWeight;
        double position = 0.0;
        for(IslandType type : islandTypes.values()) {
            position += type.weight;
            if(position >= treshold) {
                return type;
            }
        }

        return null;
    }

    public static IslandType getIslandType(String name) {
        return islandTypes.get(name);
    }
}