package net.craftish37.dabigfocus.forge;

import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.config.DaBigFocusConfigScreen;
import net.craftish37.dabigfocus.forge.config.DaBigFocusConfigForge;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(value = DaBigFocusConstants.MOD_ID)
public class DaBigFocusForge {

    public DaBigFocusForge(ModContainer container, IEventBus eventBus) {
        DaBigFocus.init();

        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((parent) -> new DaBigFocusConfigScreen(Component.literal(DaBigFocusConstants.MOD_NAME), parent) {
                @Override
                public void save() {
                    DaBigFocusConfigForge.SPECS.save();
                    DaBigFocus.toggleFullScreenMode(minecraft.options, minecraft.options.fullscreen().get());
                }

                @Override
                public void addElements() {
                    addOption(OptionInstance.createBoolean("dabigfocus.config.customization.enabled",
                            DaBigFocusConfigForge.CUSTOMIZED.get(),
                            DaBigFocusConfigForge.CUSTOMIZED::set));
                    addOption(OptionInstance.createBoolean("dabigfocus.config.customization.related",
                            DaBigFocusConfigForge.RELATED.get(),
                            DaBigFocusConfigForge.RELATED::set));

                    addIntField(Component.translatable("dabigfocus.config.customization.x"),
                            DaBigFocusConfigForge.X,
                            DaBigFocusConfigForge.X::set);
                    addIntField(Component.translatable("dabigfocus.config.customization.y"),
                            DaBigFocusConfigForge.Y,
                            DaBigFocusConfigForge.Y::set);
                    addIntField(Component.translatable("dabigfocus.config.customization.width"),
                            DaBigFocusConfigForge.WIDTH,
                            DaBigFocusConfigForge.WIDTH::set);
                    addIntField(Component.translatable("dabigfocus.config.customization.height"),
                            DaBigFocusConfigForge.HEIGHT,
                            DaBigFocusConfigForge.HEIGHT::set);
                }
            }));
        }
    }

}