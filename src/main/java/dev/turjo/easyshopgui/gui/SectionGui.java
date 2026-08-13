package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Section-specific GUI for browsing items.
 *
 * Prices shown here now run through PricingUtil, the same helper GuiListener's
 * buyItem()/sellItem() use to charge/pay out - previously this GUI displayed the raw
 * market price with no discount applied (only the separate player-info panel showed
 * the discount percentage as a decorative stat), so a VIP player would see one buy
 * price in the tooltip and be charged a lower, undiscounted-looking number they never
 * saw quoted anywhere. Now the tooltip is the actual checkout price.
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
    
    public void setCurrentPage(int page) {
        this.currentPage = page;
    }
    
    /**
     * Open the section GUI
     */
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&8§l»&6&l✦ " + section.getDisplayName() + " &6&l✦&8§l«"));
        
        fillBackground(gui);
        addSectionItems(gui);
        addNavigation(gui);
        addPlayerInfo(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background with a refined two-tone frame
     */
    private void fillBackground(Inventory gui) {
        gui.clear();
        
        Logger.debug("Filling background for section: " + section.getId());
        
        ItemStack background = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        ItemStack border = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }

        int[] borderSlots = {1, 2, 3, 5, 6, 7, 9, 17, 18, 26, 27, 35, 36, 44, 46, 47, 48, 50, 51, 52};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
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
     * Create item stack for shop item, with pricing that exactly matches what
     * GuiListener will actually charge/pay on click.
     */
    private ItemStack createShopItemStack(ShopItem item) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        
        double marketBuyPrice = plugin.getAiMarketplace().getCurrentBuyPrice(item.getId());
        double marketSellPrice = plugin.getAiMarketplace().getCurrentSellPrice(item.getId());
        int currentStock = plugin.getAiMarketplace().getCurrentStock(item.getId());
        
        if (marketBuyPrice <= 0) marketBuyPrice = item.getBuyPrice();
        if (marketSellPrice <= 0) marketSellPrice = item.getSellPrice();
        if (currentStock == 0) currentStock = item.getStock();

        // These are the real, final prices - same call GuiListener.buyItem()/sellItem()
        // makes before charging/paying out.
        double finalBuyPrice = PricingUtil.applyBuyDiscount(player, marketBuyPrice);
        double finalSellPrice = PricingUtil.applySellMultiplier(player, marketSellPrice);
        int discount = PricingUtil.getDiscountPercent(player);
        double multiplier = PricingUtil.getSellMultiplier(player);
        
        boolean canAfford = balance >= finalBuyPrice;
        boolean inStock = currentStock == -1 || currentStock > 0;
        
        Logger.debug("Creating item stack for: " + item.getDisplayName() + " (Material: " + item.getMaterial() + ")");

        List<String> lore = new ArrayList<>(Arrays.asList(
                "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                "&7" + item.getDescription(),
                "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                "",
                "&6&l💰 PRICING"
        ));

        if (discount > 0) {
            lore.add("&a  Buy: &f$" + String.format("%.2f", finalBuyPrice) +
                    " &7(&8was $" + String.format("%.2f", marketBuyPrice) + "&7, &a-" + discount + "%&7)" +
                    (canAfford && inStock ? " &a✓" : " &c✗"));
        } else {
            lore.add("&a  Buy: &f$" + String.format("%.2f", finalBuyPrice) + (canAfford && inStock ? " &a✓" : " &c✗"));
        }

        if (multiplier > 1.0) {
            lore.add("&c  Sell: &f$" + String.format("%.2f", finalSellPrice) + " &7(&a" + multiplier + "x&7 bonus)");
        } else {
            lore.add("&c  Sell: &f$" + String.format("%.2f", finalSellPrice));
        }

        lore.addAll(Arrays.asList(
                "",
                "&7▸ &fStock: " + (currentStock == -1 ? "&aUnlimited" : (inStock ? "&e" + currentStock : "&c0 - OUT OF STOCK")),
                "&7▸ &fDemand: " + getDemandColor(item.getDemand()) + item.getDemand().toUpperCase(),
                "",
                "&e&l⚡ QUICK ACTIONS:",
                "&a▸ Left Click: &fBuy 1",
                "&a▸ Right Click: &fSell 1",
                "&a▸ Shift + Left: &fBuy 64",
                "&a▸ Shift + Right: &fSell All",
                "&a▸ Middle Click: &fView Details"
        ));
        
        return new ItemBuilder(item.getMaterial())
                .setName(item.getDisplayName())
                .setLore(lore)
                .addGlow(canAfford && inStock)
                .build();
    }
    
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
        gui.setItem(0, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
        
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
                        "&7▸ &fRank: " + (player.hasPermission("easyshopgui.vip") ? "&6&lVIP" : "&7Default"),
                        "",
                        "&7▸ &fDiscount: &a" + PricingUtil.getDiscountPercent(player) + "%",
                        "&7▸ &fSell Multiplier: &a" + PricingUtil.getSellMultiplier(player) + "x"
                ))
                .setSkullOwner(player)
                .build());
        
        gui.setItem(8, new ItemBuilder(Material.EMERALD)
                .setName("&a&l💎 &e&lNAVIGATION TIPS")
                .setLore(Arrays.asList(
                        "&7▸ &fLeft-click an item to buy 1",
                        "&7▸ &fShift-left-click to buy 64",
                        "&7▸ &fRight-click to sell 1",
                        "&7▸ &fShift-right-click to sell all",
                        "&7▸ &fMiddle-click for full item details"
                ))
                .addGlow()
                .build());
    }
    
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
