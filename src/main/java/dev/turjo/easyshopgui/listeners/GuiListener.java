package dev.turjo.easyshopgui.listeners;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SearchGui;
import dev.turjo.easyshopgui.gui.QuickSellGui;
import dev.turjo.easyshopgui.gui.TransactionHistoryGui;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import dev.turjo.easyshopgui.utils.PricingUtil;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI listener with proper click handling and item protection.
 *
 * All "which GUI is this player in" state now lives in GuiManager (see that class for
 * why) - this listener reads from it rather than keeping parallel copies. Previously
 * this class kept its own private tracking maps (activeQuickSellGuis, activeSearchGuis,
 * playerCurrentSection, etc.) that were separate from GuiManager's. The /sell command
 * (aliased /sellgui, /quicksell) only populated GuiManager's map, so a GUI opened that
 * way was invisible to this listener: handleQuickSellGUIClick's lookup always returned
 * null, every click was cancelled with no effect (selling did nothing), and the drag
 * handler's null-guarded block was skipped entirely - meaning it fell through to
 * `return` without ever cancelling the event, so fixed buttons/decorations could be
 * dragged out of the GUI. Opening every GUI type through GuiManager and reading state
 * from GuiManager here removes that whole class of bug rather than special-casing it.
 */
public class GuiListener implements Listener {
    
    private final EasyShopGUI plugin;
    private final Map<Player, Long> lastClickTime = new HashMap<>();
    private final Map<Player, Boolean> waitingForSearch = new HashMap<>();
    // Tracks a pending large-purchase confirmation (item id -> the time it was
    // requested). Backs the "Confirm Large Purchases" preference toggle in
    // ShopSettingsGui: when enabled, a shift-click buy (64x) requires a second
    // matching shift-click within the timeout window before it actually executes.
    private final Map<Player, PendingConfirmation> pendingBuyConfirmations = new HashMap<>();
    private static final long CONFIRMATION_TIMEOUT_MS = 5000;

    private static class PendingConfirmation {
        final String itemId;
        final long requestedAt;
        PendingConfirmation(String itemId, long requestedAt) {
            this.itemId = itemId;
            this.requestedAt = requestedAt;
        }
    }
    
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
            // Special handling for Quick Sell GUI - it allows placing/removing items in
            // its sell slots, so it can't use the "cancel everything" approach below.
            if (title.contains("QUICK SELL")) {
                handleQuickSellGUIClick(event, player, title);
                return;
            }
            
            // For all other shop GUIs, cancel the event completely - these are pure
            // click-to-act menus, nothing should ever leave the GUI via pickup/drag.
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
            } else if (title.contains("ITEM DETAILS")) {
                handleItemDetailClick(player, itemName, event.getSlot());
            } else if (title.contains("SHOP SETTINGS")) {
                handleSettingsClick(player, itemName, event.getSlot());
            } else if (title.contains("AI MARKETPLACE")) {
                handleMarketplaceClick(player, itemName, event.getSlot());
            } else if (title.contains("SECTION") || title.contains("BLOCKS") || title.contains("ORES") ||
                      title.contains("FOOD") || title.contains("REDSTONE") || title.contains("FARMING") ||
                      title.contains("DECORATION") || title.contains("POTIONS")) {
                handleSectionClick(player, itemName, event.getClick(), clickedItem, event.getSlot());
            }
        }
    }
    
    /**
     * Special handling for Quick Sell GUI. Reads from GuiManager's tracking map, not a
     * local one, so it works identically no matter which entry point opened the GUI.
     */
    private void handleQuickSellGUIClick(InventoryClickEvent event, Player player, String title) {
        QuickSellGui quickSellGui = plugin.getGuiManager().getActiveQuickSellGuis().get(player);
        if (quickSellGui == null) {
            Logger.debug("No active QuickSell GUI found for player: " + player.getName());
            event.setCancelled(true);
            return;
        }

        int slot = event.getRawSlot();
        int topInventorySize = event.getView().getTopInventory().getSize();
        ItemStack clickedItem = event.getCurrentItem();
        String itemName = "";

        if (clickedItem != null && clickedItem.getItemMeta() != null) {
            itemName = MessageUtils.stripColor(clickedItem.getItemMeta().getDisplayName());
        }

        Logger.debug("QuickSell GUI click - Raw Slot: " + slot + ", Top Size: " + topInventorySize + ", Item: " + itemName);

        // Allow player inventory interactions (bottom inventory)
        if (slot >= topInventorySize) {
            Logger.debug("Player inventory clicked, allowing interaction");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory() != null) {
                    quickSellGui.updateValueDisplay(player.getOpenInventory().getTopInventory());
                }
            }, 1L);
            return; // Don't cancel - allow normal inventory operations
        }

        // Check if it's a sell slot in the TOP inventory
        if (quickSellGui.isSellSlot(slot)) {
            Logger.debug("Sell slot clicked, allowing interaction: " + slot);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory() != null) {
                    quickSellGui.updateValueDisplay(player.getOpenInventory().getTopInventory());
                }
            }, 1L);
            return; // Allow interaction in sell slots
        }

        // Cancel event for all other slots (buttons, decorations, etc.)
        event.setCancelled(true);

        // Handle button clicks
        if (slot == 49 && itemName.contains("SELL ALL")) {
            Logger.debug("Sell All button clicked");
            quickSellGui.sellAllItems(player.getOpenInventory().getTopInventory());
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        } else if (slot == 47 && itemName.contains("CLEAR ALL")) {
            Logger.debug("Clear All button clicked");
            quickSellGui.clearAllItems(player.getOpenInventory().getTopInventory());
            playSound(player, Sound.ENTITY_ITEM_PICKUP);
        } else if (slot == 51 && itemName.contains("AUTO-FILL")) {
            Logger.debug("Auto-Fill button clicked");
            quickSellGui.autoFillFromInventory(player.getOpenInventory().getTopInventory());
            playSound(player, Sound.ENTITY_ITEM_PICKUP);
        } else if (slot == 45 && itemName.contains("BACK")) {
            Logger.debug("Back button clicked in QuickSell");
            plugin.getGuiManager().getActiveQuickSellGuis().remove(player);
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
        } else if (slot == 4) {
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
            QuickSellGui quickSellGui = plugin.getGuiManager().getActiveQuickSellGuis().get(player);
            if (quickSellGui != null) {
                int topInventorySize = event.getView().getTopInventory().getSize();

                boolean validDrag = event.getRawSlots().stream().allMatch(slot -> {
                    if (slot >= topInventorySize) return true;
                    return quickSellGui.isSellSlot(slot);
                });

                if (!validDrag) {
                    event.setCancelled(true);
                    Logger.debug("Drag event cancelled - involves button/decoration slots");
                } else {
                    Logger.debug("Drag event allowed - valid slots only");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (player.getOpenInventory() != null) {
                            quickSellGui.updateValueDisplay(player.getOpenInventory().getTopInventory());
                        }
                    }, 1L);
                }
            } else {
                // No tracked session for this player (e.g. stale/unexpected state).
                // Fail safe: block the drag rather than silently allowing fixed
                // buttons/decorations to be dragged out, which is what happened here
                // before when this branch had no else and fell through un-cancelled.
                event.setCancelled(true);
                Logger.debug("Drag cancelled - no active QuickSell session found for player: " + player.getName());
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
     * Check if GUI is a shop GUI. Every custom title this plugin opens must be matched
     * here or its clicks/drags won't be protected at all (see ItemDetailGui and
     * MarketplaceGui, which previously used titles that could never match any of these
     * substrings and so were never cancelled).
     */
    private boolean isShopGUI(String title) {
        return title.contains("EASY SHOP GUI") || title.contains("SECTION") || 
               title.contains("SEARCH ITEMS") || title.contains("QUICK SELL") ||
               title.contains("BLOCKS") || title.contains("ORES") || title.contains("FOOD") ||
               title.contains("REDSTONE") || title.contains("FARMING") || title.contains("DECORATION") ||
               title.contains("POTIONS") || title.contains("TRANSACTION HISTORY") ||
               title.contains("SHOP SETTINGS") || title.contains("ITEM DETAILS") ||
               title.contains("AI MARKETPLACE");
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
            case 42: // AI Marketplace
                openMarketplace(player);
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
        String sectionId = plugin.getGuiManager().getPlayerCurrentSection(player);
        if (sectionId != null) {
            ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
            if (section != null) {
                ShopItem shopItem = findShopItem(section, clickedItem, itemName);

                if (shopItem != null) {
                    Logger.debug("Found shop item: " + shopItem.getId() + " for transaction");
                    handleItemTransaction(player, shopItem, clickType);
                } else {
                    Logger.debug("No shop item found for material: " + clickedItem.getType());
                    player.sendMessage("§cThis item is not available for purchase!");
                }
            } else {
                Logger.debug("Section not found: " + sectionId);
            }
        } else {
            Logger.debug("No section ID found for player: " + player.getName());
        }
    }

    /**
     * Resolve the ShopItem a click corresponds to.
     *
     * Matches on material AND display name together, falling back to material-only if
     * no name match is found. Matching on material alone meant that if a section ever
     * contained two entries sharing a Material (e.g. two price tiers of the same block),
     * only the first one in the list could ever be bought or sold - the second was
     * permanently unreachable through the GUI even though it rendered correctly. Every
     * item already shows a unique display name in this GUI, so using it as a second key
     * costs nothing and removes the collision risk entirely for any section built with
     * distinct names (which is required for the GUI to be readable in the first place).
     */
    private ShopItem findShopItem(ShopSection section, ItemStack clickedItem, String itemName) {
        String strippedClicked = MessageUtils.stripColor(itemName);

        ShopItem byNameAndMaterial = section.getItems().stream()
                .filter(item -> item.getMaterial() == clickedItem.getType()
                        && MessageUtils.stripColor(item.getDisplayName()).equalsIgnoreCase(strippedClicked))
                .findFirst()
                .orElse(null);

        if (byNameAndMaterial != null) {
            return byNameAndMaterial;
        }

        // Fallback for any legacy/edge case where the display name didn't line up
        // exactly (e.g. AI marketplace price change altered nothing about the name, so
        // this should rarely trigger, but keeps behaviour from regressing to "nothing
        // matches" if it ever does).
        return section.getItems().stream()
                .filter(item -> item.getMaterial() == clickedItem.getType())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Handle search GUI clicks
     */
    private void handleSearchClick(Player player, String itemName, ClickType clickType, ItemStack clickedItem) {
        SearchGui searchGui = plugin.getGuiManager().getActiveSearchGuis().get(player);

        if (itemName.contains("BACK TO SHOP")) {
            plugin.getGuiManager().getActiveSearchGuis().remove(player);
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
            if (searchGui != null && !searchGui.getSearchResults().isEmpty()) {
                String strippedClicked = MessageUtils.stripColor(itemName);
                ShopItem foundItem = searchGui.getSearchResults().stream()
                        .filter(item -> item.getMaterial() == clickedItem.getType()
                                && MessageUtils.stripColor(item.getDisplayName()).equalsIgnoreCase(strippedClicked))
                        .findFirst()
                        .orElseGet(() -> searchGui.getSearchResults().stream()
                                .filter(item -> item.getMaterial() == clickedItem.getType())
                                .findFirst()
                                .orElse(null));

                if (foundItem != null) {
                    Logger.debug("Processing transaction from search for: " + foundItem.getId());
                    handleItemTransactionFromSearch(player, foundItem, clickType, searchGui);
                } else {
                    Logger.debug("No matching item found in search results for: " + clickedItem.getType());
                }
            }
        }
    }
    
    /**
     * Handle transaction history clicks with proper navigation
     */
    private void handleTransactionHistoryClick(Player player, String itemName, int slot) {
        TransactionHistoryGui historyGui = plugin.getGuiManager().getActiveTransactionGuis().get(player);
        
        Logger.debug("Transaction history click - Item: " + itemName + ", Slot: " + slot);
        
        if (itemName.contains("BACK TO SHOP") && slot == 45) {
            plugin.getGuiManager().getActiveTransactionGuis().remove(player);
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
            player.sendMessage("§e🔍 Filter options coming soon!");
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }

    /**
     * Handle Item Detail GUI clicks (quantity controls, buy/sell, navigation). This GUI
     * previously had no click routing at all - its title never matched isShopGUI(), so
     * the event was never cancelled and none of these buttons ever fired.
     */
    private void handleItemDetailClick(Player player, String itemName, int slot) {
        var itemGui = plugin.getGuiManager().getActiveItemDetailGuis().get(player);
        if (itemGui == null) {
            Logger.debug("No active ItemDetail GUI found for player: " + player.getName());
            return;
        }

        switch (slot) {
            case 10: itemGui.decreaseQuantity(5); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 11: itemGui.decreaseQuantity(1); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 14: itemGui.increaseQuantity(1); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 15: itemGui.increaseQuantity(5); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 19: itemGui.setQuantity(16); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 20: itemGui.setQuantity(32); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 21: itemGui.setQuantity(64); playSound(player, Sound.UI_BUTTON_CLICK); return;
            case 30:
                buyItem(player, itemGui.getShopItem(), itemGui.getQuantity());
                plugin.getGuiManager().openItemDetail(player, itemGui.getSectionId(), itemGui.getShopItem().getId());
                return;
            case 31:
                sellAllItems(player, itemGui.getShopItem());
                plugin.getGuiManager().openItemDetail(player, itemGui.getSectionId(), itemGui.getShopItem().getId());
                return;
            case 32:
                sellItem(player, itemGui.getShopItem(), itemGui.getQuantity());
                plugin.getGuiManager().openItemDetail(player, itemGui.getSectionId(), itemGui.getShopItem().getId());
                return;
            case 36:
                plugin.getGuiManager().getActiveItemDetailGuis().remove(player);
                if (itemGui.getSectionId() != null) {
                    plugin.getGuiManager().openSection(player, itemGui.getSectionId());
                } else {
                    plugin.getGuiManager().openShop(player, "main");
                }
                playSound(player, Sound.UI_BUTTON_CLICK);
                return;
            case 44:
                plugin.getGuiManager().getActiveItemDetailGuis().remove(player);
                player.closeInventory();
                playSound(player, Sound.UI_BUTTON_CLICK);
                return;
            default:
                Logger.debug("Unhandled ItemDetail click - Slot: " + slot + ", Item: " + itemName);
        }
    }

    /**
     * Handle Shop Settings GUI clicks.
     */
    private void handleSettingsClick(Player player, String itemName, int slot) {
        switch (slot) {
            case 40: // Back
                plugin.getGuiManager().openShop(player, "main");
                playSound(player, Sound.UI_BUTTON_CLICK);
                return;
            case 20: // Sound effects toggle
                plugin.getPlayerPreferencesManager().toggleSoundEffects(player);
                boolean soundNowOn = plugin.getPlayerPreferencesManager().isSoundEffectsEnabled(player);
                player.sendMessage("§a⚙ Sound effects " + (soundNowOn ? "enabled" : "disabled") + "!");
                if (soundNowOn) playSound(player, Sound.UI_BUTTON_CLICK);
                plugin.getGuiManager().openSettings(player);
                return;
            case 22: // Purchase confirmation toggle
                plugin.getPlayerPreferencesManager().togglePurchaseConfirm(player);
                boolean confirmNowOn = plugin.getPlayerPreferencesManager().isPurchaseConfirmEnabled(player);
                player.sendMessage("§a⚙ Large purchase confirmation " + (confirmNowOn ? "enabled" : "disabled") + "!");
                playSound(player, Sound.UI_BUTTON_CLICK);
                plugin.getGuiManager().openSettings(player);
                return;
            case 24: // Transaction chat messages toggle
                plugin.getPlayerPreferencesManager().toggleTransactionMessages(player);
                boolean messagesNowOn = plugin.getPlayerPreferencesManager().isTransactionMessagesEnabled(player);
                player.sendMessage("§a⚙ Transaction messages " + (messagesNowOn ? "enabled" : "disabled") + "!");
                playSound(player, Sound.UI_BUTTON_CLICK);
                plugin.getGuiManager().openSettings(player);
                return;
            case 31: // Admin-only debug toggle
                if (player.hasPermission("easyshopgui.admin")) {
                    boolean current = plugin.getConfigManager().getMainConfig().getBoolean("plugin.debug", false);
                    plugin.getConfigManager().getMainConfig().set("plugin.debug", !current);
                    Logger.setDebugMode(!current);
                    player.sendMessage("§a⚙ Debug mode " + (!current ? "enabled" : "disabled") + "!");
                    playSound(player, Sound.UI_BUTTON_CLICK);
                    plugin.getGuiManager().openSettings(player);
                } else {
                    player.sendMessage("§cYou don't have permission to change this setting!");
                    playSound(player, Sound.ENTITY_VILLAGER_NO);
                }
                return;
            default:
                Logger.debug("Unhandled Settings click - Slot: " + slot + ", Item: " + itemName);
        }
    }

    /**
     * Handle AI Marketplace overview GUI clicks.
     */
    private void handleMarketplaceClick(Player player, String itemName, int slot) {
        if (itemName.contains("BACK") || slot == 49) {
            plugin.getGuiManager().openShop(player, "main");
            playSound(player, Sound.UI_BUTTON_CLICK);
            return;
        }
        // Trending/recommendation items are informational; clicking them just confirms
        // with a sound rather than performing an action, since they summarise multiple
        // items at once rather than representing a single purchasable item.
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Check if item is a navigation/decoration item
     */
    private boolean isNavigationItem(String itemName, ItemStack item, int slot) {
        if (slot == 0 || slot == 4 || slot == 8 || slot == 9 || slot == 17 || slot == 18 || 
            slot == 26 || slot == 27 || slot == 35 || slot == 36 || slot == 44 || 
            slot == 45 || slot == 46 || slot == 47 || slot == 48 || slot == 49 || 
            slot == 50 || slot == 51 || slot == 52 || slot == 53) {
            return true;
        }
        
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
                confirmOrBuyLarge(player, shopItem, 64);
                break;
            case SHIFT_RIGHT:
                sellAllItems(player, shopItem);
                break;
            case MIDDLE:
                String sectionId = plugin.getGuiManager().getPlayerCurrentSection(player);
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
     * Buy a large quantity, requiring a second matching shift-click within
     * CONFIRMATION_TIMEOUT_MS if the player has "Confirm Large Purchases" enabled in
     * Settings. If the toggle is off (the default), this behaves exactly as before -
     * a single shift-click buys immediately.
     */
    private void confirmOrBuyLarge(Player player, ShopItem item, int amount) {
        if (!plugin.getPlayerPreferencesManager().isPurchaseConfirmEnabled(player)) {
            buyItem(player, item, amount);
            return;
        }

        PendingConfirmation pending = pendingBuyConfirmations.get(player);
        long now = System.currentTimeMillis();

        if (pending != null && pending.itemId.equals(item.getId()) && (now - pending.requestedAt) <= CONFIRMATION_TIMEOUT_MS) {
            pendingBuyConfirmations.remove(player);
            buyItem(player, item, amount);
        } else {
            pendingBuyConfirmations.put(player, new PendingConfirmation(item.getId(), now));
            double marketPrice = plugin.getAiMarketplace().getCurrentBuyPrice(item.getId());
            if (marketPrice <= 0) marketPrice = item.getBuyPrice();
            double totalPrice = PricingUtil.applyBuyDiscount(player, marketPrice) * amount;
            player.sendMessage("§e⚠ Shift-click again within 5 seconds to confirm buying " + amount + "x " +
                    MessageUtils.stripColor(item.getDisplayName()) + " for §6$" + String.format("%.2f", totalPrice) + "§e.");
            playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING);
        }
    }
    
    /**
     * Buy item implementation. Applies the player's permission-based discount, so the
     * price actually charged matches the price the GUI tooltip promised.
     */
    private void buyItem(Player player, ShopItem item, int amount) {
        Logger.debug("Attempting to buy " + amount + "x " + item.getId());
        
        if (item.getStock() != -1 && item.getStock() < amount) {
            player.sendMessage("§c📦 Not enough stock! Available: " + item.getStock() + ", Requested: " + amount);
            playSound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double marketPrice = plugin.getAiMarketplace().getCurrentBuyPrice(item.getId());
        if (marketPrice <= 0) marketPrice = item.getBuyPrice(); // Fallback to base price
        double discountedPrice = PricingUtil.applyBuyDiscount(player, marketPrice);
        double totalPrice = discountedPrice * amount;
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        Logger.debug("Price: $" + totalPrice + ", Balance: $" + balance);
        
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
        
        try {
            plugin.getEconomyManager().getEconomy().withdrawPlayer(player, totalPrice);
            // Fixed: this used to be `new ItemStack(item.getMaterial(), amount)` - a
            // completely bare stack that ignored any lore/enchantments/potion-type the
            // shop config defined. createItemStack() applies all of that.
            ItemStack itemToGive = item.createItemStack(amount);
            player.getInventory().addItem(itemToGive);
            
            if (item.getStock() != -1) {
                item.setStock(Math.max(0, item.getStock() - amount));
            }
            plugin.getAiMarketplace().recordTransaction(item.getId(), "BUY", amount, totalPrice);
            plugin.getTransactionManager().recordTransaction(player, "BUY", item.getDisplayName(), amount, totalPrice);
            
            int discount = PricingUtil.getDiscountPercent(player);
            String discountSuffix = discount > 0 ? " §7(§a-" + discount + "%§7 discount applied)" : "";
            if (plugin.getPlayerPreferencesManager().isTransactionMessagesEnabled(player)) {
                player.sendMessage("§a💰 Successfully purchased " + amount + "x " + MessageUtils.stripColor(item.getDisplayName()) + 
                                  " §afor §6$" + String.format("%.2f", totalPrice) + "!" + discountSuffix);
            }
            playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            
            Logger.debug("Purchase successful: " + amount + "x " + item.getId());
            
            plugin.getGuiManager().refreshCurrentSection(player);
            
        } catch (Exception e) {
            Logger.error("Error during purchase: " + e.getMessage());
            player.sendMessage("§cError during purchase! Please try again.");
            playSound(player, Sound.ENTITY_VILLAGER_NO);
        }
    }
    
    /**
     * Sell item implementation. Applies the player's permission-based sell multiplier,
     * so the payout actually received matches the price the GUI tooltip promised.
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
        
        double marketPrice = plugin.getAiMarketplace().getCurrentSellPrice(item.getId());
        if (marketPrice <= 0) marketPrice = item.getSellPrice(); // Fallback to base price
        double boostedPrice = PricingUtil.applySellMultiplier(player, marketPrice);
        double totalPrice = boostedPrice * amount;
        
        try {
            removeItemsFromInventory(player, item.getMaterial(), amount);
            plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
            
            plugin.getAiMarketplace().recordTransaction(item.getId(), "SELL", amount, totalPrice);
            plugin.getTransactionManager().recordTransaction(player, "SELL", item.getDisplayName(), amount, totalPrice);
            
            double multiplier = PricingUtil.getSellMultiplier(player);
            String multiplierSuffix = multiplier > 1.0 ? " §7(§a" + multiplier + "x§7 multiplier applied)" : "";
            if (plugin.getPlayerPreferencesManager().isTransactionMessagesEnabled(player)) {
                player.sendMessage("§6💸 Successfully sold " + amount + "x " + MessageUtils.stripColor(item.getDisplayName()) + 
                                  " §6for §a$" + String.format("%.2f", totalPrice) + "!" + multiplierSuffix);
            }
            playSound(player, Sound.ENTITY_VILLAGER_YES);
            
            Logger.debug("Sale successful: " + amount + "x " + item.getId());
            
            plugin.getGuiManager().refreshCurrentSection(player);
            
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
     * Open section with proper tracking (delegates to GuiManager, the single source of
     * truth for section/page state).
     */
    private void openSection(Player player, String sectionId) {
        Logger.debug("Opening section: " + sectionId + " for player: " + player.getName());
        plugin.getGuiManager().openSection(player, sectionId, 0);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open search GUI
     */
    private void openSearchGUI(Player player) {
        plugin.getGuiManager().openSearch(player);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open quick sell GUI
     */
    private void openQuickSell(Player player) {
        plugin.getGuiManager().openQuickSell(player);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open transaction history
     */
    private void openTransactionHistory(Player player) {
        plugin.getGuiManager().openTransactionHistory(player);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Open settings
     */
    private void openSettings(Player player) {
        plugin.getGuiManager().openSettings(player);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }

    /**
     * Open the AI Marketplace overview GUI.
     */
    private void openMarketplace(Player player) {
        plugin.getGuiManager().openMarketplace(player);
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * Page navigation with proper section tracking via GuiManager.
     */
    private void handlePreviousPage(Player player) {
        int currentPage = plugin.getGuiManager().getPlayerCurrentPage(player);
        Logger.debug("Previous page - Current: " + currentPage);
        
        if (currentPage > 0) {
            String sectionId = plugin.getGuiManager().getPlayerCurrentSection(player);
            if (sectionId != null) {
                plugin.getGuiManager().openSection(player, sectionId, currentPage - 1);
            } else {
                Logger.warn("No section ID found for player " + player.getName() + " during previous page navigation");
            }
        } else {
            player.sendMessage("§c📖 You're already on the first page!");
        }
    }
    
    private void handleNextPage(Player player) {
        String sectionId = plugin.getGuiManager().getPlayerCurrentSection(player);
        if (sectionId == null) {
            Logger.warn("No section ID found for player " + player.getName() + " during next page navigation");
            return;
        }
        
        ShopSection section = plugin.getGuiManager().getSections().get(sectionId);
        if (section == null) {
            Logger.warn("Section not found: " + sectionId + " for player " + player.getName());
            return;
        }
        
        int currentPage = plugin.getGuiManager().getPlayerCurrentPage(player);
        int totalPages = (int) Math.ceil((double) section.getItems().size() / 28);
        
        Logger.debug("Next page - Current: " + currentPage + ", Total: " + totalPages);
        
        if (currentPage < totalPages - 1) {
            plugin.getGuiManager().openSection(player, sectionId, currentPage + 1);
        } else {
            player.sendMessage("§c📖 You're already on the last page!");
        }
    }
    
    /**
     * Handle item transaction from search GUI (keeps search open)
     */
    private void handleItemTransactionFromSearch(Player player, ShopItem shopItem, ClickType clickType, SearchGui searchGui) {
        Logger.debug("Handling search transaction - Item: " + shopItem.getId() + ", Click: " + clickType);

        switch (clickType) {
            case LEFT:
                buyItem(player, shopItem, 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (searchGui != null && player.getOpenInventory() != null) {
                        searchGui.open();
                    }
                }, 2L);
                break;
            case RIGHT:
                sellItem(player, shopItem, 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (searchGui != null && player.getOpenInventory() != null) {
                        searchGui.open();
                    }
                }, 2L);
                break;
            case SHIFT_LEFT:
                confirmOrBuyLarge(player, shopItem, 64);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (searchGui != null && player.getOpenInventory() != null) {
                        searchGui.open();
                    }
                }, 2L);
                break;
            case SHIFT_RIGHT:
                sellAllItems(player, shopItem);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (searchGui != null && player.getOpenInventory() != null) {
                        searchGui.open();
                    }
                }, 2L);
                break;
            default:
                Logger.debug("Unhandled click type from search: " + clickType);
                break;
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
                Bukkit.getScheduler().runTask(plugin, () -> plugin.getGuiManager().openSearch(player));
                return;
            }
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.sendMessage("§b🔍 Searching for: §e'" + message + "'");
                SearchGui searchGui = new SearchGui(plugin, player, message);
                plugin.getGuiManager().getActiveSearchGuis().put(player, searchGui);
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
        if (!plugin.getPlayerPreferencesManager().isSoundEffectsEnabled(player)) {
            return;
        }
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
        
        if (title.contains("SEARCH ITEMS")) {
            if (!waitingForSearch.getOrDefault(player, false)) {
                plugin.getGuiManager().getActiveSearchGuis().remove(player);
            }
        } else if (title.contains("QUICK SELL")) {
            plugin.getGuiManager().getActiveQuickSellGuis().remove(player);
        } else if (title.contains("TRANSACTION HISTORY")) {
            plugin.getGuiManager().getActiveTransactionGuis().remove(player);
        } else if (title.contains("ITEM DETAILS")) {
            plugin.getGuiManager().getActiveItemDetailGuis().remove(player);
        } else if (title.contains("SECTION") || title.contains("BLOCKS") || title.contains("ORES") || 
                  title.contains("FOOD") || title.contains("REDSTONE") || title.contains("FARMING") || 
                  title.contains("DECORATION") || title.contains("POTIONS")) {
            plugin.getGuiManager().getActiveSectionGuis().remove(player);
        }
        
        if (isShopGUI(title)) {
            playSound(player, Sound.UI_BUTTON_CLICK);
        }
    }
}
