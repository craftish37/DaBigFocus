package net.craftish37.dabigfocus.neoforge.compat;

import net.craftish37.dabigfocus.neoforge.config.DaBigFocusConfigNeoForge;
import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.FullscreenMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.embeddium.api.OptionGroupConstructionEvent;
import org.embeddedt.embeddium.api.options.control.CyclingControl;
import org.embeddedt.embeddium.api.options.control.TickBoxControl;
import org.embeddedt.embeddium.api.options.storage.MinecraftOptionsStorage;
import org.embeddedt.embeddium.api.options.structure.OptionImpl;
import org.embeddedt.embeddium.api.options.structure.StandardOptions;

public class EmbeddiumCompat {

    public static void init() {
        OptionGroupConstructionEvent.BUS.addListener(event -> {
            if (event.getId() != null && event.getId().toString().equals(StandardOptions.Group.WINDOW.toString())) {
                var options = event.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).getId().toString().equals(StandardOptions.Option.FULLSCREEN.toString())) {
                        options.add(i, OptionImpl.createBuilder(FullscreenMode.class, MinecraftOptionsStorage.INSTANCE)
                                .setId(ResourceLocation.fromNamespaceAndPath(DaBigFocusConstants.MOD_ID, "fullscreen_mode"))
                                .setName(Component.translatable("dabigfocus.option.fullscreen_mode"))
                                .setTooltip(Component.translatable("dabigfocus.option.fullscreen_mode.tooltip"))
                                .setControl((opt) -> new CyclingControl<>(opt, FullscreenMode.class, new Component[]{
                                        Component.translatable("dabigfocus.option.fullscreen_mode.exclusive"),
                                        Component.translatable("dabigfocus.option.fullscreen_mode.native"),
                                        Component.translatable("dabigfocus.option.fullscreen_mode.borderless")
                                }))
                                .setBinding((vanillaOpts, value) -> {
                                            DaBigFocusConfigNeoForge.ensureLoaded().setFullscreenMode(value);
                                            DaBigFocusConfigNeoForge.ensureLoaded().save();
                                            if (vanillaOpts.fullscreen().get()) {
                                                // If fullscreen turns on, re-toggle to changing the fullscreen mode instantly
                                                DaBigFocus.toggleFullScreenMode(vanillaOpts, true);
                                            }
                                        },
                                        (vanillaOpts) -> DaBigFocusConfigNeoForge.ensureLoaded().getFullscreenMode()
                                )
                                .build());
                        options.set(
                                i + 1,
                                OptionImpl.createBuilder(Boolean.TYPE, MinecraftOptionsStorage.INSTANCE)
                                        .setId(StandardOptions.Option.FULLSCREEN)
                                        .setName(Component.translatable("options.fullscreen"))
                                        .setTooltip(Component.translatable("sodium.options.fullscreen.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding(DaBigFocus::toggleFullScreenMode, (opts) -> opts.fullscreen().get()).build()
                        );
                        break;
                    }
                }
            }
        });
    }

}
