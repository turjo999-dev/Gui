package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SearchGui;
import dev.turjo.easyshopgui.gui.QuickSellGui;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced GUI listener with proper buying/selling system
 */
public class GuiListener implements Listener {
    
    private final EasyShopGUI plugin;
    private final Map<Player, String> playerCurrentSection = new HashMap<>();
    private final Map<Player, Integer> playerCurrentPage = new HashMap<>();
    private final Map<Player, Long> lastClickTime = new HashMap<>();
    private final Map<Player, Boolean> waitingForSearch = new HashMap<>();
    private final Map<Player, SearchGui> activeSearchGuis = new HashMap<>();
    private final Map<Player, QuickSellGui> activeQuickSellGuis = new HashMap<>();
    
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
        if (isShopGUI(title)) {
            event.setCancelled(true);
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;
            
            String itemName = meta.getDisplayName() != null ? MessageUtils.stripColor(meta.getDisplayName()) : "";
            
            Logger.debug("GUI Click - Title: " + title + ", Item: " + itemName + ", Slot: " + event.getSlot() + ", Click: " + event.getClick());
            
            // Route to appropriate handler
            if (title.contains("EASY SHOP GUI")) {
                handleMainShopClick(player, itemName, clickedItem.getType(), event.getSlot());
            } else if (title.contains("SEARCH ITEMS")) {
                handleSearchClick(player, itemName, event.getClick(), clickedItem);
            } else if (title.contains("QUICK SELL")) {
                handleQuickSellClick(player, itemName, event.getClick(), clickedItem);
            } else if (title.contains("SECTION") || title.contains("BLOCKS") || title.contains("ORES") || title.contains("FOOD")) {
                handleSectionClick(player, itemName, event.getClick(), clickedItem, event.getSlot());
            }
        }
    }
    
    /**
     * Check if GUI is a shop GUI
     */
    private boolean isShopGUI(String title) {
        return title.contains("EASY SHOP GUI") || title.contains("SECTION") || 
               title.contains("SEARCH ITEMS") || title.contains("QUICK SELL") ||
               title.contains("BLOCKS") || title.contains("ORES") || title.contains("FOOD") ||
               title.contains("REDSTONE") || title.contains("FARMING") || title.contains("DECORATION") ||
               title.contains("TRANSACTION HISTORY") || title.contains("SHOP SETTINGS");
    }
    
    /**
     * Handle main shop GUI clicks
     */
    private void handleMainShopClick(Player player, String itemName, Material material, int slot) {
        Logger.debug("Main shop click - Slot: " + slot + ", Item: " + itemName);
        
        switch (slot) {
            case 20: // Blocks
                openSection(player, "blocks");
                break;
            case 22: // Ores
                openSection(player, "ores");
                break;
            case 24: // Food
                openSection(player, "food");
                break;
            case 29: // Redstone
                openSection(player, "redstone");
                break;
            case 31: // Farming
                openSection(player, "farming");
                break;
            case 33: // Decoration
                openSection(player, "decoration");
                break;
            case 37: // Search
                openSearchGUI(player);
                break;
            case 38: // Transaction History
                openTransactionHistory(player);
                break;
            case 39: // Settings
                openSettings(player);
                break;
            case 41: // Quick Sell
                openQuickSell(player);
                break;
            case 43: // Close
                player.closeInventory();
                playSound(player, Sound.UI_BUTTON_CLICK);
                break;
            default:
                // Check by material for fallback
                if (material == Material.STONE) {
                    openSection(player, "blocks");
                } else if (material == Material.DIAMOND_ORE) {
                    openSection(player, "ores");
                } else if (material == Material.GOLDEN_APPLE) {
                    openSection(player, "food");
                } else if (material == Material.REDSTONE) {
                    openSection(player, "redstone");
                } else if (material == Material.WHEAT) {
                    openSection(player, "farming");
                } else if (material == Material.FLOWER_POT) {
                    openSection(player, "decoration");
                }
                break;
        }
    }
    
    /**
     * Handle section GUI clicks with proper item transactions
     */
    private void handleSectionClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem, int slot) {
        Logger.debug("Section click - Item: " + itemName + ", Click: " + clickType + ", Slot: " + slot);
        
        // Navigation items
        if (slot == 0 || itemName.contains("BACK")) {
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        
        if (slot == 45 || itemName.contains("PREVIOUS PAGE")) {
            handlePreviousPage(player);
            return;
        }
        
        if (slot == 53 || itemName.contains("NEXT PAGE")) {
            handleNextPage(player);
            return;
        }
        
        // Skip navigation and decoration items
        if (isNavigationItem(itemName, clickedItem, slot)) {
            return;
        }
        
        // Handle item transactions
        String sectionId = playerCurrentSection.get(player);
        if (sectionId != null) {
            ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
            if (section != null) {
                // Find the item by material
                ShopItem shopItem = section.getItems().stream()
                        .filter(item -> item.getMaterial() == clickedItem.getType())
                        .findFirst()
                        .orElse(null);
                
                if (shopItem != null) {
                    Logger.debug("Found shop item: " + shopItem.getId() + " for transaction");
                    handleItemTransaction(player, shopItem, clickType);
                } else {
                    Logger.debug("No shop item found for material: " + clickedItem.getType());
                }
            }
        }
    }
    
    /**
     * Check if item is a navigation/decoration item
     */
    private boolean isNavigationItem(String itemName, ItemStack item, int slot) {
        // Check by slot position (navigation slots)
        if (slot == 0 || slot == 4 || slot == 8 || slot == 45 || slot == 49 || slot == 53) {
            return true;
        }
        
        // Check by material (background items)
        Material material = item.getType();
        if (material == Material.BLACK_STAINED_GLASS_PANE ||
            material == Material.GRAY_STAINED_GLASS_PANE ||
            material == Material.BLUE_STAINED_GLASS_PANE ||
            material == Material.RED_STAINED_GLASS_PANE ||
            material == Material.YELLOW_STAINED_GLASS_PANE ||
            material == Material.SPECTRAL_ARROW ||
            material == Material.ARROW ||
            material == Material.BOOK ||
            material == Material.EMERALD ||
            material == Material.PLAYER_HEAD) {
            return true;
        }
        
        // Check by name content
        return itemName.contains("PAGE INFO") || itemName.contains("PLAYER") ||
               itemName.contains("QUICK ACTIONS") || itemName.contains("BACK") ||
               itemName.contains("PREVIOUS") || itemName.contains("NEXT");
    }
    
    /**
     * Handle item transactions (buy/sell)
     */
    private void handleItemTransaction(Player player, ShopItem shopItem, ClickType clickType) {
        Logger.debug("Handling transaction - Item: " + shopItem.getId() + ", Click: " + clickType);
        
        switch (clickType) {
            case LEFT:
                buyItem(player, shopItem, 1);
                break;
            case RIGHT:
                sellItem(player, shopItem, 1);
                break;
            case SHIFT_LEFT:
                buyItem(player, shopItem, 64);
                break;
            case SHIFT_RIGHT:
                sellAllItems(player, shopItem);
                break;
            case MIDDLE:
                String sectionId = playerCurrentSection.get(player);
                if (sectionId != null) {
                    plugin.getGuiManager().openItemDetail(player, sectionId, shopItem.getId());
                }
                break;
            default:
                Logger.debug("Unhandled click type: " + clickType);
                break;
        }
    }
    
    /**
     * Buy item implementation
     */
    private void buyItem(Player player, ShopItem item, int amount) {
        Logger.debug("Attempting to buy " + amount + "x " + item.getId());
        
        // Calculate total price
        double totalPrice = item.getBuyPrice() * amount;
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        Logger.debug("Price: $" + totalPrice + ", Balance: $" + balance);
        
        // Check if player has enough money
        if (balance < totalPrice) {
            player.sendMessage("§c💰 Insufficient funds! Need $" + String.format("%.2f", totalPrice) + 
                             " but only have $" + String.format("%.2f", balance));
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
        try {
            plugin.getEconomyManager().getEconomy().withdrawPlayer(player, totalPrice);
            ItemStack itemToGive = new ItemStack(item.getMaterial(), amount);
            player.getInventory().addItem(itemToGive);
            
            player.sendMessage("§a💰 Successfully purchased " + amount + "x " + item.getDisplayName() + 
                              " §afor §6$" + String.format("%.2f", totalPrice) + "!");
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            
            Logger.debug("Purchase successful: " + amount + "x " + item.getId());
            
            // Refresh GUI to update balance display
            refreshCurrentGUI(player);
            
        } catch (Exception e) {
            Logger.error("Error during purchase: " + e.getMessage());
            player.sendMessage("§cError during purchase! Please try again.");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
        }
    }
    
    /**
     * Sell item implementation
     */
    private void sellItem(Player player, ShopItem item, int amount) {
        Logger.debug("Attempting to sell " + amount + "x " + item.getId());
        
        int playerItemCount = getPlayerItemCount(player, item.getMaterial());
        
        if (playerItemCount < amount) {
            player.sendMessage("§c📦 You don't have enough " + item.getDisplayName() + 
                             "! Have " + playerItemCount + ", need " + amount);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double totalPrice = item.getSellPrice() * amount;
        
        try {
            removeItemsFromInventory(player, item.getMaterial(), amount);
            plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
            
            player.sendMessage("§6💸 Successfully sold " + amount + "x " + item.getDisplayName() + 
                              " §6for §a$" + String.format("%.2f", totalPrice) + "!");
            playSound(player, Sound.ENTITY_VILLAGER_YES);
            
            Logger.debug("Sale successful: " + amount + "x " + item.getId());
            
            // Refresh GUI to update balance display
            refreshCurrentGUI(player);
            
        } catch (Exception e) {
            Logger.error("Error during sale: " + e.getMessage());
            player.sendMessage("§cError during sale! Please try again.");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
        }
    }
    
    /**
     * Sell all items of a type
     */
    private void sellAllItems(Player player, ShopItem item) {
        int playerItemCount = getPlayerItemCount(player, item.getMaterial());
        if (playerItemCount > 0) {
            sellItem(player, item, playerItemCount);
        } else {
            player.sendMessage("§c📦 You don't have any " + item.getDisplayName() + " to sell!");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
        }
    }
    
    /**
     * Handle search GUI clicks
     */
    private void handleSearchClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        SearchGui searchGui = activeSearchGuis.get(player);
        
        if (itemName.contains("BACK TO SHOP")) {
            activeSearchGuis.remove(player);
            waitingForSearch.remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("CLEAR SEARCH")) {
            if (searchGui != null) {
                searchGui.clearSearch();
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
        } else if (itemName.contains("PREVIOUS PAGE")) {
            if (searchGui != null) {
                searchGui.previousPage();
                playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            }
        } else if (itemName.contains("NEXT PAGE")) {
            if (searchGui != null) {
                searchGui.nextPage();
                playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            }
        } else if (itemName.contains("SMART SEARCH")) {
            waitingForSearch.put(player, true);
            player.closeInventory();
            player.sendMessage("§b🔍 Type your search query in chat! (Type 'cancel' to cancel)");
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
    
    /**
     * Handle quick sell GUI clicks
     */
    private void handleQuickSellClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        if (itemName.contains("BACK TO SHOP")) {
            activeQuickSellGuis.remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("SELL EVERYTHING")) {
            QuickSellGui quickSellGui = activeQuickSellGuis.get(player);
            if (quickSellGui != null) {
                quickSellGui.sellAll();
                playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            }
        } else if (itemName.contains("REFRESH INVENTORY")) {
            openQuickSell(player);
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
    
    /**
     * Open section with proper tracking
     */
    private void openSection(Player player, String sectionId) {
        Logger.debug("Opening section: " + sectionId + " for player: " + player.getName());
        playerCurrentSection.put(player, sectionId);
        playerCurrentPage.put(player, 0);
        plugin.getGuiManager().openSection(player, sectionId);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open search GUI
     */
    private void openSearchGUI(Player player) {
        SearchGui searchGui = new SearchGui(plugin, player);
        activeSearchGuis.put(player, searchGui);
        searchGui.open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open quick sell GUI
     */
    private void openQuickSell(Player player) {
        QuickSellGui quickSellGui = new QuickSellGui(plugin, player);
        activeQuickSellGuis.put(player, quickSellGui);
        quickSellGui.open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open transaction history
     */
    private void openTransactionHistory(Player player) {
        new dev.turjo.easyshopgui.gui.TransactionHistoryGui(plugin, player).open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open settings
     */
    private void openSettings(Player player) {
        new dev.turjo.easyshopgui.gui.ShopSettingsGui(plugin, player).open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Page navigation
     */
    private void handlePreviousPage(Player player) {
        int currentPage = playerCurrentPage.getOrDefault(player, 0);
        Logger.debug("Previous page - Current: " + currentPage);
        
        if (currentPage > 0) {
            playerCurrentPage.put(player, currentPage - 1);
            String sectionId = playerCurrentSection.get(player);
            if (sectionId != null) {
                openSectionWithPage(player, sectionId, currentPage - 1);
                playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            }
        }
    }
    
    private void handleNextPage(Player player) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId == null) return;
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section == null) return;
        
        int currentPage = playerCurrentPage.getOrDefault(player, 0);
        int totalPages = (int) Math.ceil((double) section.getItems().size() / 28);
        
        Logger.debug("Next page - Current: " + currentPage + ", Total: " + totalPages);
        
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
        Logger.debug("Opening section " + sectionId + " page " + page);
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
     * Chat listener for search functionality
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (waitingForSearch.getOrDefault(player, false)) {
            event.setCancelled(true);
            waitingForSearch.remove(player);
            
            String message = event.getMessage().trim();
            
            if (message.equalsIgnoreCase("cancel")) {
                player.sendMessage("§c🔍 Search cancelled!");
                Bukkit.getScheduler().runTask(plugin, () -> {
                    SearchGui searchGui = new SearchGui(plugin, player);
                    activeSearchGuis.put(player, searchGui);
                    searchGui.open();
                });
                return;
            }
            
            // Perform search
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.sendMessage("§b🔍 Searching for: §e'" + message + "'");
                SearchGui searchGui = new SearchGui(plugin, player, message);
                activeSearchGuis.put(player, searchGui);
                searchGui.open();
            });
        }
    }
    
    /**
     * Utility methods
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
    
    private int getPlayerItemCount(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
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
    
    private void playSound(Player player, Sound sound) {
        try {
            player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
        } catch (Exception e) {
            Logger.debug("Could not play sound: " + sound);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Clean up tracking for search
        if (title.contains("SEARCH ITEMS")) {
            if (!waitingForSearch.getOrDefault(player, false)) {
                activeSearchGuis.remove(player);
            }
        } else if (title.contains("QUICK SELL")) {
            activeQuickSellGuis.remove(player);
        }
        
        if (isShopGUI(title)) {
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
}