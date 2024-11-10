package net.craftish37.dabigfocus.mixin;

import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.ConfigProvider;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin extends OptionsSubScreen {

    public VideoSettingsScreenMixin(Screen parent, Options options, Component component) {
        super(parent, options, component);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void inject$removed(CallbackInfo ci) {
        this.options.save(); // fix that the options won't save when exit options screen by pressing ESC
    }

    @Inject(method = "options", at = @At("HEAD"), cancellable = true)
    private static void inject$options(Options options, CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        cir.setReturnValue(
                new OptionInstance[]{
                        options.graphicsMode(),
                        options.renderDistance(),
                        options.prioritizeChunkUpdates(),
                        options.simulationDistance(),
                        options.ambientOcclusion(),
                        options.framerateLimit(),
                        options.enableVsync(),
                        options.bobView(),
                        options.guiScale(),
                        options.attackIndicator(),
                        options.gamma(),
                        options.cloudStatus(),
                        dabigfocus$FullscreenMode(options),
                        dabigfocus$wrapperFullscreen(options),
                        options.particles(),
                        options.mipmapLevels(),
                        options.entityShadows(),
                        options.screenEffectScale(),
                        options.entityDistanceScaling(),
                        options.fovEffectScale(),
                        options.showAutosaveIndicator(),
                        options.glintSpeed(),
                        options.glintStrength(),
                        options.menuBackgroundBlurriness()
                }
        );
    }

    @Unique
    private static OptionInstance<FullscreenMode> dabigfocus$FullscreenMode(Options options) {
        return new OptionInstance<>(
                "dabigfocus.option.fullscreen_mode",
                OptionInstance.noTooltip(),
                OptionInstance.forOptionEnum(),
                new OptionInstance.Enum<>(Arrays.asList(FullscreenMode.values()), FullscreenMode.CODEC),
                ConfigProvider.INSTANCE.ensureLoaded().getFullscreenMode(),
                fullscreenMode -> {
                    ConfigProvider.INSTANCE.ensureLoaded().setFullscreenMode(fullscreenMode);
                    ConfigProvider.INSTANCE.ensureLoaded().save();
                    if (options.fullscreen().get()) {
                        DaBigFocus.toggleFullScreenMode(options, true);
                    }
                });
    }

    @Unique
    private static OptionInstance<Boolean> dabigfocus$wrapperFullscreen(Options options) {
        return OptionInstance.createBoolean(
                "options.fullscreen",
                options.fullscreen().get(),
                (value) -> DaBigFocus.toggleFullScreenMode(options, value)
        );
    }

    @Shadow
    protected abstract void addOptions();

}
