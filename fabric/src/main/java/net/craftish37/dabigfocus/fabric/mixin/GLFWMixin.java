package net.craftish37.dabigfocus.fabric.mixin;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import net.craftish37.dabigfocus.fabric.DaBigFocusFabricCaching;
import net.craftish37.dabigfocus.fabric.config.DaBigFocusConfigFabric;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.Config;
import net.craftish37.dabigfocus.mixin.accessor.WindowAccessor;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.windows.User32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.JNI.invokePPV;

@Mixin(value = GLFW.class, remap = false)
public class GLFWMixin {

    @Inject(method = "glfwSetWindowMonitor", at = @At("HEAD"), cancellable = true)
    private static void inject$glfwSetWindowMonitor(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate, CallbackInfo ci) {
        Window windowInstance = Minecraft.getInstance().getWindow();
        if (windowInstance == null)
            return;

        DaBigFocusConstants.LOGGER.info("================= [DaBigFocus Start] =================");
        DaBigFocusConstants.LOGGER.info("Trying to modify window monitor");

        WindowAccessor accessor = (WindowAccessor) (Object) windowInstance;

        if (windowInstance.isFullscreen())
            if (monitor == 0L)
                monitor = windowInstance.findBestMonitor().getMonitor();

        Monitor monitorInstance = accessor.getScreenManager().getMonitor(monitor);
        DaBigFocusConstants.LOGGER.info("Current fullscreen monitor is {}", monitor);

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

        if (windowInstance != null && windowInstance.isFullscreen()) {
            VideoMode currentMode = monitorInstance.getCurrentMode();

            DaBigFocusConfigFabric config = DaBigFocusConfigFabric.getInstance();

            DaBigFocusFabricCaching.lastMonitor = monitor;

            DaBigFocusFabricCaching.cacheSizeLock = true;
            DaBigFocusConstants.LOGGER.info("Locked size caching");

            if (config.fullscreen == FullscreenMode.NATIVE) {
                DaBigFocusConstants.LOGGER.info("Fullscreen mode is native, apply now!");
                finalMonitor = monitor;
                finalX = monitorInstance.getX();
                finalY = monitorInstance.getY();
                finalWidth = currentMode.getWidth();
                finalHeight = currentMode.getHeight();
                DaBigFocusConstants.LOGGER.info("================= [DaBigFocus End] =================");
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
                        finalWidth = width;
                        finalHeight = height + 1;
                    }
                }

                accessor.setX(finalX);
                accessor.setY(finalY);
                accessor.setWidth(finalWidth);
                accessor.setHeight(finalHeight);
            }
        } else {
            DaBigFocusConstants.LOGGER.info("Trying to switch to windowed mode");

            finalMonitor = 0L;

            DaBigFocusConstants.LOGGER.info("Trying to use cached value to resize the window");

            finalWidth = DaBigFocusFabricCaching.cachedSize ? DaBigFocusFabricCaching.cachedWidth : width;
            finalHeight = DaBigFocusFabricCaching.cachedSize ? DaBigFocusFabricCaching.cachedHeight : height;

            if (DaBigFocusFabricCaching.cachedPos) {
                finalX = DaBigFocusFabricCaching.cachedX;
                finalY = DaBigFocusFabricCaching.cachedY;
            } else if (DaBigFocusFabricCaching.lastMonitor != -1) {
                Monitor lastMonitor = accessor.getScreenManager().getMonitor(DaBigFocusFabricCaching.lastMonitor);
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
        finalExecute(window, finalMonitor, finalX, finalY, finalWidth, finalHeight, -1);

        if (windowInstance.isFullscreen()) {
            GLFW.glfwSetWindowAttrib(windowInstance.getWindow(), 0x20006, 1);
            if (!Config.isBorderless()) {
                if (System.getProperty("os.name").contains("Windows")) {
                    long hWnd = GLFWNativeWin32.glfwGetWin32Window(windowInstance.getWindow());
                    if (hWnd != 0) {
                        User32.SetWindowPos(
                                hWnd,
                                User32.HWND_TOPMOST,
                                windowInstance.getX(),
                                windowInstance.getY(),
                                windowInstance.getScreenWidth(),
                                windowInstance.getScreenHeight(),
                                1027
                        );
                        User32.SetWindowLongPtr(hWnd, -16, 0x960A0000L);
                        User32.SetWindowLongPtr(hWnd, -20, 0x40010L);
                    }
                }
                if (Config.isExclusive()) {
                    GLFW.glfwSetWindowAttrib(windowInstance.getWindow(), 0x20006, 0);
                    if (System.getProperty("os.name").contains("Windows")) {
                        long hWnd = GLFWNativeWin32.glfwGetWin32Window(windowInstance.getWindow());
                        if (hWnd != 0) {
                            User32.SetWindowPos(
                                    hWnd,
                                    User32.HWND_NOTOPMOST,
                                    windowInstance.getX(),
                                    windowInstance.getY(),
                                    windowInstance.getScreenWidth(),
                                    windowInstance.getScreenHeight(),
                                    1027
                            );
                            User32.SetWindowLongPtr(hWnd, -16, 369229824);
                            User32.SetWindowLongPtr(hWnd, -20, 34340880);
                        }
                    }
                }
            } else {
                GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
                GLFW.glfwSetWindowAttrib(windowInstance.getWindow(), 0x20006, 0);
                if (System.getProperty("os.name").contains("Windows")) {
                    long hWnd = GLFWNativeWin32.glfwGetWin32Window(windowInstance.getWindow());
                    if (hWnd != 0) {
                        User32.SetWindowPos(
                                hWnd,
                                User32.HWND_NOTOPMOST,
                                windowInstance.getX(),
                                windowInstance.getY(),
                                windowInstance.getScreenWidth(),
                                windowInstance.getScreenHeight(),
                                1027
                        );
                        User32.SetWindowLongPtr(hWnd, -16, 369229824);
                        User32.SetWindowLongPtr(hWnd, -20, 34340880);
                    }
                }
            }
        } else {
            GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
        }

        DaBigFocusConstants.LOGGER.info("================= [DaBigFocus End] =================");
        ci.cancel();
    }

    @Unique
    private static void finalExecute(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
        long __functionAddress = GLFW.Functions.SetWindowMonitor;
        if (CHECKS) {
            check(window);
        }
        invokePPV(window, monitor, xpos, ypos, width, height, refreshRate, __functionAddress);
    }

}
