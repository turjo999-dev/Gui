package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Main player event listener - handles per-player setup/teardown around join and quit.
 */
public class PlayerListener implements Listener {
    
    private final EasyShopGUI plugin;
    
    public PlayerListener(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load this player's transaction history from the database asynchronously so
        // Transaction History / stats reflect their history from previous sessions
        // rather than starting empty every time they join.
        plugin.getTransactionManager().loadPlayerTransactions(event.getPlayer());
        Logger.debug("Loading shop data for " + event.getPlayer().getName());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clear session-scoped state so these maps don't grow unbounded on servers
        // with high player churn. Transaction history itself is already persisted to
        // the database on every transaction (see TransactionManager), not here - there
        // is nothing to flush on quit, only in-memory session state to release.
        plugin.getGuiManager().clearSessions(event.getPlayer());
        plugin.getPlayerPreferencesManager().clear(event.getPlayer());
        Logger.debug("Cleared session data for " + event.getPlayer().getName());
    }
}
