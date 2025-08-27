package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles GUI click events
 */
public class GuiListener implements Listener {
    
    private final EasyShopGUI plugin;
    private final Map<Player, String> playerCurrentSection = new HashMap<>();
    private final Map<Player, Integer> playerCurrentPage = new HashMap<>();
    private final Map<Player, Long> lastClickTime = new HashMap<>();
    
    public GuiListener(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Anti-spam protection
        long currentTime = System.currentTimeMillis();
        if (lastClickTime.containsKey(player) && currentTime - lastClickTime.get(player) < 100) {
            event.setCancelled(true);
            return;
        }
        lastClickTime.put(player, currentTime);
        
        // Check if it's a shop GUI
        if (title.contains("EASY SHOP GUI") || title.contains("SECTION") || title.contains("✦")) {
            event.setCancelled(true);
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null || meta.getDisplayName() == null) return;
            
            String itemName = MessageUtils.stripColor(meta.getDisplayName());
            
            // Handle main shop GUI clicks
            Logger.debug("GUI click detected - Title: " + title + ", Item: " + itemName + ", Material: " + clickedItem.getType());
            
            if (title.contains("EASY SHOP GUI")) {
                handleMainShopClick(player, itemName, clickedItem.getType());
            }
            // Handle section GUI clicks
            else if (title.contains("SECTION") || (title.contains("✦") && !title.contains("EASY SHOP GUI"))) {
                handleSectionClick(player, itemName, event.getClick(), clickedItem);
            }
        }
    }
    
    /**
     * Handle main shop GUI clicks
     */
    private void handleMainShopClick(Player player, String itemName, Material material) {
        // Section navigation
        Logger.debug("Handling main shop click - Item: " + itemName + ", Material: " + material);
        
        if (itemName.contains("BLOCKS") || material == Material.STONE) {
            playerCurrentSection.put(player, "blocks");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "blocks");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("ORES") || itemName.contains("MINERALS") || material == Material.DIAMOND_ORE) {
            playerCurrentSection.put(player, "ores");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "ores");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("FOOD") || material == Material.GOLDEN_APPLE || material == Material.APPLE) {
            playerCurrentSection.put(player, "food");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "food");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("REDSTONE") || material == Material.REDSTONE || material == Material.REPEATER) {
            playerCurrentSection.put(player, "redstone");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "redstone");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("FARMING") || material == Material.WHEAT || material == Material.WHEAT_SEEDS) {
            playerCurrentSection.put(player, "farming");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "farming");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("DECORATION") || material == Material.FLOWER_POT || material == Material.POPPY) {
            playerCurrentSection.put(player, "decoration");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "decoration");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("POTIONS") || material == Material.POTION || material == Material.BREWING_STAND) {
            playerCurrentSection.put(player, "potions");
            playerCurrentPage.put(player, 0);
            plugin.getGuiManager().openSection(player, "potions");
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
        // Utility functions
        else if (itemName.contains("SEARCH ITEMS")) {
            new dev.turjo.easyshopgui.gui.SearchGui(plugin, player).open();
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("TRANSACTION HISTORY")) {
            new dev.turjo.easyshopgui.gui.TransactionHistoryGui(plugin, player).open();
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("SHOP SETTINGS")) {
            new dev.turjo.easyshopgui.gui.ShopSettingsGui(plugin, player).open();
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("QUICK SELL")) {
            new dev.turjo.easyshopgui.gui.QuickSellGui(plugin, player).open();
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("CLOSE SHOP")) {
            player.closeInventory();
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
    
    /**
     * Handle section GUI clicks
     */
    private void handleSectionClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        // Navigation
        if (itemName.contains("BACK TO SHOP")) {
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        } else if (itemName.contains("PREVIOUS PAGE")) {
            handlePreviousPage(player);
            return;
        } else if (itemName.contains("NEXT PAGE")) {
            handleNextPage(player);
            return;
        } else if (itemName.contains("QUICK ACTIONS")) {
            player.sendMessage("§d⚡ Quick actions coming soon!");
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        
        // Handle item interactions
        if (!itemName.isEmpty() && !itemName.contains("PAGE INFO") && !itemName.contains("PLAYER") && 
            !itemName.contains("QUICK ACTIONS") && clickedItem.getType() != Material.BLACK_STAINED_GLASS_PANE &&
            clickedItem.getType() != Material.GRAY_STAINED_GLASS_PANE) {
            handleItemTransaction(player, itemName, clickType, clickedItem);
        }
    }
    
    /**
     * Handle item transactions (buy/sell)
     */
    private void handleItemTransaction(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId == null) return;
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section == null) return;
        
        // Find the item by material type
        ShopItem shopItem = null;
        for (ShopItem item : section.getItems()) {
            if (item.getMaterial() == clickedItem.getType()) {
                shopItem = item;
                break;
            }
        }
        
        if (shopItem == null) return;
        
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        switch (clickType) {
            case LEFT:
                // Buy 1 item
                buyItem(player, shopItem, 1);
                break;
            case RIGHT:
                // Sell 1 item
                sellItem(player, shopItem, 1);
                break;
            case SHIFT_LEFT:
                // Buy 64 items
                buyItem(player, shopItem, 64);
                break;
            case SHIFT_RIGHT:
                // Sell all items of this type
                sellAllItems(player, shopItem);
                break;
            case MIDDLE:
                // Open item detail GUI
                plugin.getGuiManager().openItemDetail(player, sectionId, shopItem.getId());
                break;
            default:
                break;
        }
    }
    
    /**
     * Buy item
     */
    private void buyItem(Player player, ShopItem item, int amount) {
        double totalPrice = item.getBuyPrice() * amount;
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        if (balance < totalPrice) {
            player.sendMessage("§c💰 Insufficient funds! You need $" + String.format("%.2f", totalPrice) + " but only have $" + String.format("%.2f", balance));
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        // Check inventory space
        if (!hasInventorySpace(player, amount)) {
            player.sendMessage("§c📦 Not enough inventory space!");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        // Process transaction
        plugin.getEconomyManager().getEconomy().withdrawPlayer(player, totalPrice);
        
        // Give items
        ItemStack itemStack = new ItemStack(item.getMaterial(), amount);
        player.getInventory().addItem(itemStack);
        
        // Success message
        player.sendMessage("§a💰 Successfully purchased " + amount + "x " + item.getDisplayName() + " §afor §6$" + String.format("%.2f", totalPrice) + "!");
        playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        
        // Refresh GUI
        refreshCurrentGUI(player);
    }
    
    /**
     * Sell item
     */
    private void sellItem(Player player, ShopItem item, int amount) {
        int playerItemCount = getPlayerItemCount(player, item.getMaterial());
        
        if (playerItemCount < amount) {
            player.sendMessage("§c📦 You don't have enough " + item.getDisplayName() + "! You have " + playerItemCount + " but need " + amount);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double totalPrice = item.getSellPrice() * amount;
        
        // Remove items from inventory
        removeItemsFromInventory(player, item.getMaterial(), amount);
        
        // Give money
        plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
        
        // Success message
        player.sendMessage("§6💸 Successfully sold " + amount + "x " + item.getDisplayName() + " §6for §a$" + String.format("%.2f", totalPrice) + "!");
        playSound(player, Sound.ENTITY_VILLAGER_YES);
        
        // Refresh GUI
        refreshCurrentGUI(player);
    }
    
    /**
     * Sell all items of a type
     */
    private void sellAllItems(Player player, ShopItem item) {
        int playerItemCount = getPlayerItemCount(player, item.getMaterial());
        
        if (playerItemCount == 0) {
            player.sendMessage("§c📦 You don't have any " + item.getDisplayName() + " to sell!");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        sellItem(player, item, playerItemCount);
    }
    
    /**
     * Handle previous page
     */
    private void handlePreviousPage(Player player) {
        int currentPage = playerCurrentPage.getOrDefault(player, 0);
        if (currentPage > 0) {
            playerCurrentPage.put(player, currentPage - 1);
            String sectionId = playerCurrentSection.get(player);
            if (sectionId != null) {
                openSectionWithPage(player, sectionId, currentPage - 1);
                playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            }
        }
    }
    
    /**
     * Handle next page
     */
    private void handleNextPage(Player player) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId == null) return;
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section == null) return;
        
        int currentPage = playerCurrentPage.getOrDefault(player, 0);
        int totalPages = (int) Math.ceil((double) section.getItems().size() / 28);
        
        if (currentPage < totalPages - 1) {
            playerCurrentPage.put(player, currentPage + 1);
            openSectionWithPage(player, sectionId, currentPage + 1);
            playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
        }
    }
    
    /**
     * Open section with specific page
     */
    private void openSectionWithPage(Player player, String sectionId, int page) {
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section != null) {
            dev.turjo.easyshopgui.gui.SectionGui sectionGui = new dev.turjo.easyshopgui.gui.SectionGui(plugin, player, section);
            sectionGui.setCurrentPage(page);
            sectionGui.open();
        }
    }
    
    /**
     * Refresh current GUI
     */
    private void refreshCurrentGUI(Player player) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId != null) {
            int currentPage = playerCurrentPage.getOrDefault(player, 0);
            openSectionWithPage(player, sectionId, currentPage);
        }
    }
    
    /**
     * Check if player has inventory space
     */
    private boolean hasInventorySpace(Player player, int amount) {
        int emptySlots = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
            }
        }
        return emptySlots >= Math.ceil((double) amount / 64);
    }
    
    /**
     * Get player item count
     */
    private int getPlayerItemCount(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    /**
     * Remove items from inventory
     */
    private void removeItemsFromInventory(Player player, Material material, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= itemAmount;
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
            }
        }
    }
    
    /**
     * Play sound to player
     */
    private void playSound(Player player, Sound sound) {
        try {
            player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
        } catch (Exception e) {
            // Ignore sound errors
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Play sound when closing shop GUIs
        if (title.contains("EASY SHOP GUI") || title.contains("SECTION") || title.contains("✦")) {
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
}