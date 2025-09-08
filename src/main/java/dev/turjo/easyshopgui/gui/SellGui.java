package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Perfect interactive sell GUI with proper event handling
 */
public class SellGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private final Map<Integer, SellableItem> sellSlots = new HashMap<>();
    private double totalValue = 0.0;
    private int totalItems = 0;
    
    // Define sell slots (where players can place items) - 3x7 grid
    private final int[] SELL_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };
    
    // Define button slots (protected from player interaction)
    private final Set<Integer> BUTTON_SLOTS = Set.of(4, 47, 48, 49, 50, 51, 45, 53);
    
    public SellGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Logger.debug("Creating SellGui for player: " + player.getName());
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&c&l💸 &e&lSELL YOUR ITEMS"));
        
        // Fill background and setup GUI
        fillBackground(gui);
        addInstructions(gui);
        addSellSlots(gui);
        addActionButtons(gui);
        addNavigation(gui);
        
        player.openInventory(gui);
        Logger.debug("Opened SellGui for player: " + player.getName());
    }
    
    /**
     * Fill background with beautiful design
     */
    private void fillBackground(Inventory gui) {
        Logger.debug("Filling background for SellGui");
        
        ItemStack background = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        ItemStack border = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots first
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        
        // Add orange border
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }
    }
    
    /**
     * Add instructions and info
     */
    private void addInstructions(Inventory gui) {
        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l💰 &e&lSELL INTERFACE")
                .setLore(Arrays.asList(
                        "&7▸ &fDrag items from your inventory",
                        "&7▸ &fPlace them in the empty slots below",
                        "&7▸ &fSellable items will show their value",
                        "&7▸ &fUnsellable items will be returned",
                        "&7▸ &fClick 'Sell All' when ready",
                        "",
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", totalValue),
                        "&7▸ &fTotal Items: &e" + totalItems,
                        "",
                        "&a&l💡 &aTip: Drag and drop items into slots!"
                ))
                .addGlow()
                .build());
    }
    
    /**
     * Add sell slots (clear them for item placement)
     */
    private void addSellSlots(Inventory gui) {
        // Clear sell slots for item placement
        for (int slot : SELL_SLOTS) {
            if (sellSlots.containsKey(slot)) {
                SellableItem sellableItem = sellSlots.get(slot);
                gui.setItem(slot, createSellSlotItem(sellableItem));
            } else {
                gui.setItem(slot, null); // Empty slot for placing items
            }
        }
    }
    
    /**
     * Create sell slot item display
     */
    private ItemStack createSellSlotItem(SellableItem sellableItem) {
        ShopItem shopItem = sellableItem.shopItem;
        double itemValue = shopItem.getSellPrice() * sellableItem.amount;
        
        return new ItemBuilder(shopItem.getMaterial())
                .setName("&6&l💸 " + shopItem.getDisplayName())
                .setLore(Arrays.asList(
                        "&7▸ &fQuantity: &e" + sellableItem.amount,
                        "&7▸ &fPrice Each: &a$" + String.format("%.2f", shopItem.getSellPrice()),
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", itemValue),
                        "",
                        "&e&l⚡ ACTIONS:",
                        "&c▸ Right Click: &fRemove from sale",
                        "&c▸ Shift + Right: &fRemove all"
                ))
                .setAmount(Math.min(sellableItem.amount, 64))
                .addGlow()
                .build());
    }
    
    /**
     * Add action buttons
     */
    private void addActionButtons(Inventory gui) {
        // Sell all button
        gui.setItem(49, new ItemBuilder(totalValue > 0 ? Material.DIAMOND : Material.COAL)
                .setName(totalValue > 0 ? "&a&l💎 &e&lSELL ALL ITEMS" : "&c&l❌ &e&lNO ITEMS TO SELL")
                .setLore(Arrays.asList(
                        "&7▸ &fSell all placed items",
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", totalValue),
                        "&7▸ &fTotal Items: &e" + totalItems,
                        "&7▸ &fDifferent Types: &e" + sellSlots.size(),
                        "",
                        totalValue > 0 ? "&a&l➤ &aClick to sell everything!" : "&c&l➤ &cPlace items first!"
                ))
                .addGlow(totalValue > 0)
                .build());
        
        // Clear all button
        gui.setItem(47, new ItemBuilder(Material.BARRIER)
                .setName("&c&l🗑 &e&lCLEAR ALL")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn all items to inventory",
                        "&7▸ &fClear all sell slots",
                        "&7▸ &fStart over fresh",
                        "",
                        "&c&l➤ &cClick to clear everything!"
                ))
                .build());
        
        // Refresh/Calculate button
        gui.setItem(51, new ItemBuilder(Material.HOPPER)
                .setName("&b&l🔄 &e&lRECALCULATE VALUE")
                .setLore(Arrays.asList(
                        "&7▸ &fRecalculate total value",
                        "&7▸ &fUpdate sellable status",
                        "&7▸ &fRefresh display",
                        "",
                        "&b&l➤ &bClick to refresh!"
                ))
                .build());
        
        // Quick fill from inventory
        gui.setItem(48, new ItemBuilder(Material.CHEST)
                .setName("&e&l📦 &e&lQUICK FILL")
                .setLore(Arrays.asList(
                        "&7▸ &fAutomatically fill with sellable items",
                        "&7▸ &fScans your inventory",
                        "&7▸ &fAdds all sellable items",
                        "",
                        "&e&l➤ &eClick for quick fill!"
                ))
                .build());
        
        // Sell valuable only
        gui.setItem(50, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&6&l⭐ &e&lSELL VALUABLE ONLY")
                .setLore(Arrays.asList(
                        "&7▸ &fSell items worth $5+ each",
                        "&7▸ &fKeep cheap items",
                        "&7▸ &fSmart filtering",
                        "",
                        "&6&l➤ &6Click to sell valuable!"
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
                        "&7▸ &fItems will be returned",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
        
        // Help button
        gui.setItem(53, new ItemBuilder(Material.KNOWLEDGE_BOOK)
                .setName("&b&l❓ &e&lHELP & TIPS")
                .setLore(Arrays.asList(
                        "&7▸ &fHow to use the sell GUI:",
                        "&a  1. &fDrag items from inventory",
                        "&a  2. &fPlace in empty slots",
                        "&a  3. &fCheck total value",
                        "&a  4. &fClick 'Sell All'",
                        "",
                        "&b&l💡 &bTips:",
                        "&7  • &fOnly sellable items work",
                        "&7  • &fUnsellable items return",
                        "&7  • &fRight-click to remove items"
                ))
                .build());
    }
    
    /**
     * Handle button clicks
     */
    public boolean handleClick(int slot, ItemStack clickedItem) {
        Logger.debug("SellGui click - Slot: " + slot + ", Item: " + (clickedItem != null ? clickedItem.getType() : "null"));
        
        // Check if it's a button slot
        if (BUTTON_SLOTS.contains(slot)) {
            String itemName = clickedItem != null && clickedItem.getItemMeta() != null ? 
                             MessageUtils.stripColor(clickedItem.getItemMeta().getDisplayName()) : "";
            
            Logger.debug("Button clicked: " + itemName);
            
            switch (slot) {
                case 49: // Sell All
                    if (itemName.contains("SELL ALL ITEMS")) {
                        sellAll();
                        return true;
                    }
                    break;
                case 47: // Clear All
                    if (itemName.contains("CLEAR ALL")) {
                        clearAll();
                        return true;
                    }
                    break;
                case 51: // Recalculate
                    if (itemName.contains("RECALCULATE")) {
                        recalculateValue();
                        open(); // Refresh GUI
                        playSound(Sound.UI_BUTTON_CLICK);
                        return true;
                    }
                    break;
                case 48: // Quick Fill
                    if (itemName.contains("QUICK FILL")) {
                        quickFill();
                        return true;
                    }
                    break;
                case 50: // Sell Valuable
                    if (itemName.contains("SELL VALUABLE")) {
                        sellValuableOnly();
                        return true;
                    }
                    break;
                case 45: // Back
                    if (itemName.contains("BACK")) {
                        clearAll(); // Return items before closing
                        plugin.getGuiManager().openShop(player, "main");
                        playSound(Sound.UI_BUTTON_CLICK);
                        return true;
                    }
                    break;
                case 53: // Help
                    if (itemName.contains("HELP")) {
                        player.sendMessage("§b💡 SellGui Help: Drag items from your inventory into the empty slots to sell them!");
                        playSound(Sound.UI_BUTTON_CLICK);
                        return true;
                    }
                    break;
            }
            return true; // Always cancel button clicks
        }
        
        return false; // Allow other clicks
    }
    
    /**
     * Check if slot is a sell slot
     */
    public boolean isSellSlot(int slot) {
        for (int sellSlot : SELL_SLOTS) {
            if (sellSlot == slot) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if slot is a button slot
     */
    public boolean isButtonSlot(int slot) {
        return BUTTON_SLOTS.contains(slot);
    }
    
    /**
     * Handle item placement in sell slots
     */
    public void handleItemPlacement(int slot, ItemStack item) {
        Logger.debug("Handling item placement in slot " + slot + " with item: " + (item != null ? item.getType() : "null"));
        
        if (!isSellSlot(slot)) {
            Logger.debug("Slot " + slot + " is not a sell slot");
            return;
        }
        
        if (item == null || item.getType() == Material.AIR) {
            // Remove item from sell slots
            if (sellSlots.containsKey(slot)) {
                SellableItem removed = sellSlots.remove(slot);
                player.sendMessage("§c📦 Removed " + removed.amount + "x " + 
                                 MessageUtils.stripColor(removed.shopItem.getDisplayName()) + " from sale");
                playSound(Sound.UI_BUTTON_CLICK);
            }
            recalculateValue();
            return;
        }
        
        // Check if item is sellable
        ShopItem shopItem = findSellableItem(item.getType());
        if (shopItem != null && shopItem.getSellPrice() > 0) {
            SellableItem sellableItem = new SellableItem(shopItem, item.getAmount());
            sellSlots.put(slot, sellableItem);
            
            double itemValue = shopItem.getSellPrice() * item.getAmount();
            player.sendMessage("§a✓ Added " + item.getAmount() + "x " + 
                             MessageUtils.stripColor(shopItem.getDisplayName()) + 
                             " §a(Value: §6$" + String.format("%.2f", itemValue) + "§a)");
            playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        } else {
            // Return unsellable item to player
            player.getInventory().addItem(item);
            player.sendMessage("§c❌ " + item.getType().name().replace("_", " ").toLowerCase() + 
                             " is not sellable! Returned to inventory.");
            playSound(Sound.ENTITY_VILLAGER_NO);
        }
        
        recalculateValue();
        
        // Refresh GUI after a short delay
        Bukkit.getScheduler().runTaskLater(plugin, this::open, 2L);
    }
    
    /**
     * Quick fill with sellable items from inventory
     */
    public void quickFill() {
        Map<Material, Integer> inventoryItems = new HashMap<>();
        
        // Scan player inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                ShopItem shopItem = findSellableItem(item.getType());
                if (shopItem != null && shopItem.getSellPrice() > 0) {
                    inventoryItems.put(item.getType(), 
                        inventoryItems.getOrDefault(item.getType(), 0) + item.getAmount());
                }
            }
        }
        
        if (inventoryItems.isEmpty()) {
            player.sendMessage("§c📦 No sellable items found in your inventory!");
            playSound(Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        // Clear current sell slots
        sellSlots.clear();
        
        // Fill sell slots with sellable items
        int slotIndex = 0;
        int itemsAdded = 0;
        
        for (Map.Entry<Material, Integer> entry : inventoryItems.entrySet()) {
            if (slotIndex >= SELL_SLOTS.length) break;
            
            Material material = entry.getKey();
            int amount = entry.getValue();
            ShopItem shopItem = findSellableItem(material);
            
            if (shopItem != null) {
                // Remove items from inventory
                removeItemsFromInventory(material, amount);
                
                // Add to sell slots
                sellSlots.put(SELL_SLOTS[slotIndex], new SellableItem(shopItem, amount));
                slotIndex++;
                itemsAdded += amount;
            }
        }
        
        player.sendMessage("§a📦 Quick filled with " + itemsAdded + " sellable items!");
        playSound(Sound.ENTITY_PLAYER_LEVELUP);
        
        recalculateValue();
        open(); // Refresh GUI
    }
    
    /**
     * Find sellable item in shop
     */
    private ShopItem findSellableItem(Material material) {
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                if (item.getMaterial() == material) {
                    return item;
                }
            }
        }
        return null;
    }
    
    /**
     * Recalculate total value and item count
     */
    private void recalculateValue() {
        totalValue = 0.0;
        totalItems = 0;
        
        for (SellableItem sellableItem : sellSlots.values()) {
            totalValue += sellableItem.shopItem.getSellPrice() * sellableItem.amount;
            totalItems += sellableItem.amount;
        }
        
        Logger.debug("Recalculated - Total Value: $" + String.format("%.2f", totalValue) + 
                    ", Total Items: " + totalItems);
    }
    
    /**
     * Sell all items
     */
    public void sellAll() {
        if (sellSlots.isEmpty()) {
            player.sendMessage("§c❌ No items to sell!");
            playSound(Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double totalEarned = 0.0;
        int itemsSold = 0;
        
        for (SellableItem sellableItem : sellSlots.values()) {
            double earned = sellableItem.shopItem.getSellPrice() * sellableItem.amount;
            plugin.getEconomyManager().getEconomy().depositPlayer(player, earned);
            totalEarned += earned;
            itemsSold += sellableItem.amount;
        }
        
        sellSlots.clear();
        totalValue = 0.0;
        totalItems = 0;
        
        player.sendMessage("§a💎 Successfully sold all items!");
        player.sendMessage("§a💰 Earned: §6$" + String.format("%.2f", totalEarned));
        player.sendMessage("§a📦 Items sold: §e" + itemsSold);
        
        playSound(Sound.ENTITY_PLAYER_LEVELUP);
        
        // Close GUI and return to shop
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.closeInventory();
            plugin.getGuiManager().openShop(player, "main");
        }, 20L);
    }
    
    /**
     * Sell only valuable items (worth $5+ each)
     */
    public void sellValuableOnly() {
        if (sellSlots.isEmpty()) {
            player.sendMessage("§c❌ No items to sell!");
            playSound(Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        double totalEarned = 0.0;
        int itemsSold = 0;
        List<Integer> slotsToRemove = new ArrayList<>();
        
        for (Map.Entry<Integer, SellableItem> entry : sellSlots.entrySet()) {
            SellableItem sellableItem = entry.getValue();
            
            if (sellableItem.shopItem.getSellPrice() >= 5.0) {
                double earned = sellableItem.shopItem.getSellPrice() * sellableItem.amount;
                plugin.getEconomyManager().getEconomy().depositPlayer(player, earned);
                totalEarned += earned;
                itemsSold += sellableItem.amount;
                slotsToRemove.add(entry.getKey());
            }
        }
        
        // Remove sold items from sell slots
        for (int slot : slotsToRemove) {
            sellSlots.remove(slot);
        }
        
        if (itemsSold > 0) {
            player.sendMessage("§6⭐ Sold valuable items only!");
            player.sendMessage("§a💰 Earned: §6$" + String.format("%.2f", totalEarned));
            player.sendMessage("§a📦 Items sold: §e" + itemsSold);
            playSound(Sound.ENTITY_PLAYER_LEVELUP);
        } else {
            player.sendMessage("§c❌ No valuable items (worth $5+) found!");
            playSound(Sound.ENTITY_VILLAGER_NO);
        }
        
        recalculateValue();
        open(); // Refresh GUI
    }
    
    /**
     * Clear all items and return to inventory
     */
    public void clearAll() {
        if (sellSlots.isEmpty()) {
            player.sendMessage("§c❌ No items to clear!");
            playSound(Sound.ENTITY_VILLAGER_NO);
            return;
        }
        
        int itemsReturned = 0;
        
        for (SellableItem sellableItem : sellSlots.values()) {
            ItemStack returnItem = new ItemStack(sellableItem.shopItem.getMaterial(), sellableItem.amount);
            player.getInventory().addItem(returnItem);
            itemsReturned += sellableItem.amount;
        }
        
        sellSlots.clear();
        totalValue = 0.0;
        totalItems = 0;
        
        player.sendMessage("§e📦 Cleared all items! Returned " + itemsReturned + " items to inventory.");
        playSound(Sound.UI_BUTTON_CLICK);
        
        open(); // Refresh GUI
    }
    
    /**
     * Remove items from player inventory
     */
    private void removeItemsFromInventory(Material material, int amount) {
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
    private void playSound(Sound sound) {
        try {
            player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
        } catch (Exception e) {
            Logger.debug("Could not play sound: " + sound);
        }
    }
    
    /**
     * Get sell slots for external access
     */
    public int[] getSellSlots() {
        return SELL_SLOTS;
    }
    
    /**
     * Get current sell items
     */
    public Map<Integer, SellableItem> getCurrentSellItems() {
        return new HashMap<>(sellSlots);
    }
    
    /**
     * Helper class for sellable items
     */
    public static class SellableItem {
        public final ShopItem shopItem;
        public int amount;
        
        public SellableItem(ShopItem shopItem, int amount) {
            this.shopItem = shopItem;
            this.amount = amount;
        }
        
        public double getTotalValue() {
            return shopItem.getSellPrice() * amount;
        }
    }
}