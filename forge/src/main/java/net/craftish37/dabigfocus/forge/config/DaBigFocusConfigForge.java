package net.craftish37.dabigfocus.forge.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class DaBigFocusConfigForge implements Config {

    public final static DaBigFocusConfigForge INSTANCE = new DaBigFocusConfigForge();

    public static final ForgeConfigSpec SPECS;
    public static final ForgeConfigSpec.BooleanValue CUSTOMIZED;
    public static final ForgeConfigSpec.BooleanValue RELATED;
    public static final ForgeConfigSpec.IntValue X;
    public static final ForgeConfigSpec.IntValue Y;
    public static final ForgeConfigSpec.IntValue WIDTH;
    public static final ForgeConfigSpec.IntValue HEIGHT;
    public static final ForgeConfigSpec.EnumValue<FullscreenMode> FULLSCREEN;

    private static boolean loaded = false;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("dabigfocus");

        CUSTOMIZED = builder.comment("Whether the window size and pos is customized")
                .define("customized", false);
        RELATED = builder.comment("Whether the window pos should related to the monitor")
                .define("related", false);

        X = builder.comment("X coordinate")
                .defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Y = builder.comment("Y coordinate")
                .defineInRange("y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        WIDTH = builder.comment("Width")
                .defineInRange("width", 800, 1, Integer.MAX_VALUE);
        HEIGHT = builder.comment("Height")
                .defineInRange("height", 600, 1, Integer.MAX_VALUE);
        FULLSCREEN = builder.comment("Fullscreen mode")
                .defineEnum("fullscreen", FullscreenMode.NATIVE);

        builder.pop();

        SPECS = builder.build();
    }

    public static DaBigFocusConfigForge ensureLoaded() {
        if (!loaded) {
            DaBigFocusConstants.LOGGER.info("Loading DaBigFocus Config");

            Path path = FMLPaths.CONFIGDIR.get().resolve("dabigfocus-client.toml");
            CommentedFileConfig config = CommentedFileConfig.builder(path)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            config.load();

            loaded = true;
        }
        return INSTANCE;
    }

    private DaBigFocusConfigForge() {
    }

    @Override
    public boolean customized() {
        return CUSTOMIZED.get();
    }

    @Override
    public FullscreenMode getFullscreenMode() {
        return FULLSCREEN.get();
    }

    @Override
    public void setFullscreenMode(FullscreenMode fullscreenMode) {
        FULLSCREEN.set(fullscreenMode);
    }

    @Override
    public void save() {
        SPECS.save();
    }

}
