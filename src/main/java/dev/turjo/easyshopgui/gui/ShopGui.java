package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
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
        // Load sections from configuration
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        
        // Manually add sections in specific order (excluding potions)
        addSectionItem(gui, sections.get("blocks"), 10);
        addSectionItem(gui, sections.get("ores"), 11);
        addSectionItem(gui, sections.get("food"), 12);
        addSectionItem(gui, sections.get("redstone"), 13);
        addSectionItem(gui, sections.get("farming"), 14);
        addSectionItem(gui, sections.get("decoration"), 15);
    }
    
    /**
     * Add individual section item
     */
    private void addSectionItem(Inventory gui, ShopSection section, int slot) {
        if (section == null || !section.isEnabled()) return;
        
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
        // Player balance
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        gui.setItem(4, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&6&l💰 &e&lYOUR BALANCE")
                .setLore(Arrays.asList(
                        "&7▸ &fCurrent Balance:",
                        "&a&l$" + String.format("%.2f", balance),
                        "",
                        "&7▸ &fEarn money by:",
                        "&7  • &aSelling items",
                        "&7  • &aCompleting jobs",
                        "&7  • &aVoting for server"
                ))
                .addGlow()
                .build());
        
        // Search function
        gui.setItem(37, new ItemBuilder(Material.COMPASS)
                .setName("&b&l🔍 &e&lSEARCH ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fQuickly find items",
                        "&7▸ &fSearch by name",
                        "&7▸ &fFilter by category",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
        
        // Transaction history
        gui.setItem(38, new ItemBuilder(Material.BOOK)
                .setName("&3&l📋 &e&lTRANSACTION HISTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fView purchase history",
                        "&7▸ &fTrack your spending",
                        "&7▸ &fExport data",
                        "",
                        "&a&l➤ &aClick to view!"
                ))
                .build());
        
        // Shop settings
        gui.setItem(39, new ItemBuilder(Material.COMPARATOR)
                .setName("&7&l⚙ &e&lSHOP SETTINGS")
                .setLore(Arrays.asList(
                        "&7▸ &fConfirm purchases: &aON",
                        "&7▸ &fSound effects: &aON",
                        "&7▸ &fNotifications: &aON",
                        "",
                        "&a&l➤ &aClick to configure!"
                ))
                .build());
        
        // Quick sell
        gui.setItem(40, new ItemBuilder(Material.HOPPER)
                .setName("&c&l💸 &e&lQUICK SELL")
                .setLore(Arrays.asList(
                        "&7▸ &fSell inventory items",
                        "&7▸ &fBulk selling",
                        "&7▸ &fInstant money",
                        "",
                        "&a&l➤ &aClick to sell!"
                ))
                .build());
        
        // Shop info
        gui.setItem(41, new ItemBuilder(Material.PAPER)
                .setName("&e&l📄 &e&lSHOP INFORMATION")
                .setLore(Arrays.asList(
                        "&7▸ &fShop Name: &a" + shopName,
                        "&7▸ &fTotal Items: &a2,847",
                        "&7▸ &fCategories: &a13",
                        "&7▸ &fLast Updated: &aToday",
                        "",
                        "&7▸ &fDynamic Pricing: &aENABLED",
                        "&7▸ &fStock System: &aENABLED"
                ))
                .build());
        
        // Close button
        gui.setItem(43, new ItemBuilder(Material.BARRIER)
                .setName("&c&l✖ &e&lCLOSE SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fClose this menu",
                        "",
                        "&c&l➤ &cClick to close!"
                ))
                .build());
    }
}