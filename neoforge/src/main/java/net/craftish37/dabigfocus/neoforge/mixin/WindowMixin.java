package net.craftish37.dabigfocus.neoforge.mixin;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import net.craftish37.dabigfocus.neoforge.config.DaBigFocusConfigNeoForge;
import net.craftish37.dabigfocus.DaBigFocusConstants;
import net.craftish37.dabigfocus.FullscreenMode;
import net.craftish37.dabigfocus.config.Config;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.windows.User32;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private long dabigfocus$lastMonitor = -1;

    @Unique
    private boolean dabigfocus$cachedSize = false;
    @Unique
    private boolean dabigfocus$cachedPos = false;
    @Unique
    private boolean dabigfocus$cacheSizeLock = true;

    @Unique
    private int dabigfocus$cachedX = 0;
    @Unique
    private int dabigfocus$cachedY = 0;
    @Unique
    private int dabigfocus$cachedWidth = 0;
    @Unique
    private int dabigfocus$cachedHeight = 0;

    @Inject(method = "onMove", at = @At("HEAD"))
    private void inject$onMove$head(long window, int x, int y, CallbackInfo ci) {
        if (!this.fullscreen) {
            if (!this.dabigfocus$cachedPos) {
                DaBigFocusConstants.LOGGER.info("Window position has been cached");
            }
            this.dabigfocus$cachedPos = true;
            this.dabigfocus$cachedX = x;
            this.dabigfocus$cachedY = y;
        }
    }

    @Inject(method = "onResize", at = @At("HEAD"))
    private void inject$onResize$head(long window, int width, int height, CallbackInfo ci) {
        if (!this.fullscreen && !this.dabigfocus$cacheSizeLock) {
            if (!this.dabigfocus$cachedSize) {
                DaBigFocusConstants.LOGGER.info("Window size has been cached");
            }
            this.dabigfocus$cachedSize = true;
            this.dabigfocus$cachedWidth = width;
            this.dabigfocus$cachedHeight = height;
        }
    }

    @Redirect(method = "setMode", at = @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V"))
    private void redirect$glfwSetWindowMonitor(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
        DaBigFocusConstants.LOGGER.info("================= [DaBigFocus Start] =================");
        DaBigFocusConstants.LOGGER.info("Trying to modify window monitor");

        Monitor monitorInstance = this.screenManager.getMonitor(monitor);
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

        if (this.fullscreen) {
            VideoMode currentMode = monitorInstance.getCurrentMode();
            DaBigFocusConfigNeoForge.ensureLoaded();

            this.dabigfocus$lastMonitor = monitor;

            this.dabigfocus$cacheSizeLock = true;
            DaBigFocusConstants.LOGGER.info("Locked size caching");

            if (DaBigFocusConfigNeoForge.FULLSCREEN.get() == FullscreenMode.NATIVE) {
                DaBigFocusConstants.LOGGER.info("Fullscreen mode is native, apply now!");
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
                    if (DaBigFocusConfigNeoForge.CUSTOMIZED.get()) {
                        final boolean related = DaBigFocusConfigNeoForge.RELATED.get();
                        final int configX = DaBigFocusConfigNeoForge.X.get();
                        final int configY = DaBigFocusConfigNeoForge.Y.get();
                        final int configWidth = DaBigFocusConfigNeoForge.WIDTH.get();
                        final int configHeight = DaBigFocusConfigNeoForge.HEIGHT.get();
                        DaBigFocusConstants.LOGGER.info("Customization enabled, so replace the fullscreen size with customized size");

                        finalX = configX + (related ? monitorInstance.getX() : 0);
                        finalY = configY - (configHeight == height ? 1 : 0) + (related ? monitorInstance.getY() : 0);
                        finalWidth = configWidth;
                        finalHeight = configHeight + (configHeight == height ? 1 : 0);
                    } else {
                        finalX = monitorInstance.getX();
                        finalY = monitorInstance.getY();
                        finalWidth = width;
                        finalHeight = height + 1;
                    }
                }

                if (monitor != 0L) {
                    this.dabigfocus$lastMonitor = monitor;
                }

                this.x = finalX;
                this.y = finalY;
                this.width = finalWidth;
                this.height = finalHeight;
            }
        } else {
            DaBigFocusConstants.LOGGER.info("Trying to switch to windowed mode");
            finalMonitor = 0L;

            DaBigFocusConstants.LOGGER.info("Trying to use cached value to resize the window");

            finalWidth = dabigfocus$cachedSize ? dabigfocus$cachedWidth : width;
            finalHeight = dabigfocus$cachedSize ? dabigfocus$cachedHeight : height;

            if (this.dabigfocus$cachedPos) {
                finalX = dabigfocus$cachedX;
                finalY = dabigfocus$cachedY;
            } else if (this.dabigfocus$lastMonitor != -1 && this.screenManager.getMonitor(this.dabigfocus$lastMonitor) != null) {
                Monitor lastMonitor = this.screenManager.getMonitor(this.dabigfocus$lastMonitor);
                VideoMode videoMode = lastMonitor.getCurrentMode();
                finalX = (videoMode.getWidth() - finalWidth) / 2;
                finalY = (videoMode.getHeight() - finalHeight) / 2;
            } else {
                finalX = xpos;
                finalY = ypos;
            }
            this.dabigfocus$cacheSizeLock = false;
            DaBigFocusConstants.LOGGER.info("Unlocked size caching");
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
            GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
        }

        DaBigFocusConstants.LOGGER.info("================= [DaBigFocus End] =================");
    }

}