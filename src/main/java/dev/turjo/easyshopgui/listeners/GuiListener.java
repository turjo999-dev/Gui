package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Handles GUI click events
 */
public class GuiListener implements Listener {
    
    private final EasyShopGUI plugin;
    
    public GuiListener(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Check if it's a shop GUI
        if (title.contains("EASY SHOP GUI") || title.contains("SECTION") || title.contains("✦")) {
            event.setCancelled(true);
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null || meta.getDisplayName() == null) return;
            
            String itemName = MessageUtils.stripColor(meta.getDisplayName());
            
            // Handle main shop GUI clicks
            if (title.contains("EASY SHOP GUI")) {
                handleMainShopClick(player, itemName, clickedItem.getType());
            }
            // Handle section GUI clicks
            else if (title.contains("SECTION")) {
                handleSectionClick(player, itemName, event.getClick());
            }
            // Handle item detail GUI clicks
            else if (title.contains("✦") && !title.contains("EASY SHOP GUI")) {
                handleItemDetailClick(player, itemName, event.getClick());
            }
        }
    }
    
    /**
     * Handle main shop GUI clicks
     */
    private void handleMainShopClick(Player player, String itemName, Material material) {
        // Section navigation
        if (itemName.contains("BLOCKS SECTION")) {
            plugin.getGuiManager().openSection(player, "blocks");
        } else if (itemName.contains("TOOLS & WEAPONS")) {
            plugin.getGuiManager().openSection(player, "tools");
        } else if (itemName.contains("ARMOR SECTION")) {
            plugin.getGuiManager().openSection(player, "armor");
        } else if (itemName.contains("FOOD SECTION")) {
            plugin.getGuiManager().openSection(player, "food");
        } else if (itemName.contains("REDSTONE SECTION")) {
            plugin.getGuiManager().openSection(player, "redstone");
        } else if (itemName.contains("FARMING SECTION")) {
            plugin.getGuiManager().openSection(player, "farming");
        } else if (itemName.contains("DECORATION")) {
            plugin.getGuiManager().openSection(player, "decoration");
        } else if (itemName.contains("SPAWNERS")) {
            plugin.getGuiManager().openSection(player, "spawners");
        } else if (itemName.contains("ENCHANTED BOOKS")) {
            plugin.getGuiManager().openSection(player, "enchanted_books");
        } else if (itemName.contains("POTIONS & EFFECTS")) {
            plugin.getGuiManager().openSection(player, "potions");
        } else if (itemName.contains("RARE ITEMS")) {
            plugin.getGuiManager().openSection(player, "rare_items");
        }
        // Utility functions
        else if (itemName.contains("SEARCH ITEMS")) {
            player.sendMessage("§e🔍 Search feature coming soon!");
        } else if (itemName.contains("TRANSACTION HISTORY")) {
            player.sendMessage("§3📋 Transaction history coming soon!");
        } else if (itemName.contains("SHOP SETTINGS")) {
            player.sendMessage("§7⚙ Shop settings coming soon!");
        } else if (itemName.contains("QUICK SELL")) {
            player.sendMessage("§c💸 Quick sell feature coming soon!");
        } else if (itemName.contains("CLOSE SHOP")) {
            player.closeInventory();
        }
    }
    
    /**
     * Handle section GUI clicks
     */
    private void handleSectionClick(Player player, String itemName, org.bukkit.event.inventory.ClickType clickType) {
        // Navigation
        if (itemName.contains("BACK TO SHOP")) {
            plugin.getGuiManager().openShop(player, "main");
        } else if (itemName.contains("PREVIOUS PAGE")) {
            player.sendMessage("§e← Previous page functionality coming soon!");
        } else if (itemName.contains("NEXT PAGE")) {
            player.sendMessage("§e→ Next page functionality coming soon!");
        } else if (itemName.contains("QUICK ACTIONS")) {
            player.sendMessage("§d⚡ Quick actions coming soon!");
        }
        // Item interactions would be handled here
        else {
            // This would open the item detail GUI
            player.sendMessage("§aItem interaction: " + itemName);
            player.sendMessage("§7Click type: " + clickType.name());
        }
    }
    
    /**
     * Handle item detail GUI clicks
     */
    private void handleItemDetailClick(Player player, String itemName, org.bukkit.event.inventory.ClickType clickType) {
        // Quantity controls
        if (itemName.contains("-5 QUANTITY")) {
            player.sendMessage("§c-5 Quantity");
        } else if (itemName.contains("-1 QUANTITY")) {
            player.sendMessage("§c-1 Quantity");
        } else if (itemName.contains("+1 QUANTITY")) {
            player.sendMessage("§a+1 Quantity");
        } else if (itemName.contains("+5 QUANTITY")) {
            player.sendMessage("§a+5 Quantity");
        } else if (itemName.contains("SET TO")) {
            String[] parts = itemName.split("SET TO ");
            if (parts.length > 1) {
                player.sendMessage("§eSet quantity to " + parts[1]);
            }
        }
        // Actions
        else if (itemName.contains("BUY ITEMS")) {
            player.sendMessage("§a💰 Purchase confirmed!");
        } else if (itemName.contains("SELL ITEMS")) {
            player.sendMessage("§6💸 Sale confirmed!");
        } else if (itemName.contains("QUICK ACTIONS")) {
            player.sendMessage("§d⚡ Quick actions menu coming soon!");
        } else if (itemName.contains("BACK")) {
            player.sendMessage("§c← Going back...");
        } else if (itemName.contains("CLOSE")) {
            player.closeInventory();
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Play sound when closing shop GUIs
        if (title.contains("EASY SHOP GUI") || title.contains("SECTION") || title.contains("✦")) {
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        }
    }
}