package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;

/**
 * Listener for paper currency interactions
 */
public class CurrencyListener implements Listener {
    
    private final EasyShopGUI plugin;
    
    public CurrencyListener(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        // Check if it's a cheque
        if (plugin.getPaperCurrency().isCheque(item)) {
            event.setCancelled(true);
            
            // Attempt to redeem cheque
            boolean success = plugin.getPaperCurrency().redeemCheque(player, item);
            
            if (success) {
                // Play success sound
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                // Play error sound
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    }
    
    /**
     * Handle cheque interactions in other plugin GUIs (like Shopkeeper)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        // Check if it's a cheque being used in a trade
        if (clickedItem != null && plugin.getPaperCurrency().isCheque(clickedItem)) {
            String inventoryTitle = event.getView().getTitle();
            
            // Check if it's a Shopkeeper GUI or similar trading interface
            if (inventoryTitle.contains("Shopkeeper") || inventoryTitle.contains("Trading") || 
                inventoryTitle.contains("Shop") || inventoryTitle.contains("Trade")) {
                
                // Allow the cheque to be used as currency
                // The cheque value will be checked by the trading plugin
                Logger.debug("Cheque being used in trading interface: " + inventoryTitle);
                
                // Don't cancel the event - let the trading plugin handle it
                // The cheque acts as a currency item with its value
            }
        }
    }
}