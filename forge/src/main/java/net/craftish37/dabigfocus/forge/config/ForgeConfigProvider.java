package net.craftish37.dabigfocus.forge.config;

import net.craftish37.dabigfocus.config.Config;
import net.craftish37.dabigfocus.config.ConfigProvider;

public class ForgeConfigProvider implements ConfigProvider {

    @Override
    public Config ensureLoaded() {
        return DaBigFocusConfigForge.ensureLoaded();
    }

}
