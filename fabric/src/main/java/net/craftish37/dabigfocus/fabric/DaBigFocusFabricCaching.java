package net.craftish37.dabigfocus.fabric;

public class DaBigFocusFabricCaching {

    public static long lastMonitor = -1;

    public static boolean cachedSize = false;
    public static boolean cachedPos = false;
    public static boolean cacheSizeLock = true;

    public static int cachedX = 0;
    public static int cachedY = 0;
    public static int cachedWidth = 0;
    public static int cachedHeight = 0;
}
