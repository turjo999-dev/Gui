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
import java.util.List;

/**
 * Section-specific GUI for browsing items
 */
public class SectionGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private final ShopSection section;
    private int currentPage = 0;
    
    public SectionGui(EasyShopGUI plugin, Player player, ShopSection section) {
        this.plugin = plugin;
        this.player = player;
        this.section = section;
    }
    
    /**
     * Set current page
     */
    public void setCurrentPage(int page) {
        this.currentPage = page;
    }
    
    /**
     * Open the section GUI
     */
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&6&l✦ " + section.getDisplayName() + " &6&l✦"));
        
        // Fill background
        fillBackground(gui);
        
        // Add section items
        addSectionItems(gui);
        
        // Add navigation
        addNavigation(gui);
        
        // Add player info
        addPlayerInfo(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background
     */
    private void fillBackground(Inventory gui) {
        // Clear inventory first
        gui.clear();
        
        Logger.debug("Filling background for section: " + section.getId());
        
        ItemStack background = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
    }
    
    /**
     * Add section items
     */
    private void addSectionItems(Inventory gui) {
        List<ShopItem> items = section.getItems();
        Logger.debug("Adding " + items.size() + " items to section GUI for: " + section.getId());
        
        int startIndex = currentPage * 28; // 28 items per page (7x4 grid)
        
        int[] itemSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        for (int i = 0; i < itemSlots.length && (startIndex + i) < items.size(); i++) {
            ShopItem item = items.get(startIndex + i);
            Logger.debug("Adding item to slot " + itemSlots[i] + ": " + item.getDisplayName());
            
            gui.setItem(itemSlots[i], createShopItemStack(item));
        }
    }
    
    /**
     * Create item stack for shop item
     */
    private ItemStack createShopItemStack(ShopItem item) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        boolean canAfford = balance >= item.getBuyPrice();
        
        Logger.debug("Creating item stack for: " + item.getDisplayName() + " (Material: " + item.getMaterial() + ")");
        
        return new ItemBuilder(item.getMaterial())
                .setName(item.getDisplayName())
                .setLore(Arrays.asList(
                        "&7▸ &fDescription:",
                        "&7  " + item.getDescription(),
                        "",
                        "&7▸ &fPricing:",
                        "&a  Buy: &f$" + String.format("%.2f", item.getBuyPrice()) + (canAfford ? " &a✓" : " &c✗"),
                        "&c  Sell: &f$" + String.format("%.2f", item.getSellPrice()),
                        "",
                        "&7▸ &fStock: " + (item.getStock() == -1 ? "&aUnlimited" : "&e" + item.getStock()),
                        "&7▸ &fDemand: " + getDemandColor(item.getDemand()) + item.getDemand().toUpperCase(),
                        "",
                        "&e&l⚡ QUICK ACTIONS:",
                        "&a▸ Left Click: &fBuy 1",
                        "&a▸ Right Click: &fSell 1", 
                        "&a▸ Shift + Left: &fBuy 64",
                        "&a▸ Shift + Right: &fSell All",
                        "&a▸ Middle Click: &fView Details"
                ))
                .addGlow(canAfford)
                .build();
    }
    
    /**
     * Get demand color
     */
    private String getDemandColor(String demand) {
        switch (demand.toLowerCase()) {
            case "high": return "&c";
            case "medium": return "&e";
            case "low": return "&a";
            default: return "&7";
        }
    }
    
    /**
     * Add navigation items
     */
    private void addNavigation(Inventory gui) {
        // Back to main shop
        gui.setItem(0, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
        
        // Previous page
        if (currentPage > 0) {
            gui.setItem(45, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .setName("&e&l← &e&lPREVIOUS PAGE")
                    .setLore(Arrays.asList(
                            "&7▸ &fPage " + (currentPage) + " of " + getTotalPages(),
                            "",
                            "&a&l➤ &aClick to go back!"
                    ))
                    .build());
        }
        
        // Next page
        if (hasNextPage()) {
            gui.setItem(53, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .setName("&e&l→ &e&lNEXT PAGE")
                    .setLore(Arrays.asList(
                            "&7▸ &fPage " + (currentPage + 2) + " of " + getTotalPages(),
                            "",
                            "&a&l➤ &aClick to continue!"
                    ))
                    .build());
        }
        
        // Page info
        gui.setItem(49, new ItemBuilder(Material.BOOK)
                .setName("&6&l📖 &e&lPAGE INFO")
                .setLore(Arrays.asList(
                        "&7▸ &fCurrent Page: &a" + (currentPage + 1),
                        "&7▸ &fTotal Pages: &a" + getTotalPages(),
                        "&7▸ &fTotal Items: &a" + section.getItems().size(),
                        "&7▸ &fItems on Page: &a" + getItemsOnCurrentPage()
                ))
                .build());
    }
    
    /**
     * Add player info
     */
    private void addPlayerInfo(Inventory gui) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        gui.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&6&l👤 &e&l" + player.getName().toUpperCase())
                .setLore(Arrays.asList(
                        "&7▸ &fBalance: &a$" + String.format("%.2f", balance),
                        "&7▸ &fSection: &e" + section.getName(),
                        "&7▸ &fPermissions: " + (player.hasPermission("easyshopgui.vip") ? "&aVIP" : "&7Default"),
                        "",
                        "&7▸ &fDiscount: " + getPlayerDiscount() + "%",
                        "&7▸ &fSell Multiplier: " + getSellMultiplier() + "x"
                ))
                .setSkullOwner(player.getName())
                .build());
        
        // Quick actions
        gui.setItem(8, new ItemBuilder(Material.EMERALD)
                .setName("&a&l💎 &e&lQUICK ACTIONS")
                .setLore(Arrays.asList(
                        "&7▸ &fAvailable actions:",
                        "&a  • &fBuy in bulk",
                        "&a  • &fSell inventory",
                        "&a  • &fPrice alerts",
                        "&a  • &fWishlist items",
                        "",
                        "&a&l➤ &aClick for options!"
                ))
                .addGlow()
                .build());
    }
    
    /**
     * Helper methods
     */
    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) section.getItems().size() / 28));
    }
    
    private boolean hasNextPage() {
        return (currentPage + 1) < getTotalPages();
    }
    
    private int getItemsOnCurrentPage() {
        int startIndex = currentPage * 28;
        int endIndex = Math.min(startIndex + 28, section.getItems().size());
        return Math.max(0, endIndex - startIndex);
    }
    
    private int getPlayerDiscount() {
        if (player.hasPermission("easyshopgui.discount.vip")) return 15;
        if (player.hasPermission("easyshopgui.discount.premium")) return 10;
        if (player.hasPermission("easyshopgui.discount.member")) return 5;
        return 0;
    }
    
    private double getSellMultiplier() {
        if (player.hasPermission("easyshopgui.multiplier.vip")) return 1.5;
        if (player.hasPermission("easyshopgui.multiplier.premium")) return 1.3;
        if (player.hasPermission("easyshopgui.multiplier.member")) return 1.1;
        return 1.0;
    }
    
    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
            open();
        }
    }
    
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            open();
        }
    }
}