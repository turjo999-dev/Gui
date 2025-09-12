package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Advanced quick sell GUI with inventory analysis
 */
public class QuickSellGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    public Map<Material, SellableItem> sellableItems = new HashMap<>();
    private double totalValue = 0.0;
    
    // Sell slots (where players can place items)
    public static final int[] SELL_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };
    
    public QuickSellGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        analyzeSellableItems();
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&c&l💸 &e&lQUICK SELL"));
        
        // Fill background (but leave sell slots empty)
        fillBackground(gui);
        
        // Add sell actions
        addSellActions(gui);
        
        // Add navigation
        addNavigation(gui);
        
        // Add instructions
        addInstructions(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background but leave sell slots empty for item placement
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots first
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        
        // Clear sell slots so players can place items
        for (int slot : SELL_SLOTS) {
            gui.setItem(slot, null);
        }
    }
    
    /**
     * Check if slot is a sell slot
     */
    public boolean isSellSlot(int slot) {
        for (int sellSlot : SELL_SLOTS) {
            if (sellSlot == slot) return true;
        }
        return false;
    }
    
    /**
     * Calculate total value of items in sell slots
     */
    public double calculateTotalValue(Inventory gui) {
        double total = 0.0;
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        Map<Material, ShopItem> shopItemMap = new HashMap<>();
        
        // Create lookup map
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                shopItemMap.put(item.getMaterial(), item);
            }
        }
        
        // Calculate value of items in sell slots
        for (int slot : SELL_SLOTS) {
            ItemStack item = gui.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                ShopItem shopItem = shopItemMap.get(item.getType());
                if (shopItem != null && shopItem.getSellPrice() > 0) {
                    total += shopItem.getSellPrice() * item.getAmount();
                }
            }
        }
        
        return total;
    }
    
    /**
     * Sell all items in the GUI
     */
    public void sellAllItems(Inventory gui) {
        double totalEarned = 0.0;
        int itemsSold = 0;
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        Map<Material, ShopItem> shopItemMap = new HashMap<>();
        
        // Create lookup map
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                shopItemMap.put(item.getMaterial(), item);
            }
        }
        
        // Process all items in sell slots
        for (int slot : SELL_SLOTS) {
            ItemStack item = gui.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                ShopItem shopItem = shopItemMap.get(item.getType());
                if (shopItem != null && shopItem.getSellPrice() > 0) {
                    double earned = shopItem.getSellPrice() * item.getAmount();
                    plugin.getEconomyManager().getEconomy().depositPlayer(player, earned);
                    
                    // Record transaction
                    plugin.getTransactionManager().recordTransaction(player, "SELL", shopItem.getDisplayName(), item.getAmount(), earned);
                    
                    totalEarned += earned;
                    itemsSold += item.getAmount();
                    
                    // Clear the slot
                    gui.setItem(slot, null);
                }
            }
        }
        
        if (totalEarned > 0) {
            player.sendMessage("§a💎 Sold " + itemsSold + " items for §6$" + String.format("%.2f", totalEarned) + "!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else {
            player.sendMessage("§c❌ No sellable items found!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
        
        // Refresh GUI
        updateValueDisplay(gui);
    }
    
    /**
     * Clear all items from sell slots and return to player
     */
    public void clearAllItems(Inventory gui) {
        int itemsReturned = 0;
        
        for (int slot : SELL_SLOTS) {
            ItemStack item = gui.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                // Try to add to player inventory
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                if (!leftover.isEmpty()) {
                    // Drop items if inventory is full
                    for (ItemStack leftoverItem : leftover.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftoverItem);
                    }
                }
                itemsReturned += item.getAmount();
                gui.setItem(slot, null);
            }
        }
        
        if (itemsReturned > 0) {
            player.sendMessage("§e📦 Returned " + itemsReturned + " items to your inventory!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
        } else {
            player.sendMessage("§c❌ No items to return!");
        }
        
        // Refresh GUI
        updateValueDisplay(gui);
    }
    
    /**
     * Update the value display in the GUI
     */
    public void updateValueDisplay(Inventory gui) {
        double currentValue = calculateTotalValue(gui);
        
        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l💰 &e&lTOTAL VALUE")
                .setLore(Arrays.asList(
                        "&7▸ &fItems in Sell Slots: &e" + countItemsInSlots(gui),
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", currentValue),
                        "&7▸ &fCurrent Balance: &e$" + String.format("%.2f", plugin.getEconomyManager().getEconomy().getBalance(player)),
                        "&7▸ &fAfter Selling: &a$" + String.format("%.2f", plugin.getEconomyManager().getEconomy().getBalance(player) + currentValue),
                        "",
                        "&a&l💡 &aDrag items into empty slots to sell!"
                ))
                .addGlow(currentValue > 0)
                .build());
    }
    
    /**
     * Count items in sell slots
     */
    private int countItemsInSlots(Inventory gui) {
        int count = 0;
        for (int slot : SELL_SLOTS) {
            ItemStack item = gui.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    /**
     * Analyze player inventory for sellable items (for auto-fill feature)
     */
    private void analyzeSellableItems() {
        sellableItems.clear();
        totalValue = 0.0;
        
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        Map<Material, ShopItem> shopItemMap = new HashMap<>();
        
        // Create lookup map for all shop items
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                shopItemMap.put(item.getMaterial(), item);
            }
        }
        
        // Analyze player inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                ShopItem shopItem = shopItemMap.get(item.getType());
                if (shopItem != null && shopItem.getSellPrice() > 0) {
                    SellableItem sellable = sellableItems.getOrDefault(item.getType(), 
                            new SellableItem(shopItem, 0));
                    sellable.count += item.getAmount();
                    sellableItems.put(item.getType(), sellable);
                    totalValue += shopItem.getSellPrice() * item.getAmount();
                }
            }
        }
        
        Logger.debug("Found " + sellableItems.size() + " types of sellable items worth $" + String.format("%.2f", totalValue));
    }
    
    /**
     * Add sell action buttons
     */
    private void addSellActions(Inventory gui) {
        // Sell all button
        gui.setItem(49, new ItemBuilder(Material.DIAMOND)
                .setName("&a&l💎 &e&lSELL ALL ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fSell all items in sell slots",
                        "&7▸ &fInstant transaction",
                        "&7▸ &fGet money immediately",
                        "",
                        "&a&l➤ &aClick to sell everything!"
                ))
                .addGlow()
                .build());
        
        // Clear all button
        gui.setItem(47, new ItemBuilder(Material.HOPPER)
                .setName("&c&l📦 &e&lCLEAR ALL ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn all items to inventory",
                        "&7▸ &fNo selling, just return",
                        "&7▸ &fSafe item retrieval",
                        "",
                        "&c&l➤ &cClick to clear!"
                ))
                .build());
        
        // Auto-fill from inventory
        gui.setItem(51, new ItemBuilder(Material.CHEST)
                .setName("&b&l🔄 &e&lAUTO-FILL FROM INVENTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fAutomatically fill with sellable items",
                        "&7▸ &fFrom your inventory",
                        "&7▸ &fOnly sellable items",
                        "",
                        "&b&l➤ &bClick to auto-fill!"
                ))
                .build());
    }
    
    /**
     * Auto-fill sell slots with sellable items from inventory
     */
    public void autoFillFromInventory(Inventory gui) {
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        Map<Material, ShopItem> shopItemMap = new HashMap<>();
        
        // Create lookup map
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                shopItemMap.put(item.getMaterial(), item);
            }
        }
        
        int slotIndex = 0;
        int itemsMoved = 0;
        
        // Move sellable items from inventory to sell slots
        for (int i = 0; i < player.getInventory().getSize() && slotIndex < SELL_SLOTS.length; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                ShopItem shopItem = shopItemMap.get(item.getType());
                if (shopItem != null && shopItem.getSellPrice() > 0) {
                    // Move item to sell slot
                    gui.setItem(SELL_SLOTS[slotIndex], item.clone());
                    player.getInventory().setItem(i, null);
                    itemsMoved += item.getAmount();
                    slotIndex++;
                }
            }
        }
        
        if (itemsMoved > 0) {
            player.sendMessage("§b🔄 Auto-filled " + itemsMoved + " sellable items!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
        } else {
            player.sendMessage("§c❌ No sellable items found in inventory!");
        }
        
        updateValueDisplay(gui);
    }
    
    /**
     * Add instructions
     */
    private void addInstructions(Inventory gui) {
        gui.setItem(0, new ItemBuilder(Material.BOOK)
                .setName("&b&l📖 &e&lHOW TO USE")
                .setLore(Arrays.asList(
                        "&7▸ &fDrag items into empty slots",
                        "&7▸ &fOnly sellable items work",
                        "&7▸ &fClick 'Sell All' when ready",
                        "&7▸ &fClick 'Clear All' to get items back",
                        "",
                        "&b&l💡 &bTip: Use Auto-Fill for convenience!"
                ))
                .build());
    }
    
    /**
     * Add navigation
     */
    private void addNavigation(Inventory gui) {
        // Back button
        gui.setItem(45, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
    }
    
    /**
     * Helper class for sellable items
     */
    public static class SellableItem {
        public final ShopItem shopItem;
        public int count;
        
        public SellableItem(ShopItem shopItem, int count) {
            this.shopItem = shopItem;
            this.count = count;
        }
        
        public double getTotalValue() {
            return shopItem.getSellPrice() * count;
        }
    }
}