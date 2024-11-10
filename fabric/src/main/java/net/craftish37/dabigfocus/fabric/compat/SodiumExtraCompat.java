package net.craftish37.dabigfocus.fabric.compat;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.fabricmc.loader.api.FabricLoader;

public class SodiumExtraCompat {

    public static boolean checkMacReduceResolution() {
        if (!FabricLoader.getInstance().isModLoaded("sodium-extra"))
            return false;
        String os = System.getProperty("os.name").toLowerCase();
        if (!(os.contains("mac") || os.contains("darwin")))
            return false;
        return checkMacReduceResolution0();
    }

    private static boolean checkMacReduceResolution0() {
        return SodiumExtraClientMod.options().extraSettings.reduceResolutionOnMac;
    }

}
