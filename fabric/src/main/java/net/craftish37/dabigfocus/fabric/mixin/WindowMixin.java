package net.craftish37.dabigfocus.fabric.mixin;

import com.mojang.blaze3d.platform.*;
import net.craftish37.dabigfocus.fabric.DaBigFocusFabricCaching;
import net.craftish37.dabigfocus.fabric.compat.SodiumExtraCompat;
import net.craftish37.dabigfocus.fabric.config.DaBigFocusConfigFabric;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.Config;
import net.craftish37.dabigfocus.config.ConfigProvider;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.windows.User32;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow
    private boolean fullscreen;

    @Shadow
    @Final
    private ScreenManager screenManager;

    @Shadow
    private int x;
    @Shadow
    private int y;
    @Shadow
    private int width;
    @Shadow
    private int height;

    @Shadow
    @Final
    private long window;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$init(WindowEventHandler eventHandler, ScreenManager screenManager, DisplayData displayData, String preferredFullscreenVideoMode, String title, CallbackInfo ci) {
        DaBigFocusConstants.LOGGER.info("      =============================================");
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            DaBigFocusConstants.LOGGER.info("      ==             System: Windows             ==");
        } else if (osName.contains("darwin") || osName.contains("mac")) {
            DaBigFocusConstants.LOGGER.info("      ==              System: macOS              ==");
        } else {
            DaBigFocusConstants.LOGGER.info("      ==              System: Linux              ==");
        }
        // DaBigFocusConstants.LOGGER.info("      ==                                         ==");
        DaBigFocusConstants.LOGGER.info("      =============================================");
    }

    @Inject(method = "onMove", at = @At("HEAD"))
    private void inject$onMove$head(long window, int x, int y, CallbackInfo ci) {
        if (!this.fullscreen) {
            if (!DaBigFocusFabricCaching.cachedPos) {
                DaBigFocusConstants.LOGGER.info("Window position has been cached");
            }
            DaBigFocusFabricCaching.cachedPos = true;
            DaBigFocusFabricCaching.cachedX = x;
            DaBigFocusFabricCaching.cachedY = y;
        }
    }

    @Inject(method = "onResize", at = @At("HEAD"))
    private void inject$onResize$head(long window, int width, int height, CallbackInfo ci) {
        if (!this.fullscreen && !DaBigFocusFabricCaching.cacheSizeLock) {
            if (!DaBigFocusFabricCaching.cachedSize) {
                DaBigFocusConstants.LOGGER.info("Window size has been cached");
            }
            DaBigFocusFabricCaching.cachedSize = true;
            DaBigFocusFabricCaching.cachedWidth = width;
            DaBigFocusFabricCaching.cachedHeight = height;
        }
    }

    @Redirect(method = "setMode", at = @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V"))
    private void redirect$glfwSetWindowMonitor(long window, long monitor, int xpos, int ypos, int ignored$width, int ignored$height, int ignored$refreshRate) {
        DaBigFocusConstants.LOGGER.info("================= [DaBigFocus Start] =================");
        DaBigFocusConstants.LOGGER.info("Trying to modify window monitor");

        Monitor monitorInstance = this.screenManager.getMonitor(monitor);
        DaBigFocusConstants.LOGGER.info("Current fullscreen monitor is {}", monitor);
        DaBigFocusConstants.LOGGER.info("Current fullscreen mode is {}", ConfigProvider.INSTANCE.ensureLoaded().getFullscreenMode());

        if (monitor != 0L) {
            VideoMode currentMode = monitorInstance.getCurrentMode();
            DaBigFocusConstants.LOGGER.info("Modifying window size limits");
            GLFW.glfwSetWindowSizeLimits(window, 0, 0, currentMode.getWidth(), currentMode.getHeight());
        }

        long finalMonitor;

        int finalWidth;
        int finalHeight;

        int finalX;
        int finalY;

        if (this.fullscreen) {
            VideoMode currentMode = monitorInstance.getCurrentMode();

            DaBigFocusConfigFabric config = DaBigFocusConfigFabric.getInstance();

            DaBigFocusFabricCaching.lastMonitor = monitor;

            DaBigFocusFabricCaching.cacheSizeLock = true;
            DaBigFocusConstants.LOGGER.info("Locked size caching");

            if (config.fullscreen == FullscreenMode.NATIVE) {
                DaBigFocusConstants.LOGGER.info("Fullscreen mode is native");
                finalMonitor = monitor;
                finalX = monitorInstance.getX();
                finalY = monitorInstance.getY();
                finalWidth = currentMode.getWidth();
                finalHeight = currentMode.getHeight();
            } else {
                DaBigFocusConstants.LOGGER.info("Trying to switch to borderless fullscreen mode");

                if (Config.isExclusive()) {
                    finalMonitor = monitor;
                    finalX = monitorInstance.getX();
                    finalY = monitorInstance.getY();
                    finalWidth = currentMode.getWidth();
                    finalHeight = currentMode.getHeight();
                } else {
                    finalMonitor = 0L;
                    if (DaBigFocusConfigFabric.getInstance().customized) {
                        DaBigFocusConstants.LOGGER.info("Customization enabled, so replace the fullscreen size with customized size");
                        finalX = config.x + (config.related ? monitorInstance.getX() : 0);
                        finalY = config.y - (config.height == height ? 1 : 0) + (config.related ? monitorInstance.getY() : 0);
                        finalWidth = config.width;
                        finalHeight = config.height + (config.height == height ? 1 : 0);
                    } else {
                        finalX = monitorInstance.getX();
                        finalY = monitorInstance.getY();
                        finalWidth = ignored$width;
                        finalHeight = ignored$height + 1;
                    }
                    if (SodiumExtraCompat.checkMacReduceResolution()) {
                        DaBigFocusConstants.LOGGER.info("On macOS and reduce resolution in Sodium Extra enabled, reduce the resolution");
                    }
                }
            }

            this.x = finalX;
            this.y = finalY;
            this.width = finalWidth;
            this.height = finalHeight;
        } else {
            DaBigFocusConstants.LOGGER.info("Trying to switch to windowed mode");
            finalMonitor = 0L;

            DaBigFocusConstants.LOGGER.info("Trying to use cached value to resize the window");

            finalWidth = DaBigFocusFabricCaching.cachedSize ? DaBigFocusFabricCaching.cachedWidth : width;
            finalHeight = DaBigFocusFabricCaching.cachedSize ? DaBigFocusFabricCaching.cachedHeight : height;

            if (DaBigFocusFabricCaching.cachedPos) {
                finalX = DaBigFocusFabricCaching.cachedX;
                finalY = DaBigFocusFabricCaching.cachedY;
            } else if (DaBigFocusFabricCaching.lastMonitor != -1 && this.screenManager.getMonitor(DaBigFocusFabricCaching.lastMonitor) != null) {
                Monitor lastMonitor = this.screenManager.getMonitor(DaBigFocusFabricCaching.lastMonitor);
                VideoMode videoMode = lastMonitor.getCurrentMode();
                finalX = (videoMode.getWidth() - finalWidth) / 2;
                finalY = (videoMode.getHeight() - finalHeight) / 2;
            } else {
                finalX = xpos;
                finalY = ypos;
            }
            DaBigFocusFabricCaching.cacheSizeLock = false;
            DaBigFocusConstants.LOGGER.info("Unlocked size caching");
        }

        if (monitor != 0L) {
            DaBigFocusFabricCaching.lastMonitor = monitor;
        }

        DaBigFocusConstants.LOGGER.info("Window size: {}, {}, position: {}, {}", finalWidth, finalHeight, finalX, finalY);

        DaBigFocusConstants.LOGGER.info("Trying to resize and reposition the window");
        GLFW.glfwSetWindowMonitor(window, finalMonitor, finalX, finalY, finalWidth, finalHeight, -1);

        if (this.fullscreen) {
            GLFW.glfwSetWindowAttrib(this.window, 0x20006, 1);
            if (!Config.isBorderless()) {
                if (System.getProperty("os.name").contains("Windows")) {
                    long hWnd = GLFWNativeWin32.glfwGetWin32Window(this.window);
                    if (hWnd != 0) {
                        User32.SetWindowPos(hWnd, User32.HWND_TOPMOST, this.x, this.y, this.width, this.height, 1027);
                        User32.SetWindowLongPtr(hWnd, -16, 0x960A0000L);
                        User32.SetWindowLongPtr(hWnd, -20, 0x40010L);
                    }
                }
                if (Config.isExclusive()) {
                    GLFW.glfwSetWindowAttrib(this.window, 0x20006, 0);
                    if (System.getProperty("os.name").contains("Windows")) {
                        long hWnd = GLFWNativeWin32.glfwGetWin32Window(this.window);
                        if (hWnd != 0) {
                            User32.SetWindowPos(hWnd, User32.HWND_NOTOPMOST, this.x, this.y, this.width, this.height, 1027);
                            User32.SetWindowLongPtr(hWnd, -16, 369229824);
                            User32.SetWindowLongPtr(hWnd, -20, 34340880);
                        }
                    }
                }
            } else {
                GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
                GLFW.glfwSetWindowAttrib(this.window, 0x20006, 1);
                if (System.getProperty("os.name").contains("Windows")) {
                    long hWnd = GLFWNativeWin32.glfwGetWin32Window(this.window);
                    if (hWnd != 0) {
                        User32.SetWindowPos(hWnd, User32.HWND_NOTOPMOST, this.x, this.y, this.width, this.height, 1027);
                        User32.SetWindowLongPtr(hWnd, -16, 369229824);
                        User32.SetWindowLongPtr(hWnd, -20, 34340880);
                    }
                }
            }
        } else {
            GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
        }

        DaBigFocusConstants.LOGGER.info("================= [DaBigFocus End] =================");
    }

}