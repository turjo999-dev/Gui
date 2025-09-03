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

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/**
 * Main shop GUI with all sections
 */
public class ShopGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private final String shopName;
    
    public ShopGui(EasyShopGUI plugin, Player player, String shopName) {
        this.plugin = plugin;
        this.player = player;
        this.shopName = shopName;
    }
    
    /**
     * Open the main shop GUI
     */
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, MessageUtils.colorize("&6&l✦ &e&lEASY SHOP GUI &6&l✦"));
        
        // Fill background
        fillBackground(gui);
        
        // Add shop sections
        addShopSections(gui);
        
        // Add navigation and info items
        addNavigationItems(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background with decorative items
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        ItemStack border = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots with background
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        
        // Add border
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }
    }
    
    /**
     * Add all shop sections
     */
    private void addShopSections(Inventory gui) {
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        Logger.debug("Adding " + sections.size() + " sections to main shop GUI");
        
        // Stunning diamond pattern layout
        addSectionItem(gui, sections.get("blocks"), 20);      // Top left
        addSectionItem(gui, sections.get("ores"), 22);        // Top center  
        addSectionItem(gui, sections.get("food"), 24);        // Top right
        addSectionItem(gui, sections.get("redstone"), 29);    // Middle left
        addSectionItem(gui, sections.get("farming"), 31);     // Center
        addSectionItem(gui, sections.get("decoration"), 33);  // Middle right
        addSectionItem(gui, sections.get("potions"), 40);     // Bottom center
    }
    
    /**
     * Add individual section item
     */
    private void addSectionItem(Inventory gui, ShopSection section, int slot) {
        if (section == null || !section.isEnabled()) return;
        
        Logger.debug("Adding section to slot " + slot + ": " + section.getId() + 
                    " (" + section.getDisplayName() + ") with " + section.getItems().size() + " items");
        
        gui.setItem(slot, new ItemBuilder(section.getIcon())
                .setName(MessageUtils.colorize(section.getDisplayName()))
                .setLore(Arrays.asList(
                        "&7▸ &f" + section.getDescription(),
                        "&7▸ &fItems: &a" + section.getItems().size(),
                        "&7▸ &fCategory: &e" + section.getName(),
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
    }
    
    /**
     * Get slot for section from configuration
     */
    private int getSlotForSection(String sectionId) {
        // Default slot mapping
        Map<String, Integer> defaultSlots = new HashMap<>();
        defaultSlots.put("blocks", 10);
        defaultSlots.put("ores", 11);
        defaultSlots.put("food", 12);
        defaultSlots.put("redstone", 13);
        defaultSlots.put("farming", 14);
        defaultSlots.put("decoration", 15);
        defaultSlots.put("potions", 16);
        
        return defaultSlots.getOrDefault(sectionId, 10);
    }
    
    /**
     * Add navigation and info items
     */
    private void addNavigationItems(Inventory gui) {
        // Player info (top center)
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        gui.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&6&l👤 &e&l" + player.getName().toUpperCase())
                .setLore(Arrays.asList(
                        "&7▸ &fBalance: &a$" + String.format("%.2f", balance),
                        "&7▸ &fRank: " + (player.hasPermission("easyshopgui.vip") ? "&6VIP" : "&7Member"),
                        "&7▸ &fDiscount: &a" + getPlayerDiscount(player) + "%",
                        "&7▸ &fSell Bonus: &a" + getSellMultiplier(player) + "x",
                        "",
                        "&6&l⭐ &eWelcome to EasyShop!"
                ))
                .setSkullOwner(player.getName())
                .addGlow()
                .build());
        
        // Search function (bottom left)
        gui.setItem(37, new ItemBuilder(Material.COMPASS)
                .setName("&b&l🔍 &e&lSEARCH ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fQuickly find items",
                        "&7▸ &fPartial words work",
                        "&7▸ &fTypo-friendly matching",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
        
        // Transaction history (bottom left-center)
        gui.setItem(38, new ItemBuilder(Material.BOOK)
                .setName("&3&l📋 &e&lTRANSACTION HISTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fView purchase history",
                        "&7▸ &fTrack your spending",
                        "&7▸ &fAnalyze patterns",
                        "",
                        "&a&l➤ &aClick to view!"
                ))
                .build());
        
        // Shop settings (bottom center-left)
        gui.setItem(39, new ItemBuilder(Material.COMPARATOR)
                .setName("&7&l⚙ &e&lSHOP SETTINGS")
                .setLore(Arrays.asList(
                        "&7▸ &fConfirm purchases: &aON",
                        "&7▸ &fSound effects: &aON",
                        "&7▸ &fNotifications: &aON",
                        "&7▸ &fPrice alerts: &cOFF",
                        "",
                        "&a&l➤ &aClick to configure!"
                ))
                .build());
        
        // Quick sell (bottom center-right)
        gui.setItem(41, new ItemBuilder(Material.HOPPER)
                .setName("&c&l💸 &e&lQUICK SELL")
                .setLore(Arrays.asList(
                        "&7▸ &fAnalyze inventory",
                        "&7▸ &fBulk selling options",
                        "&7▸ &fInstant transactions",
                        "",
                        "&a&l➤ &aClick to sell!"
                ))
                .build());
        
        // Shop info (bottom right-center)
        gui.setItem(42, new ItemBuilder(Material.PAPER)
                .setName("&e&l📄 &e&lSHOP INFORMATION")
                .setLore(Arrays.asList(
                        "&7▸ &fShop Name: &a" + shopName,
                        "&7▸ &fTotal Items: &a" + getTotalItemCount(),
                        "&7▸ &fCategories: &a" + sections.size(),
                        "&7▸ &fLast Updated: &aToday",
                        "",
                        "&7▸ &fDynamic Pricing: &aENABLED",
                        "&7▸ &fStock System: &aENABLED"
                ))
                .build());
        
        // Close button (bottom right)
        gui.setItem(43, new ItemBuilder(Material.BARRIER)
                .setName("&c&l✖ &e&lCLOSE SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fClose this menu",
                        "",
                        "&c&l➤ &cClick to close!"
                ))
                .build());
    }
    
    /**
     * Helper methods
     */
    private int getTotalItemCount() {
        return plugin.getGuiManager().getSections().values().stream()
                .mapToInt(section -> section.getItems().size())
                .sum();
    }
    
    private int getPlayerDiscount(Player player) {
        if (player.hasPermission("easyshopgui.discount.vip")) return 15;
        if (player.hasPermission("easyshopgui.discount.premium")) return 10;
        if (player.hasPermission("easyshopgui.discount.member")) return 5;
        return 0;
    }
    
    private double getSellMultiplier(Player player) {
        if (player.hasPermission("easyshopgui.multiplier.vip")) return 1.5;
        if (player.hasPermission("easyshopgui.multiplier.premium")) return 1.3;
        if (player.hasPermission("easyshopgui.multiplier.member")) return 1.1;
        return 1.0;
    }
}