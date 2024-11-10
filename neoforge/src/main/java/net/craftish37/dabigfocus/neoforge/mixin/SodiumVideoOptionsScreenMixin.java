package net.craftish37.dabigfocus.neoforge.mixin;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.craftish37.dabigfocus.neoforge.config.DaBigFocusConfigNeoForge;
import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.FullscreenMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(SodiumGameOptionPages.class)
public class SodiumVideoOptionsScreenMixin {

    @Shadow(remap = false)
    @Final
    private static MinecraftOptionsStorage vanillaOpts;

    @ModifyArg(method = "general", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/gui/options/OptionPage;<init>(Lnet/minecraft/network/chat/Component;Lcom/google/common/collect/ImmutableList;)V"), index = 1)
    private static ImmutableList<OptionGroup> inject$general(ImmutableList<OptionGroup> groups) {
        List<OptionGroup> newGroups = new ArrayList<>();

        for (OptionGroup group : groups) {
            OptionGroup.Builder builder = OptionGroup.createBuilder();
            for (Option<?> option : group.getOptions()) {
                if (option.getName().getContents() instanceof TranslatableContents translatableContents) {
                    if (translatableContents.getKey().equals("options.fullscreen")) {
                        builder.add(
                                OptionImpl.createBuilder(FullscreenMode.class, vanillaOpts)
                                        .setName(Component.translatable("dabigfocus.option.fullscreen_mode"))
                                        .setTooltip(Component.translatable("dabigfocus.option.fullscreen_mode.tooltip"))
                                        .setControl((opt) -> new CyclingControl<>(opt, FullscreenMode.class, new Component[]{
                                                Component.translatable("dabigfocus.option.fullscreen_mode.exclusive"),
                                                Component.translatable("dabigfocus.option.fullscreen_mode.native"),
                                                Component.translatable("dabigfocus.option.fullscreen_mode.borderless")
                                        }))
                                        .setBinding((options, value) -> {
                                                    DaBigFocusConfigNeoForge.FULLSCREEN.set(value);
                                                    DaBigFocusConfigNeoForge.SPECS.save();
                                                    if (options.fullscreen().get()) {
                                                        // If fullscreen turns on, re-toggle to changing the fullscreen mode instantly
                                                        DaBigFocus.toggleFullScreenMode(options, true);
                                                    }
                                                },
                                                (options) -> DaBigFocusConfigNeoForge.FULLSCREEN.get()
                                        )
                                        .build()
                        ).add(
                                OptionImpl.createBuilder(boolean.class, vanillaOpts)
                                        .setName(Component.translatable("options.fullscreen"))
                                        .setTooltip(Component.translatable("sodium.options.fullscreen.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding(DaBigFocus::toggleFullScreenMode, (options) -> options.fullscreen().get())
                                        .build()
                        );
                        continue;
                    }
                }
                builder.add(option);
            }
            newGroups.add(builder.build());
        }

        return ImmutableList.copyOf(newGroups);
    }

}
