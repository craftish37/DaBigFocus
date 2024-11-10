package net.craftish37.dabigfocus.neoforge.config;

import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.Config;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class DaBigFocusConfigNeoForge implements Config {

    public final static DaBigFocusConfigNeoForge INSTANCE = new DaBigFocusConfigNeoForge();

    public static final ModConfigSpec SPECS;
    public static final ModConfigSpec.BooleanValue CUSTOMIZED;
    public static final ModConfigSpec.BooleanValue RELATED;
    public static final ModConfigSpec.IntValue X;
    public static final ModConfigSpec.IntValue Y;
    public static final ModConfigSpec.IntValue WIDTH;
    public static final ModConfigSpec.IntValue HEIGHT;
    public static final ModConfigSpec.EnumValue<FullscreenMode> FULLSCREEN;

    // private static boolean loaded = false;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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

    public static DaBigFocusConfigNeoForge ensureLoaded() {/*
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
        }*/
        return INSTANCE;
    }

    private DaBigFocusConfigNeoForge() {
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
