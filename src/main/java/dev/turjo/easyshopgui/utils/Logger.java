package dev.turjo.easyshopgui.utils;

import dev.turjo.easyshopgui.EasyShopGUI;
import org.bukkit.Bukkit;

/**
 * Custom logger utility for the plugin
 */
public class Logger {
    
    private static final String PREFIX = "[EasyShopGUI] ";
    private static boolean debugMode = false;
    
    /**
     * Set debug mode
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }
    
    /**
     * Log info message
     */
    public static void info(String message) {
        Bukkit.getLogger().info(PREFIX + message);
    }
    
    /**
     * Log warning message
     */
    public static void warn(String message) {
        Bukkit.getLogger().warning(PREFIX + message);
    }
    
    /**
     * Log error message
     */
    public static void error(String message) {
        Bukkit.getLogger().severe(PREFIX + message);
    }
    
    /**
     * Log debug message (only if debug mode is enabled)
     */
    public static void debug(String message) {
        if (debugMode) {
            Bukkit.getLogger().info(PREFIX + "[DEBUG] " + message);
        }
    }
    
    /**
     * Log exception
     */
    public static void exception(String message, Exception e) {
        error(message + ": " + e.getMessage());
        if (debugMode) {
            e.printStackTrace();
        }
    }
}