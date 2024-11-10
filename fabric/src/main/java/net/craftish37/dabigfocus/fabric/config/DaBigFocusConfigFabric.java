package net.craftish37.dabigfocus.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class DaBigFocusConfigFabric implements Config {

    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("dabigfocus.json");

    private DaBigFocusConfigFabric() {
    }

    private static DaBigFocusConfigFabric INSTANCE = null;

    public static DaBigFocusConfigFabric getInstance() {
        if (INSTANCE == null) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(configFile.toFile())) {
                INSTANCE = gson.fromJson(reader, DaBigFocusConfigFabric.class);
            } catch (IOException ignored) {
            }
            if (INSTANCE == null) {
                INSTANCE = new DaBigFocusConfigFabric();
                INSTANCE.save();
            }
        }
        return INSTANCE;
    }

    public boolean customized = false;
    public boolean related = false;
    public int x = 0;
    public int y = 0;
    public int width = 800;
    public int height = 600;
    public FullscreenMode fullscreen = FullscreenMode.NATIVE;

    @Override
    public boolean customized() {
        return this.customized;
    }

    @Override
    public FullscreenMode getFullscreenMode() {
        return this.fullscreen;
    }

    @Override
    public void setFullscreenMode(FullscreenMode fullscreenMode) {
        this.fullscreen = fullscreenMode;
    }

    @Override
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            gson.toJson(this, DaBigFocusConfigFabric.class, writer);
        } catch (IOException ignored) {
        }
    }

}
