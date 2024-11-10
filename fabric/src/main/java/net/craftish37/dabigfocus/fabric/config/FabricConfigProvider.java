package net.craftish37.dabigfocus.fabric.config;

import net.craftish37.dabigfocus.config.Config;
import net.craftish37.dabigfocus.config.ConfigProvider;

public class FabricConfigProvider implements ConfigProvider {

    @Override
    public Config ensureLoaded() {
        return DaBigFocusConfigFabric.getInstance();
    }

}
