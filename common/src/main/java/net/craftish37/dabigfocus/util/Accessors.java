package net.craftish37.dabigfocus.util;

import net.craftish37.dabigfocus.mixin.accessor.WindowAccessor;
import net.minecraft.client.Minecraft;

public final class Accessors {

    public static WindowAccessor window() {
        return ((WindowAccessor) (Object) Minecraft.getInstance().getWindow());
    }

    private Accessors() {
    }

}
