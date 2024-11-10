package net.craftish37.dabigfocus.neoforge;

import net.craftish37.dabigfocus.neoforge.compat.EmbeddiumCompat;
import net.craftish37.dabigfocus.neoforge.config.DaBigFocusConfigNeoForge;
import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.config.DaBigFocusConfigScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(value = DaBigFocusConstants.MOD_ID, dist = Dist.CLIENT)
public class DaBigFocusNeoForge {

    public DaBigFocusNeoForge(ModContainer container, IEventBus eventBus) {
        DaBigFocus.init();

        container.registerConfig(ModConfig.Type.CLIENT, DaBigFocusConfigNeoForge.SPECS, "dabigfocus-client.toml");

        if (ModList.get().isLoaded("embeddium")) {
            EmbeddiumCompat.init();
        }

        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
                @Override
                public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen parent) {
                    return new DaBigFocusConfigScreen(Component.literal(DaBigFocusConstants.MOD_NAME), parent) {

                        @Override
                        public void save() {
                            DaBigFocusConfigNeoForge.SPECS.save();
                            DaBigFocus.toggleFullScreenMode(minecraft.options, minecraft.options.fullscreen().get());
                        }

                        @Override
                        public void addElements() {
                            addOption(OptionInstance.createBoolean("dabigfocus.config.customization.enabled",
                                    DaBigFocusConfigNeoForge.CUSTOMIZED.get(),
                                    DaBigFocusConfigNeoForge.CUSTOMIZED::set));
                            addOption(OptionInstance.createBoolean("dabigfocus.config.customization.related",
                                    DaBigFocusConfigNeoForge.RELATED.get(),
                                    DaBigFocusConfigNeoForge.RELATED::set));

                            addIntField(Component.translatable("dabigfocus.config.customization.x"),
                                    DaBigFocusConfigNeoForge.X,
                                    DaBigFocusConfigNeoForge.X::set);
                            addIntField(Component.translatable("dabigfocus.config.customization.y"),
                                    DaBigFocusConfigNeoForge.Y,
                                    DaBigFocusConfigNeoForge.Y::set);
                            addIntField(Component.translatable("dabigfocus.config.customization.width"),
                                    DaBigFocusConfigNeoForge.WIDTH,
                                    DaBigFocusConfigNeoForge.WIDTH::set);
                            addIntField(Component.translatable("dabigfocus.config.customization.height"),
                                    DaBigFocusConfigNeoForge.HEIGHT,
                                    DaBigFocusConfigNeoForge.HEIGHT::set);
                        }
                    };
                }
            });
        }
    }

}