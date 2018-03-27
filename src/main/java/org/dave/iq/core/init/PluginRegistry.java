package org.dave.iq.core.init;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.iq.api.IIslandQuestsPlugin;
import org.dave.iq.api.IslandQuestsPlugin;
import org.dave.iq.core.utility.Logz;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginRegistry {
    public static ASMDataTable asmData;
    private static List<IIslandQuestsPlugin> cachedPluginList = null;

    private PluginRegistry() {
    }

    public static List<IIslandQuestsPlugin> getIQPlugins() {
        if(cachedPluginList == null) {
            cachedPluginList = getInstances(asmData, IslandQuestsPlugin.class, IIslandQuestsPlugin.class);
        }

        return cachedPluginList;
    }

    private static <T> List<T> getInstances(ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        List<T> instances = new ArrayList<T>();
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Map<String, Object> annotationInfo = asmData.getAnnotationInfo();
                if (annotationInfo.containsKey("mod")) {
                    String requiredMod = (String) annotationInfo.get("mod");
                    if (requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                        continue;
                    }
                }

                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch (ClassNotFoundException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (IllegalAccessException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (InstantiationException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (ExceptionInInitializerError e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }
        return instances;
    }

    public static void setAsmData(ASMDataTable asmData) {
        PluginRegistry.asmData = asmData;
    }
}
