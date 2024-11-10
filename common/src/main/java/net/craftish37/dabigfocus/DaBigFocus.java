package net.craftish37.dabigfocus;

import com.mojang.blaze3d.platform.Window;
import net.craftish37.dabigfocus.mixin.accessor.WindowAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public class DaBigFocus {

    public static void init() {
        DaBigFocusConstants.LOGGER.info("Time to focus!");
    }

    public static void toggleFullScreenMode(Options options, boolean value) {
        options.fullscreen().set(value);

        Window window = Minecraft.getInstance().getWindow();

        if (window.isFullscreen() != options.fullscreen().get()) {
            window.toggleFullScreen();
            options.fullscreen().set(window.isFullscreen());
        }

        if (options.fullscreen().get()) {
            ((WindowAccessor) (Object) window).setDirty(true);
            window.changeFullscreenVideoMode();
        }
    }

}