package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Main player event listener
 */
public class PlayerListener implements Listener {
    
    private final EasyShopGUI plugin;
    
    public PlayerListener(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player data
        // TODO: Load player transaction history, preferences, etc.
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data
        // TODO: Save player transaction history, preferences, etc.
    }
}