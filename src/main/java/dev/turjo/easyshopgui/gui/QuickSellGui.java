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
    private Map<Material, SellableItem> sellableItems = new HashMap<>();
    private double totalValue = 0.0;
    
    public QuickSellGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        analyzeSellableItems();
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&c&l💸 &e&lQUICK SELL"));
        
        // Fill background
        fillBackground(gui);
        
        // Add sellable items
        addSellableItems(gui);
        
        // Add sell actions
        addSellActions(gui);
        
        // Add navigation
        addNavigation(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
    }
    
    /**
     * Analyze player inventory for sellable items
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
     * Add sellable items to GUI
     */
    private void addSellableItems(Inventory gui) {
        // Total value display
        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l💰 &e&lTOTAL INVENTORY VALUE")
                .setLore(Arrays.asList(
                        "&7▸ &fSellable Items: &a" + sellableItems.size() + " types",
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", totalValue),
                        "&7▸ &fCurrent Balance: &e$" + String.format("%.2f", plugin.getEconomyManager().getEconomy().getBalance(player)),
                        "&7▸ &fAfter Selling: &a$" + String.format("%.2f", plugin.getEconomyManager().getEconomy().getBalance(player) + totalValue),
                        "",
                        "&a&l💡 &aClick items below to sell!"
                ))
                .addGlow()
                .build());
        
        // Display top sellable items
        List<SellableItem> sortedItems = sellableItems.values().stream()
                .sorted((a, b) -> Double.compare(b.getTotalValue(), a.getTotalValue()))
                .limit(21)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        int[] itemSlots = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        for (int i = 0; i < Math.min(sortedItems.size(), itemSlots.length); i++) {
            SellableItem sellable = sortedItems.get(i);
            gui.setItem(itemSlots[i], createSellableItemStack(sellable));
        }
        
        // No sellable items message
        if (sellableItems.isEmpty()) {
            gui.setItem(22, new ItemBuilder(Material.BARRIER)
                    .setName("&c&l❌ &e&lNO SELLABLE ITEMS")
                    .setLore(Arrays.asList(
                            "&7▸ &fYour inventory is empty",
                            "&7▸ &fOr contains no sellable items",
                            "",
                            "&b&l💡 &bTip:",
                            "&7  Get items from the shop first!"
                    ))
                    .build());
        }
    }
    
    /**
     * Create sellable item stack
     */
    private ItemStack createSellableItemStack(SellableItem sellable) {
        ShopItem item = sellable.shopItem;
        double itemValue = sellable.getTotalValue();
        double percentage = (itemValue / totalValue) * 100;
        
        return new ItemBuilder(item.getMaterial())
                .setName("&6&l💸 " + item.getDisplayName())
                .setLore(Arrays.asList(
                        "&7▸ &fYou Have: &e" + sellable.count + "x",
                        "&7▸ &fPrice Each: &a$" + String.format("%.2f", item.getSellPrice()),
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", itemValue),
                        "&7▸ &fOf Total: &e" + String.format("%.1f", percentage) + "%",
                        "",
                        "&e&l⚡ SELL OPTIONS:",
                        "&a▸ Left Click: &fSell 1",
                        "&a▸ Right Click: &fSell 10",
                        "&a▸ Shift + Left: &fSell Half",
                        "&a▸ Shift + Right: &fSell All"
                ))
                .setAmount(Math.min(sellable.count, 64))
                .addGlow()
                .build();
    }
    
    /**
     * Add sell action buttons
     */
    private void addSellActions(Inventory gui) {
        // Sell all button
        gui.setItem(49, new ItemBuilder(totalValue > 0 ? Material.DIAMOND : Material.COAL)
                .setName(totalValue > 0 ? "&a&l💎 &e&lSELL EVERYTHING" : "&c&l❌ &e&lNOTHING TO SELL")
                .setLore(Arrays.asList(
                        "&7▸ &fSell all sellable items",
                        "&7▸ &fTotal Value: &a$" + String.format("%.2f", totalValue),
                        "&7▸ &fItems: &e" + sellableItems.size() + " types",
                        "",
                        totalValue > 0 ? "&a&l➤ &aClick to sell everything!" : "&c&l➤ &cNo items to sell!"
                ))
                .addGlow(totalValue > 0)
                .build());
        
        // Sell valuable items only
        double valuableThreshold = 10.0;
        double valuableTotal = sellableItems.values().stream()
                .filter(item -> item.shopItem.getSellPrice() >= valuableThreshold)
                .mapToDouble(SellableItem::getTotalValue)
                .sum();
        
        gui.setItem(47, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&6&l⭐ &e&lSELL VALUABLE ONLY")
                .setLore(Arrays.asList(
                        "&7▸ &fSell items worth $" + String.format("%.2f", valuableThreshold) + "+ each",
                        "&7▸ &fTotal Value: &6$" + String.format("%.2f", valuableTotal),
                        "&7▸ &fKeep cheap items",
                        "",
                        "&6&l➤ &6Click to sell valuable!"
                ))
                .build());
        
        // Refresh inventory
        gui.setItem(51, new ItemBuilder(Material.HOPPER)
                .setName("&b&l🔄 &e&lREFRESH INVENTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fScan inventory again",
                        "&7▸ &fUpdate sellable items",
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
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
    }
    
    /**
     * Sell specific item
     */
    public void sellItem(Material material, int amount) {
        SellableItem sellable = sellableItems.get(material);
        if (sellable == null) {
            player.sendMessage("§c❌ You don't have any " + material.name() + " to sell!");
            return;
        }
        
        int actualAmount = Math.min(amount, sellable.count);
        if (actualAmount <= 0) {
            player.sendMessage("§c❌ You don't have enough items to sell!");
            return;
        }
        
        // Remove items from inventory
        removeItemsFromInventory(material, actualAmount);
        
        // Give money
        double totalPrice = sellable.shopItem.getSellPrice() * actualAmount;
        plugin.getEconomyManager().getEconomy().depositPlayer(player, totalPrice);
        
        // Success message
        player.sendMessage("§a💸 Sold " + actualAmount + "x " + sellable.shopItem.getDisplayName() + 
                          " §afor §6$" + String.format("%.2f", totalPrice) + "!");
        
        // Refresh and reopen
        analyzeSellableItems();
        open();
    }
    
    /**
     * Sell all items
     */
    public void sellAll() {
        if (sellableItems.isEmpty()) {
            player.sendMessage("§c❌ No sellable items found!");
            return;
        }
        
        double totalEarned = 0.0;
        int itemsSold = 0;
        
        for (SellableItem sellable : sellableItems.values()) {
            removeItemsFromInventory(sellable.shopItem.getMaterial(), sellable.count);
            double earned = sellable.getTotalValue();
            plugin.getEconomyManager().getEconomy().depositPlayer(player, earned);
            totalEarned += earned;
            itemsSold += sellable.count;
        }
        
        player.sendMessage("§a💎 Sold everything! Earned §6$" + String.format("%.2f", totalEarned) + 
                          " §afrom §e" + itemsSold + " §aitems!");
        
        // Close GUI and return to shop
        player.closeInventory();
        plugin.getGuiManager().openShop(player, "main");
    }
    
    /**
     * Remove items from inventory
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