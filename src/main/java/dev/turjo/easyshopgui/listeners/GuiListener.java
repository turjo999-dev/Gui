package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SearchGui;
import dev.turjo.easyshopgui.gui.QuickSellGui;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
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
 * Enhanced GUI listener with search and quick sell support
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
        if (lastClickTime.containsKey(player) && currentTime - lastClickTime.get(player) < 150) {
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
            if (meta == null || meta.getDisplayName() == null) return;
            
            String itemName = MessageUtils.stripColor(meta.getDisplayName());
            
            Logger.debug("GUI Click - Title: " + title + ", Item: " + itemName + ", Slot: " + event.getSlot());
            
            // Route to appropriate handler
            if (title.contains("EASY SHOP GUI")) {
                handleMainShopClick(player, itemName, clickedItem.getType(), event.getSlot());
            } else if (title.contains("SEARCH ITEMS")) {
                handleSearchClick(player, itemName, event.getClick(), clickedItem);
            } else if (title.contains("QUICK SELL")) {
                handleQuickSellClick(player, itemName, event.getClick(), clickedItem);
            } else if (title.contains("SECTION") || title.contains("✦")) {
                handleSectionClick(player, itemName, event.getClick(), clickedItem);
            }
        }
    }
    
    /**
     * Check if GUI is a shop GUI
     */
    private boolean isShopGUI(String title) {
        return title.contains("EASY SHOP GUI") || title.contains("SECTION") || 
               title.contains("SEARCH ITEMS") || title.contains("QUICK SELL") ||
               title.contains("✦") || title.contains("TRANSACTION HISTORY") ||
               title.contains("SHOP SETTINGS");
    }
    
    /**
     * Handle main shop GUI clicks with stunning diamond pattern
     */
    private void handleMainShopClick(Player player, String itemName, Material material, int slot) {
        Logger.debug("Main shop click - Slot: " + slot + ", Item: " + itemName);
        
        // Diamond pattern navigation (slots: 20, 22, 24, 29, 31, 33, 40)
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
            case 31: // Farming (center)
                openSection(player, "farming");
                break;
            case 33: // Decoration
                openSection(player, "decoration");
                break;
            case 40: // Potions
                openSection(player, "potions");
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
                // Fallback: check by item name/material
                handleFallbackNavigation(player, itemName, material);
                break;
        }
    }
    
    /**
     * Fallback navigation for item name/material matching
     */
    private void handleFallbackNavigation(Player player, String itemName, Material material) {
        if (itemName.contains("BLOCKS") || material == Material.STONE) {
            openSection(player, "blocks");
        } else if (itemName.contains("ORES") || itemName.contains("MINERALS") || material == Material.DIAMOND_ORE) {
            openSection(player, "ores");
        } else if (itemName.contains("FOOD") || material == Material.GOLDEN_APPLE) {
            openSection(player, "food");
        } else if (itemName.contains("REDSTONE") || material == Material.REDSTONE) {
            openSection(player, "redstone");
        } else if (itemName.contains("FARMING") || material == Material.WHEAT) {
            openSection(player, "farming");
        } else if (itemName.contains("DECORATION") || material == Material.FLOWER_POT) {
            openSection(player, "decoration");
        } else if (itemName.contains("POTIONS") || material == Material.POTION) {
            openSection(player, "potions");
        } else if (itemName.contains("SEARCH")) {
            openSearchGUI(player);
        } else if (itemName.contains("QUICK SELL")) {
            openQuickSell(player);
        } else if (itemName.contains("TRANSACTION")) {
            openTransactionHistory(player);
        } else if (itemName.contains("SETTINGS")) {
            openSettings(player);
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
        } else if (itemName.contains("POPULAR:")) {
            // Handle popular searches
            if (itemName.contains("DIAMONDS")) {
                performSearch(player, "diamond");
            } else if (itemName.contains("IRON")) {
                performSearch(player, "iron");
            } else if (itemName.contains("FOOD")) {
                performSearch(player, "food");
            } else if (itemName.contains("REDSTONE")) {
                performSearch(player, "redstone");
            }
        } else if (itemName.contains("SMART SEARCH")) {
            // Start search mode
            waitingForSearch.put(player, true);
            player.closeInventory();
            player.sendMessage("§b🔍 Type your search query in chat! (Type 'cancel' to cancel)");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else {
            // Handle item transactions in search results
            if (searchGui != null && !searchGui.getSearchResults().isEmpty()) {
                handleSearchItemTransaction(player, clickType, clickedItem, searchGui);
            }
        }
    }
    
    /**
     * Handle quick sell GUI clicks
     */
    private void handleQuickSellClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        QuickSellGui quickSellGui = activeQuickSellGuis.get(player);
        
        if (itemName.contains("BACK TO SHOP")) {
            activeQuickSellGuis.remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("SELL EVERYTHING")) {
            if (quickSellGui != null) {
                quickSellGui.sellAll();
                playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            }
        } else if (itemName.contains("REFRESH INVENTORY")) {
            openQuickSell(player); // Refresh
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("SELL VALUABLE ONLY")) {
            // TODO: Implement sell valuable only
            player.sendMessage("§6⭐ Selling valuable items only...");
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        } else {
            // Handle individual item selling
            if (quickSellGui != null) {
                handleQuickSellItemClick(player, clickType, clickedItem, quickSellGui);
            }
        }
    }
    
    /**
     * Handle quick sell item clicks
     */
    private void handleQuickSellItemClick(Player player, ClickType clickType, ItemStack clickedItem, QuickSellGui quickSellGui) {
        Material material = clickedItem.getType();
        
        switch (clickType) {
            case LEFT:
                quickSellGui.sellItem(material, 1);
                playSound(player, Sound.ENTITY_VILLAGER_YES);
                break;
            case RIGHT:
                quickSellGui.sellItem(material, 10);
                playSound(player, Sound.ENTITY_VILLAGER_YES);
                break;
            case SHIFT_LEFT:
                // Sell half
                QuickSellGui.SellableItem sellable = quickSellGui.sellableItems.get(material);
                if (sellable != null) {
                    quickSellGui.sellItem(material, sellable.count / 2);
                    playSound(player, Sound.ENTITY_VILLAGER_YES);
                }
                break;
            case SHIFT_RIGHT:
                // Sell all of this type
                QuickSellGui.SellableItem sellableAll = quickSellGui.sellableItems.get(material);
                if (sellableAll != null) {
                    quickSellGui.sellItem(material, sellableAll.count);
                    playSound(player, Sound.ENTITY_VILLAGER_YES);
                }
                break;
        }
    }
    
    /**
     * Handle search item transactions
     */
    private void handleSearchItemTransaction(Player player, ClickType clickType, ItemStack clickedItem, SearchGui searchGui) {
        // Find the shop item from search results
        ShopItem shopItem = null;
        for (ShopItem item : searchGui.getSearchResults()) {
            if (item.getMaterial() == clickedItem.getType()) {
                shopItem = item;
                break;
            }
        }
        
        if (shopItem != null) {
            handleItemTransaction(player, shopItem, clickType);
        }
    }
    
    /**
     * Handle section GUI clicks
     */
    private void handleSectionClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        if (itemName.contains("BACK TO SHOP")) {
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("PREVIOUS PAGE")) {
            handlePreviousPage(player);
        } else if (itemName.contains("NEXT PAGE")) {
            handleNextPage(player);
        } else if (itemName.contains("QUICK ACTIONS")) {
            player.sendMessage("§d⚡ Quick actions coming soon!");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (!isNavigationItem(itemName, clickedItem)) {
            // Handle item transactions
            handleSectionItemTransaction(player, clickType, clickedItem);
        }
    }
    
    /**
     * Check if item is a navigation item
     */
    private boolean isNavigationItem(String itemName, ItemStack item) {
        return itemName.contains("PAGE INFO") || itemName.contains("PLAYER") ||
               itemName.contains("QUICK ACTIONS") || 
               item.getType() == Material.BLACK_STAINED_GLASS_PANE ||
               item.getType() == Material.GRAY_STAINED_GLASS_PANE ||
               item.getType() == Material.BLUE_STAINED_GLASS_PANE ||
               item.getType() == Material.RED_STAINED_GLASS_PANE ||
               item.getType() == Material.YELLOW_STAINED_GLASS_PANE;
    }
    
    /**
     * Handle section item transactions
     */
    private void handleSectionItemTransaction(Player player, ClickType clickType, ItemStack clickedItem) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId == null) return;
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section == null) return;
        
        // Find the item by material
        ShopItem shopItem = section.getItems().stream()
                .filter(item -> item.getMaterial() == clickedItem.getType())
                .findFirst()
                .orElse(null);
        
        if (shopItem != null) {
            handleItemTransaction(player, shopItem, clickType);
        }
    }
    
    /**
     * Handle item transactions (universal)
     */
    private void handleItemTransaction(Player player, ShopItem shopItem, ClickType clickType) {
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
                plugin.getGuiManager().openItemDetail(player, playerCurrentSection.get(player), shopItem.getId());
                break;
        }
    }
    
    /**
     * Open section with proper tracking
     */
    private void openSection(Player player, String sectionId) {
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
     * Perform search
     */
    private void performSearch(Player player, String query) {
        SearchGui searchGui = new SearchGui(plugin, player, query);
        activeSearchGuis.put(player, searchGui);
        searchGui.open();
        playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
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
                performSearch(player, message);
            });
        }
    }
    
    /**
     * Buy item implementation
     */
    private void buyItem(Player player, ShopItem item, int amount) {
        double totalPrice = item.getBuyPrice() * amount;
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        if (balance < totalPrice) {
            player.sendMessage("§c💰 Insufficient funds! Need $" + String.format("%.2f", totalPrice) + 
                             " but only have $" + String.format("%.2f", balance));
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        if (!hasInventorySpace(player, amount)) {
            player.sendMessage("§c📦 Not enough inventory space!");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        // Process transaction
        plugin.getEconomyManager().getEconomy().withdrawPlayer(player, totalPrice);
        player.getInventory().addItem(new ItemStack(item.getMaterial(), amount));
        
        player.sendMessage("§a💰 Purchased " + amount + "x " + item.getDisplayName() + 
                          " §afor §6$" + String.format("%.2f", totalPrice) + "!");
        playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        
        refreshCurrentGUI(player);
    }
    
    /**
     * Sell item implementation
     */
    private void sellItem(Player player, ShopItem item, int amount) {
        int playerItemCount = getPlayerItemCount(player, item.getMaterial());
        
        if (playerItemCount < amount) {
            player.sendMessage("§c📦 You don't have enough " + item.getDisplayName() + 
                             "! Have " + playerItemCount + ", need " + amount);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double totalPrice = item.getSellPrice() * amount;
        
        removeItemsFromInventory(player, item.getMaterial(), amount);
        plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
        
        player.sendMessage("§6💸 Sold " + amount + "x " + item.getDisplayName() + 
                          " §6for §a$" + String.format("%.2f", totalPrice) + "!");
        playSound(player, Sound.ENTITY_VILLAGER_YES);
        
        refreshCurrentGUI(player);
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
     * Page navigation
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
        // Refresh search GUI
        if (activeSearchGuis.containsKey(player)) {
            SearchGui searchGui = activeSearchGuis.get(player);
            searchGui.performSearch();
            searchGui.open();
            return;
        }
        
        // Refresh quick sell GUI
        if (activeQuickSellGuis.containsKey(player)) {
            openQuickSell(player);
            return;
        }
        
        // Refresh section GUI
        String sectionId = playerCurrentSection.get(player);
        if (sectionId != null) {
            int currentPage = playerCurrentPage.getOrDefault(player, 0);
            openSectionWithPage(player, sectionId, currentPage);
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
            // Ignore sound errors
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Clean up tracking
        if (title.contains("SEARCH ITEMS")) {
            // Don't remove if waiting for search input
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