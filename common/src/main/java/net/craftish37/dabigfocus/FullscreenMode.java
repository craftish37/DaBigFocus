package net.craftish37.dabigfocus;

import com.mojang.serialization.Codec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum FullscreenMode implements OptionEnum, StringRepresentable {

    EXCLUSIVE(0, "exclusive", "dabigfocus.option.fullscreen_mode.exclusive"),
    NATIVE(1, "native", "dabigfocus.option.fullscreen_mode.native"),
    BORDERLESS(2, "borderless", "dabigfocus.option.fullscreen_mode.borderless");

    public static final Codec<FullscreenMode> CODEC = StringRepresentable.fromEnum(FullscreenMode::values);
    public static final IntFunction<FullscreenMode> BY_ID = ByIdMap.continuous(FullscreenMode::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
    private final String serializedName;
    private final String translationKey;

    FullscreenMode(int id, String serializedName, String translatableKey) {
        this.id = id;
        this.serializedName = serializedName;
        this.translationKey = translatableKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public @NotNull String getKey() {
        return this.translationKey;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.serializedName;
    }

}
