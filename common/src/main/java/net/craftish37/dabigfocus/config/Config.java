package net.craftish37.dabigfocus.config;

import net.craftish37.dabigfocus.FullscreenMode;

public interface Config {

    boolean customized();

    FullscreenMode getFullscreenMode();

    void setFullscreenMode(FullscreenMode fullscreenMode);

    void save();

    static boolean isExclusive() {
        return ConfigProvider.INSTANCE.ensureLoaded().getFullscreenMode() == FullscreenMode.EXCLUSIVE;
    }

    static boolean isBorderless() {
        return ConfigProvider.INSTANCE.ensureLoaded().getFullscreenMode() == FullscreenMode.BORDERLESS;
    }

    static boolean isCustomized() {
        return ConfigProvider.INSTANCE.ensureLoaded().customized();
    }

}
