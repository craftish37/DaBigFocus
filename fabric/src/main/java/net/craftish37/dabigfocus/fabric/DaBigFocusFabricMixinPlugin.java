package net.craftish37.dabigfocus.fabric;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DaBigFocusFabricMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (FabricLoader.getInstance().isModLoaded("vulkanmod")) {
            return mixinClassName.equals("mixin.net.craftish37.dabigfocus.fabric.VulkanWindowMixin") ||
                    mixinClassName.equals("mixin.net.craftish37.dabigfocus.fabric.OptionsMixin") ||
                    mixinClassName.equals("mixin.net.craftish37.dabigfocus.fabric.GLFWMixin");
        } else {
            return checkSodium(mixinClassName) || mixinClassName.equals("mixin.net.craftish37.dabigfocus.fabric.WindowMixin");
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean checkSodium(String mixinClassName) {
        return "mixin.net.craftish37.dabigfocus.fabric.SodiumVideoOptionsScreenMixin".equals(mixinClassName)
                && FabricLoader.getInstance().isModLoaded("sodium");
    }

}
