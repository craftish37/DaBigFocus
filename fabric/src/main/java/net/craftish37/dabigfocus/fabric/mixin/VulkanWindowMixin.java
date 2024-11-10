package net.craftish37.dabigfocus.fabric.mixin;

import com.mojang.blaze3d.platform.Window;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.fabric.DaBigFocusFabricCaching;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Window.class, priority = 2000)
public abstract class VulkanWindowMixin {

    @Shadow
    private boolean fullscreen;

    @Inject(method = "onMove", at = @At("HEAD"))
    private void inject$onMove$head(long window, int x, int y, CallbackInfo ci) {
        if (!this.fullscreen) {
            if (!DaBigFocusFabricCaching.cachedPos) {
                DaBigFocusConstants.LOGGER.info("Window position has been cached");
            }
            DaBigFocusFabricCaching.cachedPos = true;
            DaBigFocusFabricCaching.cachedX = x;
            DaBigFocusFabricCaching.cachedY = y;
        }
    }

    @Inject(method = "onResize", at = @At("HEAD"))
    private void inject$onResize$head(long window, int width, int height, CallbackInfo ci) {
        if (!this.fullscreen && !DaBigFocusFabricCaching.cacheSizeLock) {
            if (!DaBigFocusFabricCaching.cachedSize) {
                DaBigFocusConstants.LOGGER.info("Window size has been cached");
            }
            DaBigFocusFabricCaching.cachedSize = true;
            DaBigFocusFabricCaching.cachedWidth = width;
            DaBigFocusFabricCaching.cachedHeight = height;
        }
    }

}