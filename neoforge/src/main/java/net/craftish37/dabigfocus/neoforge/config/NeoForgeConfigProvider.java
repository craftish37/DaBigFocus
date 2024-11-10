package net.craftish37.dabigfocus.neoforge.config;

import net.craftish37.dabigfocus.config.Config;
import net.craftish37.dabigfocus.config.ConfigProvider;

public class NeoForgeConfigProvider implements ConfigProvider {

    @Override
    public Config ensureLoaded() {
        return DaBigFocusConfigNeoForge.ensureLoaded();
    }

}
