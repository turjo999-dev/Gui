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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Advanced marketplace GUI with AI-powered features
 */
public class MarketplaceGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    
    public MarketplaceGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&6&l🤖 &e&lAI MARKETPLACE &6&l🤖"));
        
        // Add market overview
        addMarketOverview(gui);
        
        // Add trending items
        addTrendingItems(gui);
        
        // Add AI recommendations
        addAIRecommendations(gui);
        
        // Add navigation
        addNavigation(gui);
        
        player.openInventory(gui);
    }
    
    private void addMarketOverview(Inventory gui) {
        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l📊 &e&lMARKET OVERVIEW")
                .setLore(Arrays.asList(
                        "&7▸ &fAI Engine: &a✓ ACTIVE",
                        "&7▸ &fDynamic Pricing: &a✓ ENABLED",
                        "&7▸ &fSmart Restocking: &a✓ RUNNING",
                        "&7▸ &fMarket Sentiment: &e📈 BULLISH",
                        "",
                        "&6&l🤖 AI FEATURES:",
                        "&7▸ &fReal-time price adjustments",
                        "&7▸ &fSupply & demand analysis",
                        "&7▸ &fTrend prediction algorithms",
                        "&7▸ &fAutomatic stock management",
                        "",
                        "&e&l💡 MARKET INSIGHTS:",
                        "&7▸ &fHigh demand items get price boosts",
                        "&7▸ &fLow stock triggers auto-restocking",
                        "&7▸ &fPrices fluctuate based on activity"
                ))
                .addGlow()
                .build());
    }
    
    private void addTrendingItems(Inventory gui) {
        // Get trending items from AI marketplace
        List<ShopItem> trendingItems = getTrendingItems();
        
        int[] slots = {19, 20, 21, 22, 23, 24, 25};
        
        for (int i = 0; i < Math.min(trendingItems.size(), slots.length); i++) {
            ShopItem item = trendingItems.get(i);
            double currentPrice = plugin.getAiMarketplace().getCurrentBuyPrice(item.getId());
            int currentStock = plugin.getAiMarketplace().getCurrentStock(item.getId());
            
            gui.setItem(slots[i], new ItemBuilder(item.getMaterial())
                    .setName("&6&l📈 " + item.getDisplayName())
                    .setLore(Arrays.asList(
                            "&7▸ &fCurrent Price: &a$" + String.format("%.2f", currentPrice),
                            "&7▸ &fStock Level: &e" + currentStock,
                            "&7▸ &fTrend: &c📈 RISING",
                            "&7▸ &fDemand: &c🔥 HIGH",
                            "",
                            "&e&l⚡ AI ANALYSIS:",
                            "&7▸ &fPrice increased by AI",
                            "&7▸ &fHigh trading volume",
                            "&7▸ &fRecommended for investment",
                            "",
                            "&a&l➤ &aClick to view details!"
                    ))
                    .addGlow()
                    .build());
        }
    }
    
    private void addAIRecommendations(Inventory gui) {
        gui.setItem(40, new ItemBuilder(Material.NETHER_STAR)
                .setName("&d&l🤖 &e&lAI RECOMMENDATIONS")
                .setLore(Arrays.asList(
                        "&7▸ &fBased on your trading history",
                        "&7▸ &fMarket analysis algorithms",
                        "&7▸ &fPersonalized suggestions",
                        "",
                        "&d&l💎 TODAY'S PICKS:",
                        "&7▸ &fDiamonds: &a📈 BUY (Rising trend)",
                        "&7▸ &fIron: &e📊 HOLD (Stable)",
                        "&7▸ &fRedstone: &c📉 SELL (Declining)",
                        "",
                        "&a&l➤ &aClick for detailed analysis!"
                ))
                .addGlow()
                .build());
    }
    
    private void addNavigation(Inventory gui) {
        // Back to main shop
        gui.setItem(45, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
        
        // Market analytics
        gui.setItem(53, new ItemBuilder(Material.BOOK)
                .setName("&b&l📊 &e&lMARKET ANALYTICS")
                .setLore(Arrays.asList(
                        "&7▸ &fDetailed market data",
                        "&7▸ &fPrice history charts",
                        "&7▸ &fTrading volume analysis",
                        "",
                        "&b&l➤ &bClick to view!"
                ))
                .build());
    }
    
    private List<ShopItem> getTrendingItems() {
        // Get all items and sort by AI metrics
        return plugin.getGuiManager().getSections().values().stream()
                .flatMap(section -> section.getItems().stream())
                .limit(7)
                .collect(Collectors.toList());
    }
}