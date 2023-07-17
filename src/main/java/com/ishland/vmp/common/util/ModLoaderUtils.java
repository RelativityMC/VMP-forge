package com.ishland.vmp.common.util;

import net.minecraftforge.fml.loading.FMLLoader;

public class ModLoaderUtils {

    public static boolean isModLoaded(String modid) {
        return FMLLoader.getLoadingModList().getMods().stream().anyMatch(info -> info.getModId().equals(modid));
    }

}
