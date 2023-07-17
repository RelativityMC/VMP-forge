package com.ishland.vmp;

import com.ishland.vmp.common.config.Config;
import com.ishland.vmp.common.playerwatching.NearbyEntityTracking;
import net.minecraftforge.fml.common.Mod;

@Mod("vmp")
public class VMPMod {
    public VMPMod() {
        if (Config.USE_OPTIMIZED_ENTITY_TRACKING) {
            NearbyEntityTracking.init();
        }
    }
}
