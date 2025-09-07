package dev.turjo.easyshopgui.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom logger utility for the plugin
 */
public class Logger {
    
    private static final String PREFIX = "[EasyShopGUI] ";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static boolean debugMode = false;
    
    /**
     * Set debug mode
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }
    
    /**
     * Check if debug mode is enabled
     */
    public static boolean isDebugMode() {
        return debugMode;
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
            String timestamp = LocalDateTime.now().format(TIME_FORMAT);
            Bukkit.getLogger().info(PREFIX + "[DEBUG " + timestamp + "] " + message);
        }
    }
    
    /**
     * Log debug message with color (for console)
     */
    public static void debugColor(String message) {
        if (debugMode) {
            String timestamp = LocalDateTime.now().format(TIME_FORMAT);
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + PREFIX + "[DEBUG " + timestamp + "] " + ChatColor.WHITE + message);
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