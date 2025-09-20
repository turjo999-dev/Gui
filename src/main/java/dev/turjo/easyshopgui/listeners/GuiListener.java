package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SearchGui;
import dev.turjo.easyshopgui.gui.QuickSellGui;
import dev.turjo.easyshopgui.gui.TransactionHistoryGui;
import dev.turjo.easyshopgui.gui.SectionGui;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Fixed GUI listener with proper click handling and item protection
 */
public class GuiListener implements Listener {
    
    private final EasyShopGUI plugin;
    private final Map<Player, String> playerCurrentSection = new HashMap<>();
    private final Map<Player, Integer> playerCurrentPage = new HashMap<>();
    private final Map<Player, Long> lastClickTime = new HashMap<>();
    private final Map<Player, Boolean> waitingForSearch = new HashMap<>();
    private final Map<Player, SearchGui> activeSearchGuis = new HashMap<>();
    private final Map<Player, QuickSellGui> activeQuickSellGuis = new HashMap<>();
    private final Map<Player, TransactionHistoryGui> activeTransactionGuis = new HashMap<>();
    private final Map<Player, SectionGui> activeSectionGuis = new HashMap<>();
    
    public GuiListener(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        Logger.debug("GUI Click Event - Title: " + title + ", Slot: " + event.getSlot() + ", Click: " + event.getClick());
        
        // Check if it's a shop GUI
        if (isShopGUI(title)) {
            // Special handling for Quick Sell GUI
            if (title.contains("QUICK SELL")) {
                handleQuickSellGUIClick(event, player, title);
                return;
            }
            
            // For all other shop GUIs, cancel the event completely
            event.setCancelled(true);
            Logger.debug("Shop GUI detected, cancelling event: " + title);
            
            // Anti-spam protection
            long currentTime = System.currentTimeMillis();
            if (lastClickTime.containsKey(player) && currentTime - lastClickTime.get(player) < 100) {
                Logger.debug("Click spam detected, ignoring");
                return;
            }
            lastClickTime.put(player, currentTime);
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                Logger.debug("Clicked item is null or air, ignoring");
                return;
            }
            
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) {
                Logger.debug("Item meta is null, ignoring");
                return;
            }
            
            String itemName = meta.getDisplayName() != null ? MessageUtils.stripColor(meta.getDisplayName()) : "";
            Logger.debug("Processing click on item: " + itemName + " in GUI: " + title);
            
            // Route to appropriate handler
            if (title.contains("EASY SHOP GUI")) {
                handleMainShopClick(player, itemName, clickedItem.getType(), event.getSlot());
            } else if (title.contains("SEARCH ITEMS")) {
                handleSearchClick(player, itemName, event.getClick(), clickedItem);
            } else if (title.contains("TRANSACTION HISTORY")) {
                handleTransactionHistoryClick(player, itemName, event.getSlot());
            } else if (title.contains("SECTION") || title.contains("BLOCKS") || title.contains("ORES") || 
                      title.contains("FOOD") || title.contains("REDSTONE") || title.contains("FARMING") || 
                      title.contains("DECORATION")) {
                handleSectionClick(player, itemName, event.getClick(), clickedItem, event.getSlot());
            }
        }
    }
    
    /**
     * Special handling for Quick Sell GUI
     */
    private void handleQuickSellGUIClick(InventoryClickEvent event, Player player, String title) {
        // Cancel ALL clicks in Quick Sell GUI first
        event.setCancelled(true);
        
        QuickSellGui quickSellGui = activeQuickSellGuis.get(player);
        if (quickSellGui == null) {
            Logger.debug("No active QuickSell GUI found for player: " + player.getName());
            return;
        }
        
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        String itemName = "";
        
        if (clickedItem != null && clickedItem.getItemMeta() != null) {
            itemName = MessageUtils.stripColor(clickedItem.getItemMeta().getDisplayName());
        }
        
        Logger.debug("QuickSell GUI click - Slot: " + slot + ", Item: " + itemName);
        
        // Allow item placement/removal in sell slots only
        if (quickSellGui.isSellSlot(slot)) {
            // Allow normal inventory operations in sell slots ONLY
            Logger.debug("Sell slot clicked, allowing interaction: " + slot);
            event.setCancelled(false); // Allow interaction in sell slots
            
            // Update value display after a short delay
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory() != null) {
                    quickSellGui.updateValueDisplay(player.getOpenInventory().getTopInventory());
                }
            }, 1L);
            return;
        }
        
        // Handle button clicks
        if (itemName.contains("SELL ALL ITEMS") && slot == 49) {
            Logger.debug("Sell All button clicked");
            quickSellGui.sellAllItems(player.getOpenInventory().getTopInventory());
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        } else if (itemName.contains("CLEAR ALL ITEMS") && slot == 47) {
            Logger.debug("Clear All button clicked");
            quickSellGui.clearAllItems(player.getOpenInventory().getTopInventory());
            playSound(player, Sound.ENTITY_ITEM_PICKUP);
        } else if (itemName.contains("AUTO-FILL FROM INVENTORY") && slot == 51) {
            Logger.debug("Auto-Fill button clicked");
            quickSellGui.autoFillFromInventory(player.getOpenInventory().getTopInventory());
            playSound(player, Sound.ENTITY_ITEM_PICKUP);
        } else if (itemName.contains("BACK TO SHOP") && slot == 45) {
            Logger.debug("Back button clicked in QuickSell");
            activeQuickSellGuis.remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else {
            Logger.debug("Unhandled QuickSell click - Slot: " + slot + ", Item: " + itemName);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        // Handle Quick Sell GUI drag events
        if (title.contains("QUICK SELL")) {
            QuickSellGui quickSellGui = activeQuickSellGuis.get(player);
            if (quickSellGui != null) {
                // Check if drag involves only sell slots
                boolean onlySellSlots = event.getRawSlots().stream()
                        .allMatch(slot -> slot >= event.getView().getTopInventory().getSize() || quickSellGui.isSellSlot(slot));
                
                if (!onlySellSlots) {
                    event.setCancelled(true);
                    Logger.debug("Drag event cancelled - involves non-sell slots");
                } else {
                    Logger.debug("Drag event allowed in sell slots");
                    // Update value display after drag
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (player.getOpenInventory() != null) {
                            quickSellGui.updateValueDisplay(player.getOpenInventory().getTopInventory());
                        }
                    }, 1L);
                }
            }
            return;
        }
        
        // Cancel ALL other shop GUI drag events
        if (isShopGUI(title)) {
            event.setCancelled(true);
            Logger.debug("Drag event cancelled in shop GUI: " + title);
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
            case 42: // Shop Info (no action needed)
                playSound(player, Sound.UI_BUTTON_CLICK);
                break;
            case 43: // Close
                player.closeInventory();
                playSound(player, Sound.UI_BUTTON_CLICK);
                break;
            default:
                Logger.debug("Unhandled slot click: " + slot);
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
            Logger.debug("Back button clicked, opening main shop");
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        
        if (slot == 45 || itemName.contains("PREVIOUS PAGE")) {
            handlePreviousPage(player);
            playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            return;
        }
        
        if (slot == 53 || itemName.contains("NEXT PAGE")) {
            handleNextPage(player);
            playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            return;
        }
        
        // Skip navigation and decoration items
        if (isNavigationItem(itemName, clickedItem, slot)) {
            Logger.debug("Navigation item clicked, ignoring: " + itemName);
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
        } else if (itemName.contains("POPULAR: DIAMONDS")) {
            if (searchGui != null) {
                searchGui.quickSearch("diamond");
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
        } else if (itemName.contains("POPULAR: IRON")) {
            if (searchGui != null) {
                searchGui.quickSearch("iron");
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
        } else if (itemName.contains("POPULAR: FOOD")) {
            if (searchGui != null) {
                searchGui.quickSearch("food");
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
        } else if (itemName.contains("POPULAR: REDSTONE")) {
            if (searchGui != null) {
                searchGui.quickSearch("redstone");
                playSound(player, Sound.UI_BUTTON_CLICK);
            }
        } else if (!isNavigationItem(itemName, clickedItem, -1)) {
            // Handle search result item clicks
            if (searchGui != null && !searchGui.getSearchResults().isEmpty()) {
                // Find the clicked item in search results
                ShopItem foundItem = searchGui.getSearchResults().stream()
                        .filter(item -> item.getMaterial() == clickedItem.getType())
                        .findFirst()
                        .orElse(null);
                
                if (foundItem != null) {
                    handleItemTransaction(player, foundItem, clickType);
                }
            }
        }
    }
    
    /**
     * Handle transaction history clicks with proper navigation
     */
    private void handleTransactionHistoryClick(Player player, String itemName, int slot) {
        TransactionHistoryGui historyGui = activeTransactionGuis.get(player);
        
        Logger.debug("Transaction history click - Item: " + itemName + ", Slot: " + slot);
        
        if (itemName.contains("BACK TO SHOP") && slot == 45) {
            activeTransactionGuis.remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (itemName.contains("PREVIOUS PAGE") && slot == 48) {
            if (historyGui != null) {
                historyGui.previousPage();
                playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            }
        } else if (itemName.contains("NEXT PAGE") && slot == 50) {
            if (historyGui != null) {
                historyGui.nextPage();
                playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            }
        } else if (itemName.contains("FILTER OPTIONS") && slot == 46) {
            // TODO: Implement filter functionality
            player.sendMessage("§e🔍 Filter options coming soon!");
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
    
    /**
     * Check if item is a navigation/decoration item
     */
    private boolean isNavigationItem(String itemName, ItemStack item, int slot) {
        // Check by slot position (navigation slots)
        if (slot == 0 || slot == 4 || slot == 8 || slot == 9 || slot == 17 || slot == 18 || 
            slot == 26 || slot == 27 || slot == 35 || slot == 36 || slot == 44 || 
            slot == 45 || slot == 46 || slot == 47 || slot == 48 || slot == 49 || 
            slot == 50 || slot == 51 || slot == 52 || slot == 53) {
            return true;
        }
        
        // Check by material (background items)
        Material material = item.getType();
        if (material == Material.BLACK_STAINED_GLASS_PANE ||
            material == Material.GRAY_STAINED_GLASS_PANE ||
            material == Material.BLUE_STAINED_GLASS_PANE ||
            material == Material.RED_STAINED_GLASS_PANE ||
            material == Material.YELLOW_STAINED_GLASS_PANE ||
            material == Material.CYAN_STAINED_GLASS_PANE ||
            material == Material.GREEN_STAINED_GLASS_PANE ||
            material == Material.PURPLE_STAINED_GLASS_PANE ||
            material == Material.PINK_STAINED_GLASS_PANE ||
            material == Material.LIGHT_BLUE_STAINED_GLASS_PANE ||
            material == Material.LIGHT_GRAY_STAINED_GLASS_PANE ||
            material == Material.LIME_STAINED_GLASS_PANE ||
            material == Material.MAGENTA_STAINED_GLASS_PANE ||
            material == Material.ORANGE_STAINED_GLASS_PANE ||
            material == Material.WHITE_STAINED_GLASS_PANE ||
            material == Material.SPECTRAL_ARROW ||
            material == Material.ARROW ||
            material == Material.BOOK ||
            material == Material.KNOWLEDGE_BOOK ||
            material == Material.EMERALD ||
            material == Material.COMPASS ||
            material == Material.HOPPER ||
            material == Material.COMPARATOR ||
            material == Material.PAPER ||
            material == Material.BARRIER ||
            material == Material.PLAYER_HEAD) {
            return true;
        }
        
        // Check by name content
        return itemName.contains("PAGE INFO") || itemName.contains("PLAYER") ||
               itemName.contains("QUICK ACTIONS") || itemName.contains("BACK") ||
               itemName.contains("PREVIOUS") || itemName.contains("NEXT") ||
               itemName.contains("SEARCH") || itemName.contains("HISTORY") ||
               itemName.contains("SETTINGS") || itemName.contains("INFORMATION") || 
               itemName.contains("CLOSE") || itemName.contains("TIPS") || 
               itemName.contains("STATISTICS") || itemName.contains("HOW TO USE") ||
               itemName.contains("TOTAL VALUE") || itemName.contains("SELL ALL") ||
               itemName.contains("CLEAR ALL") || itemName.contains("AUTO-FILL");
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
        
        // Check stock availability
        if (item.getStock() != -1 && item.getStock() < amount) {
            player.sendMessage("§c📦 Not enough stock! Available: " + item.getStock() + ", Requested: " + amount);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        // Calculate total price
        double currentPrice = plugin.getAiMarketplace().getCurrentBuyPrice(item.getId());
        if (currentPrice <= 0) currentPrice = item.getBuyPrice(); // Fallback to base price
        double totalPrice = currentPrice * amount;
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
            
            // Update stock and AI marketplace
            if (item.getStock() != -1) {
                item.setStock(Math.max(0, item.getStock() - amount));
            }
            plugin.getAiMarketplace().recordTransaction(item.getId(), "BUY", amount, totalPrice);
            
            // Record transaction
            plugin.getTransactionManager().recordTransaction(player, "BUY", item.getDisplayName(), amount, totalPrice);
            
            player.sendMessage("§a💰 Successfully purchased " + amount + "x " + MessageUtils.stripColor(item.getDisplayName()) + 
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
            player.sendMessage("§c📦 You don't have enough " + MessageUtils.stripColor(item.getDisplayName()) + 
                             "! Have " + playerItemCount + ", need " + amount);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double currentPrice = plugin.getAiMarketplace().getCurrentSellPrice(item.getId());
        if (currentPrice <= 0) currentPrice = item.getSellPrice(); // Fallback to base price
        double totalPrice = currentPrice * amount;
        
        try {
            removeItemsFromInventory(player, item.getMaterial(), amount);
            plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
            
            // Update AI marketplace
            plugin.getAiMarketplace().recordTransaction(item.getId(), "SELL", amount, totalPrice);
            
            // Record transaction
            plugin.getTransactionManager().recordTransaction(player, "SELL", item.getDisplayName(), amount, totalPrice);
            
            player.sendMessage("§6💸 Successfully sold " + amount + "x " + MessageUtils.stripColor(item.getDisplayName()) + 
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
            player.sendMessage("§c📦 You don't have any " + MessageUtils.stripColor(item.getDisplayName()) + " to sell!");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
        }
    }
    
    /**
     * Open section with proper tracking
     */
    private void openSection(Player player, String sectionId) {
        Logger.debug("Opening section: " + sectionId + " for player: " + player.getName());
        playerCurrentSection.put(player, sectionId);
        playerCurrentPage.put(player, 0);
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section != null) {
            SectionGui sectionGui = new SectionGui(plugin, player, section);
            activeSectionGuis.put(player, sectionGui);
            sectionGui.open();
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else {
            player.sendMessage("§cSection not found: " + sectionId);
        }
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
        
        // Store in the same tracking system
        activeQuickSellGuis.put(player, quickSellGui);
        plugin.getGuiManager().getActiveQuickSellGuis().put(player, quickSellGui);
        
        quickSellGui.open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open transaction history
     */
    private void openTransactionHistory(Player player) {
        TransactionHistoryGui historyGui = new TransactionHistoryGui(plugin, player);
        activeTransactionGuis.put(player, historyGui);
        historyGui.open();
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
     * Page navigation with proper section tracking
     */
    private void handlePreviousPage(Player player) {
        int currentPage = playerCurrentPage.getOrDefault(player, 0);
        Logger.debug("Previous page - Current: " + currentPage);
        
        if (currentPage > 0) {
            playerCurrentPage.put(player, currentPage - 1);
            String sectionId = playerCurrentSection.get(player);
            if (sectionId != null) {
                openSectionWithPage(player, sectionId, currentPage - 1);
            } else {
                Logger.warn("No section ID found for player " + player.getName() + " during previous page navigation");
            }
        } else {
            player.sendMessage("§c📖 You're already on the first page!");
        }
    }
    
    private void handleNextPage(Player player) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId == null) {
            Logger.warn("No section ID found for player " + player.getName() + " during next page navigation");
            return;
        }
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section == null) {
            Logger.warn("Section not found: " + sectionId + " for player " + player.getName());
            return;
        }
        
        int currentPage = playerCurrentPage.getOrDefault(player, 0);
        int totalPages = (int) Math.ceil((double) section.getItems().size() / 28);
        
        Logger.debug("Next page - Current: " + currentPage + ", Total: " + totalPages);
        
        if (currentPage < totalPages - 1) {
            playerCurrentPage.put(player, currentPage + 1);
            openSectionWithPage(player, sectionId, currentPage + 1);
        } else {
            player.sendMessage("§c📖 You're already on the last page!");
        }
    }
    
    /**
     * Open section with specific page
     */
    private void openSectionWithPage(Player player, String sectionId, int page) {
        Logger.debug("Opening section " + sectionId + " page " + page);
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section != null) {
            SectionGui sectionGui = new SectionGui(plugin, player, section);
            sectionGui.setCurrentPage(page);
            activeSectionGuis.put(player, sectionGui);
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
        
        // Clean up tracking for different GUIs
        if (title.contains("SEARCH ITEMS")) {
            if (!waitingForSearch.getOrDefault(player, false)) {
                activeSearchGuis.remove(player);
            }
        } else if (title.contains("QUICK SELL")) {
            activeQuickSellGuis.remove(player);
        } else if (title.contains("TRANSACTION HISTORY")) {
            activeTransactionGuis.remove(player);
        } else if (title.contains("SECTION") || title.contains("BLOCKS") || title.contains("ORES") || 
                  title.contains("FOOD") || title.contains("REDSTONE") || title.contains("FARMING") || 
                  title.contains("DECORATION")) {
            activeSectionGuis.remove(player);
        }
        
        if (isShopGUI(title)) {
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
}