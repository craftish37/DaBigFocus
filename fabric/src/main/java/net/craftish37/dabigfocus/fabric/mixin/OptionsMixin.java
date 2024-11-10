package net.craftish37.dabigfocus.fabric.mixin;

import net.craftish37.dabigfocus.fabric.config.DaBigFocusConfigFabric;
import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.FullscreenMode;
import net.minecraft.network.chat.Component;
import net.vulkanmod.config.option.CyclingOption;
import net.vulkanmod.config.option.Option;
import net.vulkanmod.config.option.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Options.class)
public class OptionsMixin {

    @Shadow
    static net.minecraft.client.Options minecraftOptions;

    @SuppressWarnings("unchecked")
    @ModifyArgs(method = "getVideoOpts", remap = false, at = @At(value = "INVOKE", target = "Lnet/vulkanmod/config/gui/OptionBlock;<init>(Ljava/lang/String;[Lnet/vulkanmod/config/option/Option;)V"))
    private static void modifyArg$getVideoOpts(Args args) {
        Option<?>[] options = args.get(1);
        if (options.length == 6) {
            Option<?>[] newOptions = new Option<?>[6];
            newOptions[0] = options[0];
            newOptions[1] = options[1];
            newOptions[2] = options[2];
            newOptions[3] = new CyclingOption<>(
                    Component.translatable("dabigfocus.option.fullscreen_mode"),
                    FullscreenMode.values(),
                    value -> {
                        DaBigFocusConfigFabric.getInstance().fullscreen = value;
                        DaBigFocusConfigFabric.getInstance().save();
                        if (minecraftOptions.fullscreen().get()) {
                            DaBigFocus.toggleFullScreenMode(minecraftOptions, true);
                        }
                    },
                    () -> DaBigFocusConfigFabric.getInstance().fullscreen
            ).setTranslator(fullscreenMode -> Component.translatable(fullscreenMode.getKey()));
            newOptions[4] = options[4];
            newOptions[5] = options[5];

            Option<Boolean> fullscreenOption = (Option<Boolean>) newOptions[2];
            fullscreenOption.setOnApply(value -> {
                DaBigFocus.toggleFullScreenMode(minecraftOptions, value);
            });

            args.set(1, newOptions);
        }
    }

}