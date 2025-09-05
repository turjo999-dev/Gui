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
 * Interactive sell GUI where players can place items to sell
 */
public class SellGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private final Map<Integer, ItemStack> sellSlots = new HashMap<>();
    private double totalValue = 0.0;
    
    public SellGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&c&l💸 &e&lSELL ITEMS"));
        
        // Fill background and setup GUI
        fillBackground(gui);
        addSellSlots(gui);
        addActionButtons(gui);
        addNavigation(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
    }
    
    /**
     * Add sell slots where players can place items
     */
    private void addSellSlots(Inventory gui) {
        // Create sell slots (3x7 grid)
        int[] sellSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        };
        
        for (int slot : sellSlots) {
            gui.setItem(slot, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .setName("&7&l📦 &e&lSELL SLOT")
                    .setLore(Arrays.asList(
                            "&7▸ &fPlace items here to sell",
                            "&7▸ &fOnly sellable items accepted",
                            "&7▸ &fUnsellable items will be returned",
                            "",
                            "&a&l➤ &aDrop items here!"
                    ))
                    .build());
        }
        
        // Instructions
        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l💰 &e&lSELL INTERFACE")
                .setLore(Arrays.asList(
                        "&7▸ &fPlace items in the slots below",
                        "&7▸ &fSellable items will show their value",
                        "&7▸ &fUnsellable items will be returned",
                        "&7▸ &fClick 'Sell All' when ready",
                        "",
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", totalValue),
                        "",
                        "&a&l💡 &aTip: Drag and drop items!"
                ))
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
                        "&7▸ &fItems: &e" + sellSlots.size(),
                        "",
                        totalValue > 0 ? "&a&l➤ &aClick to sell all!" : "&c&l➤ &cPlace items first!"
                ))
                .addGlow(totalValue > 0)
                .build());
        
        // Clear all button
        gui.setItem(47, new ItemBuilder(Material.BARRIER)
                .setName("&c&l🗑 &e&lCLEAR ALL")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn all items to inventory",
                        "&7▸ &fClear all sell slots",
                        "",
                        "&c&l➤ &cClick to clear!"
                ))
                .build());
        
        // Refresh/Calculate button
        gui.setItem(51, new ItemBuilder(Material.HOPPER)
                .setName("&b&l🔄 &e&lRECALCULATE VALUE")
                .setLore(Arrays.asList(
                        "&7▸ &fRecalculate total value",
                        "&7▸ &fUpdate sellable status",
                        "",
                        "&b&l➤ &bClick to refresh!"
                ))
                .build());
    }
    
    /**
     * Add navigation
     */
    private void addNavigation(Inventory gui) {
        // Back button
        gui.setItem(45, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
    }
    
    /**
     * Handle item placement in sell slots
     */
    public void handleItemPlacement(int slot, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            sellSlots.remove(slot);
            recalculateValue();
            return;
        }
        
        // Check if item is sellable
        ShopItem shopItem = findSellableItem(item.getType());
        if (shopItem != null && shopItem.getSellPrice() > 0) {
            sellSlots.put(slot, item);
            player.sendMessage("§a✓ Added " + item.getAmount() + "x " + shopItem.getDisplayName() + 
                             " §a(Value: §6$" + String.format("%.2f", shopItem.getSellPrice() * item.getAmount()) + "§a)");
        } else {
            // Return unsellable item to player
            player.getInventory().addItem(item);
            player.sendMessage("§c❌ " + item.getType().name() + " is not sellable! Returned to inventory.");
        }
        
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
     * Recalculate total value
     */
    private void recalculateValue() {
        totalValue = 0.0;
        for (ItemStack item : sellSlots.values()) {
            ShopItem shopItem = findSellableItem(item.getType());
            if (shopItem != null) {
                totalValue += shopItem.getSellPrice() * item.getAmount();
            }
        }
    }
    
    /**
     * Sell all items
     */
    public void sellAll() {
        if (sellSlots.isEmpty()) {
            player.sendMessage("§c❌ No items to sell!");
            return;
        }
        
        double totalEarned = 0.0;
        int itemsSold = 0;
        
        for (ItemStack item : sellSlots.values()) {
            ShopItem shopItem = findSellableItem(item.getType());
            if (shopItem != null) {
                double earned = shopItem.getSellPrice() * item.getAmount();
                plugin.getEconomyManager().getEconomy().depositPlayer(player, earned);
                totalEarned += earned;
                itemsSold += item.getAmount();
            }
        }
        
        sellSlots.clear();
        totalValue = 0.0;
        
        player.sendMessage("§a💎 Sold all items! Earned §6$" + String.format("%.2f", totalEarned) + 
                          " §afrom §e" + itemsSold + " §aitems!");
        
        // Close GUI and return to shop
        player.closeInventory();
        plugin.getGuiManager().openShop(player, "main");
    }
    
    /**
     * Clear all items
     */
    public void clearAll() {
        for (ItemStack item : sellSlots.values()) {
            player.getInventory().addItem(item);
        }
        sellSlots.clear();
        totalValue = 0.0;
        player.sendMessage("§e📦 All items returned to inventory!");
        open(); // Refresh GUI
    }
}