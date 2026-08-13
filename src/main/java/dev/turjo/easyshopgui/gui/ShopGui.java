package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import dev.turjo.easyshopgui.utils.PricingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * Main shop GUI with all sections.
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
        Inventory gui = Bukkit.createInventory(null, 54,
                MessageUtils.colorize("&8§l»&6&l✦ &e&lEASY SHOP GUI &6&l✦&8§l«"));
        
        fillBackground(gui);
        addShopSections(gui);
        addNavigationItems(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background with a refined two-tone frame plus corner accents
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        ItemStack border = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        ItemStack accent = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }

        int[] accentSlots = {0, 8, 45, 53};
        for (int slot : accentSlots) {
            gui.setItem(slot, accent);
        }
    }
    
    /**
     * Add all shop sections in a diamond pattern layout
     */
    private void addShopSections(Inventory gui) {
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        Logger.debug("Adding " + sections.size() + " sections to main shop GUI");
        
        addSectionItem(gui, sections.get("blocks"), 20);
        addSectionItem(gui, sections.get("ores"), 22);
        addSectionItem(gui, sections.get("food"), 24);
        addSectionItem(gui, sections.get("redstone"), 29);
        addSectionItem(gui, sections.get("farming"), 31);
        addSectionItem(gui, sections.get("decoration"), 33);
        addSectionItem(gui, sections.get("potions"), 40);
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
                        "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                        "&7" + section.getDescription(),
                        "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                        "",
                        "&7▸ &fItems Available: &a" + section.getItems().size(),
                        "&7▸ &fCategory: &e" + section.getName(),
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
    }
    
    /**
     * Add navigation and info items
     */
    private void addNavigationItems(Inventory gui) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        gui.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&6&l⭐ &e&l" + player.getName().toUpperCase() + " &6&l⭐")
                .setLore(Arrays.asList(
                        "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                        "&7▸ &fBalance: &a$" + String.format("%.2f", balance),
                        "&7▸ &fRank: " + (player.hasPermission("easyshopgui.vip") ? "&6&lVIP" : "&7Member"),
                        "&7▸ &fDiscount: &a" + PricingUtil.getDiscountPercent(player) + "%",
                        "&7▸ &fSell Bonus: &a" + PricingUtil.getSellMultiplier(player) + "x",
                        "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                        "",
                        "&6&l⭐ &eWelcome to EasyShop!"
                ))
                .setSkullOwner(player)
                .addGlow()
                .build());
        
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
        
        gui.setItem(38, new ItemBuilder(Material.BOOK)
                .setName("&3&l📋 &e&lTRANSACTION HISTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fView purchase history",
                        "&7▸ &fTrack your spending",
                        "&7▸ &fTransactions logged: &e" + plugin.getTransactionManager().getTransactionCount(player),
                        "",
                        "&a&l➤ &aClick to view!"
                ))
                .build());
        
        gui.setItem(39, new ItemBuilder(Material.COMPARATOR)
                .setName("&7&l⚙ &e&lSHOP SETTINGS")
                .setLore(Arrays.asList(
                        "&7▸ &fSound effects: " + (plugin.getPlayerPreferencesManager().isSoundEffectsEnabled(player) ? "&aON" : "&cOFF"),
                        "&7▸ &fPurchase confirm: " + (plugin.getPlayerPreferencesManager().isPurchaseConfirmEnabled(player) ? "&aON" : "&cOFF"),
                        "&7▸ &fTransaction messages: " + (plugin.getPlayerPreferencesManager().isTransactionMessagesEnabled(player) ? "&aON" : "&cOFF"),
                        "",
                        "&a&l➤ &aClick to configure!"
                ))
                .build());
        
        gui.setItem(41, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&c&l💸 &e&lQUICK SELL")
                .setLore(Arrays.asList(
                        "&7▸ &fSell items from inventory",
                        "&7▸ &fBulk selling options",
                        "&7▸ &fInstant transactions",
                        "",
                        "&a&l➤ &aClick to sell!"
                ))
                .build());
        
        gui.setItem(42, new ItemBuilder(Material.NETHER_STAR)
                .setName("&d&l🤖 &e&lAI MARKETPLACE")
                .setLore(Arrays.asList(
                        "&7▸ &fLive trend & sentiment data",
                        "&7▸ &fReal trending items ranked",
                        "&7▸ &fby actual trading volume",
                        "",
                        "&a&l➤ &aClick to explore!"
                ))
                .addGlow()
                .build());
        
        gui.setItem(43, new ItemBuilder(Material.BARRIER)
                .setName("&c&l✖ &e&lCLOSE SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fClose this menu",
                        "",
                        "&c&l➤ &cClick to close!"
                ))
                .build());
    }
    
    private int getTotalItemCount() {
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        return sections.values().stream()
                .mapToInt(section -> section.getItems().size())
                .sum();
    }
}
