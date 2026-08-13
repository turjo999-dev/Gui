package dev.turjo.easyshopgui.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for message formatting
 */
public class MessageUtils {
    
    /**
     * Colorize a string with color codes
     */
    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Colorize a list of strings
     */
    public static List<String> colorize(List<String> messages) {
        if (messages == null) return null;
        return messages.stream()
                .map(MessageUtils::colorize)
                .collect(Collectors.toList());
    }
    
    /**
     * Strip color codes from string
     */
    public static String stripColor(String message) {
        if (message == null) return "";
        return ChatColor.stripColor(colorize(message));
    }
    
    /**
     * Format number with commas
     */
    public static String formatNumber(double number) {
        return String.format("%,.2f", number);
    }
    
    /**
     * Format currency
     */
    public static String formatCurrency(double amount) {
        return "$" + formatNumber(amount);
    }
    
    /**
     * Get progress bar
     */
    public static String getProgressBar(int current, int max, int length, char symbol, String completeColor, String incompleteColor) {
        double percentage = (double) current / max;
        int completed = (int) (percentage * length);
        
        StringBuilder bar = new StringBuilder();
        bar.append(completeColor);
        
        for (int i = 0; i < length; i++) {
            if (i < completed) {
                bar.append(symbol);
            } else {
                bar.append(incompleteColor).append(symbol);
            }
        }
        
        return bar.toString();
    }
    
    /**
     * Center text
     */
    public static String centerText(String text, int length) {
        if (text.length() >= length) return text;
        
        int spaces = (length - text.length()) / 2;
        StringBuilder centered = new StringBuilder();
        
        for (int i = 0; i < spaces; i++) {
            centered.append(" ");
        }
        
        centered.append(text);
        return centered.toString();
    }
}