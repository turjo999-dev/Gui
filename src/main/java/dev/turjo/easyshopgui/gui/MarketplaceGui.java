package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.marketplace.AIMarketplace;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AI Marketplace overview GUI - shows genuinely computed trend/sentiment/volume data.
 *
 * Previously this class was never opened from anywhere (no command, no button - fully
 * unreachable dead code) and every stat it showed was a hardcoded literal ("BULLISH",
 * "RISING", a fixed "Diamonds: BUY / Iron: HOLD / Redstone: SELL" recommendation)
 * regardless of what the AI engine had actually computed. It's now wired to a button in
 * the main shop GUI (see ShopGui, slot 42) and reads real data from
 * AIMarketplace.getMarketData()/getTrendingItemIds().
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
        
        fillBackground(gui);
        addMarketOverview(gui);
        addTrendingItems(gui);
        addAIRecommendations(gui);
        addNavigation(gui);
        
        player.openInventory(gui);
    }

    private void fillBackground(Inventory gui) {
        var background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
        var border = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        int[] borderSlots = {0,1,2,3,5,6,7,8,9,17,18,26,27,35,36,44,46,47,48,49,50,51,52};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }
    }
    
    private void addMarketOverview(Inventory gui) {
        int totalItems = plugin.getGuiManager().getSections().values().stream()
                .mapToInt(section -> section.getItems().size())
                .sum();
        int trackedItems = plugin.getAiMarketplace().getTrendingItemIds(Integer.MAX_VALUE).size();

        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l📊 &e&lMARKET OVERVIEW")
                .setLore(Arrays.asList(
                        "&7▸ &fAI Engine: &a✓ ACTIVE",
                        "&7▸ &fDynamic Pricing: &a✓ ENABLED",
                        "&7▸ &fSmart Restocking: &a✓ RUNNING",
                        "&7▸ &fCatalog Size: &e" + totalItems + " items",
                        "&7▸ &fItems With Activity: &e" + trackedItems,
                        "",
                        "&6&l🤖 AI FEATURES:",
                        "&7▸ &fReal-time price adjustments",
                        "&7▸ &fSupply & demand analysis",
                        "&7▸ &fTrend prediction algorithms",
                        "&7▸ &fAutomatic stock management",
                        "",
                        "&e&l💡 HOW TRENDING WORKS:",
                        "&7▸ &fRanked by real trading volume,",
                        "&7▸ &fnot a fixed list - buy or sell",
                        "&7▸ &fitems to influence this yourself!"
                ))
                .addGlow()
                .build());
    }
    
    private void addTrendingItems(Inventory gui) {
        List<ShopItem> trendingItems = getTrendingItems();
        int[] slots = {19, 20, 21, 22, 23, 24, 25};
        
        if (trendingItems.isEmpty()) {
            gui.setItem(22, new ItemBuilder(Material.GRAY_DYE)
                    .setName("&7&lNo Trading Activity Yet")
                    .setLore(Arrays.asList(
                            "&7▸ &fNo items have been bought or sold",
                            "&7▸ &fsince the server started.",
                            "",
                            "&7▸ &fBe the first to trade and show up here!"
                    ))
                    .build());
            return;
        }
        
        for (int i = 0; i < Math.min(trendingItems.size(), slots.length); i++) {
            ShopItem item = trendingItems.get(i);
            double currentPrice = plugin.getAiMarketplace().getCurrentBuyPrice(item.getId());
            if (currentPrice <= 0) currentPrice = item.getBuyPrice();
            int currentStock = plugin.getAiMarketplace().getCurrentStock(item.getId());

            AIMarketplace.MarketData market = plugin.getAiMarketplace().getMarketData(item.getId());
            String trend = market != null ? market.getTrend() : "STABLE";
            String sentiment = market != null ? market.getSentiment() : "NEUTRAL";
            int volume = market != null ? (market.getBuyTransactions() + market.getSellTransactions()) : 0;

            gui.setItem(slots[i], new ItemBuilder(item.getMaterial())
                    .setName(trendIcon(trend) + " " + item.getDisplayName())
                    .setLore(Arrays.asList(
                            "&7▸ &fCurrent Price: &a$" + String.format("%.2f", currentPrice),
                            "&7▸ &fStock Level: " + (currentStock == -1 ? "&aUnlimited" : "&e" + currentStock),
                            "&7▸ &fTrend: " + trendColor(trend) + trendIcon(trend) + " " + trend,
                            "&7▸ &fSentiment: " + sentimentColor(sentiment) + sentiment,
                            "&7▸ &fTrading Volume: &e" + volume + " &7(this session)",
                            "",
                            "&e&l⚡ WHAT THIS MEANS:",
                            "&7▸ &f" + trendExplanation(trend)
                    ))
                    .addGlow()
                    .build());
        }
    }
    
    private void addAIRecommendations(Inventory gui) {
        List<String> lines = new ArrayList<>(Arrays.asList(
                "&7▸ &fRanked by real trading volume",
                "&7▸ &fand AI trend/sentiment analysis",
                "",
                "&d&l💎 TOP ACTIVITY THIS SESSION:"
        ));

        List<ShopItem> top = getTrendingItems();
        if (top.isEmpty()) {
            lines.add("&7▸ &7No trades recorded yet");
        } else {
            for (ShopItem item : top.subList(0, Math.min(3, top.size()))) {
                AIMarketplace.MarketData market = plugin.getAiMarketplace().getMarketData(item.getId());
                String trend = market != null ? market.getTrend() : "STABLE";
                String verdict = recommendationFor(trend);
                lines.add("&7▸ &f" + MessageUtils.stripColor(item.getDisplayName()) + ": " +
                        trendColor(trend) + verdict);
            }
        }
        lines.add("");
        lines.add("&7▸ &fThis reflects actual server activity,");
        lines.add("&7▸ &fnot a fixed daily pick.");

        gui.setItem(40, new ItemBuilder(Material.NETHER_STAR)
                .setName("&d&l🤖 &e&lAI RECOMMENDATIONS")
                .setLore(lines)
                .addGlow()
                .build());
    }
    
    private void addNavigation(Inventory gui) {
        gui.setItem(49, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
    }
    
    /**
     * Real trending items, ranked by actual trading volume via AIMarketplace, not
     * arbitrary map iteration order.
     */
    private List<ShopItem> getTrendingItems() {
        List<String> trendingIds = plugin.getAiMarketplace().getTrendingItemIds(7);
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();

        List<ShopItem> results = new ArrayList<>();
        for (String itemId : trendingIds) {
            Optional<ShopItem> found = sections.values().stream()
                    .flatMap(section -> section.getItems().stream())
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst();
            found.ifPresent(results::add);
        }
        return results;
    }

    private String trendIcon(String trend) {
        switch (trend) {
            case "RISING": return "📈";
            case "FALLING": return "📉";
            default: return "📊";
        }
    }

    private String trendColor(String trend) {
        switch (trend) {
            case "RISING": return "&c";
            case "FALLING": return "&a";
            default: return "&e";
        }
    }

    private String sentimentColor(String sentiment) {
        switch (sentiment) {
            case "BULLISH": return "&a";
            case "BEARISH": return "&c";
            default: return "&e";
        }
    }

    private String trendExplanation(String trend) {
        switch (trend) {
            case "RISING": return "Price has climbed recently - buying now costs more.";
            case "FALLING": return "Price has dropped recently - good time to buy.";
            default: return "Price has been steady - no major recent shift.";
        }
    }

    private String recommendationFor(String trend) {
        switch (trend) {
            case "RISING": return "📈 Consider selling (price is up)";
            case "FALLING": return "📉 Consider buying (price is down)";
            default: return "📊 Stable - no strong signal";
        }
    }
}
