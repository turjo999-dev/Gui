package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SellGui;
import dev.turjo.easyshopgui.gui.SearchGui;
import dev.turjo.easyshopgui.gui.QuickSellGui;
import dev.turjo.easyshopgui.gui.TransactionHistoryGui;
import dev.turjo.easyshopgui.gui.SectionGui;
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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced GUI listener with proper buying/selling system and GUI protection
 */
public class GuiListener implements Listener {
    
    private final EasyShopGUI plugin;
    private final Map<Player, String> playerCurrentSection = new HashMap<>();
    private final Map<Player, Integer> playerCurrentPage = new HashMap<>();
    private final Map<Player, Long> lastClickTime = new HashMap<>();
    private final Map<Player, Boolean> waitingForSearch = new HashMap<>();
    private final Map<Player, SearchGui> activeSearchGuis = new HashMap<>();
    private final Map<Player, QuickSellGui> activeQuickSellGuis = new HashMap<>();
    private final Map<Player, SellGui> activeSellGuis = new HashMap<>();
    private final Map<Player, TransactionHistoryGui> activeTransactionGuis = new HashMap<>();
    private final Map<Player, SectionGui> activeSectionGuis = new HashMap<>();
    
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
            // Handle SellGui differently - allow item placement in sell slots
            if (title.contains("SELL ITEMS") || title.contains("SELL YOUR ITEMS")) {
                handleSellGuiClick(event, player);
                return;
            }
            
            // Cancel all other shop GUI clicks to prevent item theft
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
     * Handle inventory drag events to prevent item theft
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = MessageUtils.stripColor(event.getView().getTitle());
        
        if (isShopGUI(title)) {
            // Only allow dragging in SellGui sell slots
            if (title.contains("SELL ITEMS")) {
                SellGui sellGui = activeSellGuis.get(player);
                if (sellGui != null) {
                    // Check if drag involves only sell slots
                    boolean validDrag = true;
                    for (int slot : event.getRawSlots()) {
                        if (slot < event.getView().getTopInventory().getSize()) {
                            if (!sellGui.isSellSlot(slot)) {
                                validDrag = false;
                                break;
                            }
                        }
                    }
                    if (!validDrag) {
                        event.setCancelled(true);
                    }
                }
            } else {
                // Cancel all other GUI drags
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Handle SellGui clicks with special item placement logic
     */
    private void handleSellGuiClick(InventoryClickEvent event, Player player) {
        SellGui sellGui = activeSellGuis.get(player);
        if (sellGui == null) {
            Logger.debug("No active SellGui found for player: " + player.getName());
            return;
        }
        
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        String itemName = clickedItem != null && clickedItem.getItemMeta() != null ? 
                         MessageUtils.stripColor(clickedItem.getItemMeta().getDisplayName()) : "";
        
        Logger.debug("SellGui click - Slot: " + slot + ", Item: " + itemName + ", Click: " + event.getClick());
        
        // Handle action buttons
        if (itemName.contains("SELL ALL ITEMS") && slot == 49) {
            event.setCancelled(true);
            sellGui.sellAll();
            playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            return;
        }
        
        if (itemName.contains("SELL VALUABLE ONLY") && slot == 50) {
            event.setCancelled(true);
            sellGui.sellValuableOnly();
            playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
            return;
        }
        
        if (itemName.contains("CLEAR ALL") && slot == 47) {
            event.setCancelled(true);
            sellGui.clearAll();
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        
        if (itemName.contains("RECALCULATE VALUE") && slot == 51) {
            event.setCancelled(true);
            sellGui.open(); // Refresh GUI
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        
        if (itemName.contains("QUICK FILL") && slot == 48) {
            event.setCancelled(true);
            sellGui.quickFill();
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            return;
        }
        
        if (itemName.contains("BACK TO SHOP") && slot == 45) {
            event.setCancelled(true);
            activeSellGuis.remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        
        // Handle sell slot interactions
        if (sellGui.isSellSlot(slot)) {
            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                // Handle item removal
                event.setCancelled(true);
                boolean removeAll = event.getClick() == ClickType.SHIFT_RIGHT;
                sellGui.handleItemRemoval(slot, removeAll);
            } else if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
                // Let the event proceed for item placement
                Logger.debug("Allowing item placement in sell slot: " + slot);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ItemStack newItem = event.getView().getTopInventory().getItem(slot);
                    Logger.debug("Processing placed item: " + (newItem != null ? newItem.getType() : "null"));
                    sellGui.handleItemPlacement(slot, newItem);
                }, 1L);
            }
        } else {
            // Cancel clicks on non-sell slots (GUI elements)
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Logger.debug("Cancelled click on GUI element: " + itemName);
            }
            event.setCancelled(true);
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
               title.contains("TRANSACTION HISTORY") || title.contains("SHOP SETTINGS") ||
               title.contains("SELL ITEMS") || title.contains("SELL YOUR ITEMS");
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
            case 43: // Close or other actions
                player.closeInventory();
                playSound(player, Sound.UI_BUTTON_CLICK);
                break;
            default:
                // Check by material for fallback
                if (itemName.contains("QUICK SELL") || material == Material.HOPPER) {
                    openSellGUI(player);
                    break;
                }
                
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
            playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            return;
        }
        
        if (slot == 53 || itemName.contains("NEXT PAGE")) {
            handleNextPage(player);
            playSound(player, Sound.ITEM_BOOK_PAGE_TURN);
            return;
        }
        
        // Additional navigation slots for different GUI layouts
        if (slot == 49 && (itemName.contains("PAGE INFO") || itemName.contains("INFO"))) {
            return;
        }
        
        if (slot == 4 && (itemName.contains("PLAYER") || clickedItem.getType() == Material.PLAYER_HEAD)) {
            return;
        }
        
        if (slot == 8 && itemName.contains("QUICK ACTIONS")) {
            player.sendMessage("§e⚡ Quick actions coming soon!");
            playSound(player, Sound.UI_BUTTON_CLICK);
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
     * Handle transaction history clicks
     */
    private void handleTransactionHistoryClick(Player player, String itemName, int slot) {
        TransactionHistoryGui historyGui = activeTransactionGuis.get(player);
        
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
               itemName.contains("SETTINGS") || itemName.contains("SELL") ||
               itemName.contains("INFORMATION") || itemName.contains("CLOSE") ||
               itemName.contains("TIPS") || itemName.contains("STATISTICS");
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
        
        double totalPrice = item.getSellPrice() * amount;
        
        try {
            removeItemsFromInventory(player, item.getMaterial(), amount);
            plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
            
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
        } else if (!isNavigationItem(itemName, clickedItem, -1)) {
            // Handle sellable item clicks
            QuickSellGui quickSellGui = activeQuickSellGuis.get(player);
            if (quickSellGui != null) {
                Material material = clickedItem.getType();
                QuickSellGui.SellableItem sellableItem = quickSellGui.sellableItems.get(material);
                
                if (sellableItem != null) {
                    switch (clickType) {
                        case LEFT:
                            quickSellGui.sellItem(material, 1);
                            break;
                        case RIGHT:
                            quickSellGui.sellItem(material, 10);
                            break;
                        case SHIFT_LEFT:
                            quickSellGui.sellItem(material, sellableItem.count / 2);
                            break;
                        case SHIFT_RIGHT:
                            quickSellGui.sellItem(material, sellableItem.count);
                            break;
                    }
                    playSound(player, Sound.ENTITY_VILLAGER_YES);
                }
            }
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
        activeQuickSellGuis.put(player, quickSellGui);
        quickSellGui.open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open sell GUI
     */
    public void openSellGUI(Player player) {
        SellGui sellGui = new SellGui(plugin, player);
        activeSellGuis.put(player, sellGui);
        sellGui.open();
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Get active sell GUI for player
     */
    public SellGui getActiveSellGui(Player player) {
        return activeSellGuis.get(player);
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
        } else if (title.contains("SELL ITEMS") || title.contains("SELL YOUR ITEMS")) {
            activeSellGuis.remove(player);
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