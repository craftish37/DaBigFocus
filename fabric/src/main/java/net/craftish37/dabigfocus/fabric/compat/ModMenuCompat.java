package net.craftish37.dabigfocus.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.craftish37.dabigfocus.DaBigFocus;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.config.DaBigFocusConfigScreen;
import net.craftish37.dabigfocus.fabric.config.DaBigFocusConfigFabric;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            DaBigFocusConfigFabric configHandler = DaBigFocusConfigFabric.getInstance();

            return new DaBigFocusConfigScreen(Component.literal(DaBigFocusConstants.MOD_NAME), parent) {
                @Override
                public void save() {
                    configHandler.save();
                    DaBigFocus.toggleFullScreenMode(Minecraft.getInstance().options, Minecraft.getInstance().options.fullscreen().get());
                }

                @Override
                public void addElements() {
                    addOption(OptionInstance.createBoolean("dabigfocus.config.customization.enabled",
                            configHandler.customized,
                            value -> configHandler.customized = value));
                    addOption(OptionInstance.createBoolean("dabigfocus.config.customization.related",
                            configHandler.related,
                            value -> configHandler.related = value));

                    addIntField(Component.translatable("dabigfocus.config.customization.x"),
                            () -> configHandler.x,
                            value -> configHandler.x = value);
                    addIntField(Component.translatable("dabigfocus.config.customization.y"),
                            () -> configHandler.y,
                            value -> configHandler.y = value);
                    addIntField(Component.translatable("dabigfocus.config.customization.width"),
                            () -> configHandler.width,
                            value -> configHandler.width = value > 0 ? value : 1);
                    addIntField(Component.translatable("dabigfocus.config.customization.height"),
                            () -> configHandler.height,
                            value -> configHandler.height = value > 0 ? value : 1);
                }
            };
        };
    }

}
