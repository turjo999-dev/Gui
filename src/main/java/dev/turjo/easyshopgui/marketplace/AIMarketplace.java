package dev.turjo.easyshopgui.marketplace;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AI-Powered Marketplace with Dynamic Pricing and Smart Stock Management
 */
public class AIMarketplace {
    
    private final EasyShopGUI plugin;
    private final Map<String, MarketData> marketData = new ConcurrentHashMap<>();
    private final Map<String, PriceHistory> priceHistory = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    // AI Configuration
    private final double PRICE_VOLATILITY = 0.15; // 15% max price change
    private final double DEMAND_INFLUENCE = 0.8;
    private final double SUPPLY_INFLUENCE = 0.6;
    private final double TREND_INFLUENCE = 0.4;
    private final int HISTORY_SIZE = 100;
    
    public AIMarketplace(EasyShopGUI plugin) {
        this.plugin = plugin;
        initializeMarketData();
        startAIEngine();
    }
    
    /**
     * Initialize market data for all items
     */
    private void initializeMarketData() {
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                String itemId = item.getId();
                
                MarketData data = new MarketData(
                    item.getBuyPrice(),
                    item.getSellPrice(),
                    item.getStock() == -1 ? 1000 : item.getStock(),
                    calculateInitialDemand(item),
                    0, 0, // No transactions initially
                    System.currentTimeMillis()
                );
                
                marketData.put(itemId, data);
                priceHistory.put(itemId, new PriceHistory());
                
                Logger.debug("Initialized market data for: " + itemId);
            }
        }
        
        Logger.info("AI Marketplace initialized with " + marketData.size() + " items");
    }
    
    /**
     * Start the AI engine with multiple threads
     */
    private void startAIEngine() {
        // Price adjustment engine (every 5 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                adjustPricesWithAI();
            }
        }.runTaskTimerAsynchronously(plugin, 6000L, 6000L); // 5 minutes
        
        // Stock management engine (every 2 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                manageStockWithAI();
            }
        }.runTaskTimerAsynchronously(plugin, 2400L, 2400L); // 2 minutes
        
        // Market analysis engine (every 10 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                analyzeMarketTrends();
            }
        }.runTaskTimerAsynchronously(plugin, 12000L, 12000L); // 10 minutes
        
        Logger.info("AI Marketplace engines started successfully!");
    }
    
    /**
     * AI-powered price adjustment
     */
    private void adjustPricesWithAI() {
        try {
        for (Map.Entry<String, MarketData> entry : marketData.entrySet()) {
            String itemId = entry.getKey();
            MarketData data = entry.getValue();
            
            try {
                // Calculate AI factors
                double demandFactor = calculateDemandFactor(data);
                double supplyFactor = calculateSupplyFactor(data);
                double trendFactor = calculateTrendFactor(itemId);
                double volatilityFactor = calculateVolatilityFactor();
                
                // AI decision making
                double priceMultiplier = 1.0 + 
                    (demandFactor * DEMAND_INFLUENCE) +
                    (supplyFactor * SUPPLY_INFLUENCE) +
                    (trendFactor * TREND_INFLUENCE) +
                    (volatilityFactor * 0.1);
                
                // Apply constraints
                priceMultiplier = Math.max(0.5, Math.min(2.0, priceMultiplier));
                
                // Update prices
                double newBuyPrice = data.getBaseBuyPrice() * priceMultiplier;
                double newSellPrice = data.getBaseSellPrice() * priceMultiplier;
                
                data.setCurrentBuyPrice(newBuyPrice);
                data.setCurrentSellPrice(newSellPrice);
                
                // Record price history
                PriceHistory history = priceHistory.get(itemId);
                if (history != null) {
                    history.addPrice(newBuyPrice);
                }
                
                // Update shop item
                updateShopItemPrice(itemId, newBuyPrice, newSellPrice);
                
                Logger.debug("AI adjusted price for " + itemId + ": $" + String.format("%.2f", newBuyPrice));
            } catch (Exception e) {
                Logger.debug("Error adjusting price for " + itemId + ": " + e.getMessage());
            }
        }
        } catch (Exception e) {
            Logger.error("Error in AI price adjustment: " + e.getMessage());
        }
    }
    
    /**
     * AI-powered stock management
     */
    private void manageStockWithAI() {
        try {
        for (Map.Entry<String, MarketData> entry : marketData.entrySet()) {
            String itemId = entry.getKey();
            MarketData data = entry.getValue();
            
            try {
                // Calculate restock needs
                double demandRate = calculateDemandRate(data);
                double stockLevel = (double) data.getCurrentStock() / Math.max(1, data.getMaxStock());
                
                // AI restocking decision
                if (stockLevel < 0.3 && demandRate > 0.5) {
                    // High demand, low stock - restock aggressively
                    int restockAmount = (int) (data.getMaxStock() * 0.8);
                    data.setCurrentStock(Math.min(data.getCurrentStock() + restockAmount, data.getMaxStock()));
                    
                    Logger.debug("AI restocked " + itemId + " with " + restockAmount + " units (high demand)");
                } else if (stockLevel < 0.1) {
                    // Critical stock level - emergency restock
                    int restockAmount = (int) (data.getMaxStock() * 0.5);
                    data.setCurrentStock(Math.min(data.getCurrentStock() + restockAmount, data.getMaxStock()));
                    
                    Logger.debug("AI emergency restocked " + itemId + " with " + restockAmount + " units");
                }
                
                // Update shop item stock
                updateShopItemStock(itemId, data.getCurrentStock());
            } catch (Exception e) {
                Logger.debug("Error managing stock for " + itemId + ": " + e.getMessage());
            }
        }
        } catch (Exception e) {
            Logger.error("Error in AI stock management: " + e.getMessage());
        }
    }
    
    /**
     * Analyze market trends with AI
     */
    private void analyzeMarketTrends() {
        Logger.debug("AI analyzing market trends...");
        
        for (Map.Entry<String, MarketData> entry : marketData.entrySet()) {
            String itemId = entry.getKey();
            MarketData data = entry.getValue();
            PriceHistory history = priceHistory.get(itemId);
            
            // Trend analysis
            String trend = analyzePriceTrend(history);
            data.setTrend(trend);
            
            // Market sentiment
            String sentiment = calculateMarketSentiment(data);
            data.setSentiment(sentiment);
            
            Logger.debug("Item " + itemId + " - Trend: " + trend + ", Sentiment: " + sentiment);
        }
    }
    
    /**
     * Record transaction for AI learning
     */
    public void recordTransaction(String itemId, String type, int quantity, double price) {
        MarketData data = marketData.get(itemId);
        if (data != null) {
            if (type.equals("BUY")) {
                data.setBuyTransactions(data.getBuyTransactions() + quantity);
            } else if (type.equals("SELL")) {
                data.setSellTransactions(data.getSellTransactions() + quantity);
            }
            
            data.setLastTransactionTime(System.currentTimeMillis());
            
            // Adjust stock based on transaction
            if (type.equals("BUY") && data.getCurrentStock() > 0) {
                data.setCurrentStock(Math.max(0, data.getCurrentStock() - quantity));
            }
            
            Logger.debug("Recorded " + type + " transaction for " + itemId + ": " + quantity + " units");
        }
    }
    
    /**
     * Get current market price for item
     */
    public double getCurrentBuyPrice(String itemId) {
        MarketData data = marketData.get(itemId);
        return data != null ? data.getCurrentBuyPrice() : 0.0;
    }
    
    public double getCurrentSellPrice(String itemId) {
        MarketData data = marketData.get(itemId);
        return data != null ? data.getCurrentSellPrice() : 0.0;
    }
    
    /**
     * Get current stock for item
     */
    public int getCurrentStock(String itemId) {
        MarketData data = marketData.get(itemId);
        return data != null ? data.getCurrentStock() : 0;
    }
    
    /**
     * AI calculation methods
     */
    private double calculateDemandFactor(MarketData data) {
        long timeSinceLastTransaction = System.currentTimeMillis() - data.getLastTransactionTime();
        double timeDecay = Math.exp(-timeSinceLastTransaction / 3600000.0); // 1 hour decay
        
        double buyDemand = data.getBuyTransactions() * timeDecay;
        double sellSupply = data.getSellTransactions() * timeDecay;
        
        return Math.tanh((buyDemand - sellSupply) / 100.0) * PRICE_VOLATILITY;
    }
    
    private double calculateSupplyFactor(MarketData data) {
        double stockRatio = (double) data.getCurrentStock() / data.getMaxStock();
        return (0.5 - stockRatio) * PRICE_VOLATILITY;
    }
    
    private double calculateTrendFactor(String itemId) {
        PriceHistory history = priceHistory.get(itemId);
        if (history == null || history.getPrices().size() < 6) return 0.0;
        
        List<Double> prices = history.getPrices();
        int size = prices.size();
        
        // Ensure we have enough data points
        if (size < 6) return 0.0;
        
        try {
            double recentAvg = prices.subList(Math.max(0, size - 3), size).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double olderAvg = prices.subList(Math.max(0, size - 6), Math.max(0, size - 3)).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            if (olderAvg == 0) return 0.0;
            return ((recentAvg - olderAvg) / olderAvg) * 0.5;
        } catch (Exception e) {
            Logger.debug("Error calculating trend factor for " + itemId + ": " + e.getMessage());
            return 0.0;
        }
    }
        
        if (olderAvg == 0) return 0.0;
        return ((recentAvg - olderAvg) / olderAvg) * 0.5;
    }
    
    private double calculateVolatilityFactor() {
        return (random.nextGaussian() * 0.05); // 5% random volatility
    }
    
    private double calculateInitialDemand(ShopItem item) {
        // Base demand on item rarity and usefulness
        switch (item.getMaterial().toString()) {
            case "DIAMOND": case "EMERALD": case "NETHERITE_INGOT":
                return 0.9;
            case "IRON_INGOT": case "GOLD_INGOT": case "REDSTONE":
                return 0.7;
            case "STONE": case "DIRT": case "COBBLESTONE":
                return 0.3;
            default:
                return 0.5;
        }
    }
    
    private double calculateDemandRate(MarketData data) {
        long timeSinceLastTransaction = System.currentTimeMillis() - data.getLastTransactionTime();
        if (timeSinceLastTransaction > 3600000) return 0.1; // Low demand if no transactions in 1 hour
        
        return Math.min(1.0, (data.getBuyTransactions() + data.getSellTransactions()) / 50.0);
    }
    
    private String analyzePriceTrend(PriceHistory history) {
        List<Double> prices = history.getPrices();
        if (prices.size() < 3) return "STABLE";
        
        int size = prices.size();
        double recent = prices.get(size - 1);
        double older = prices.get(size - 3);
        
        double change = (recent - older) / older;
        
        if (change > 0.1) return "RISING";
        else if (change < -0.1) return "FALLING";
        else return "STABLE";
    }
    
    private String calculateMarketSentiment(MarketData data) {
        double stockRatio = (double) data.getCurrentStock() / data.getMaxStock();
        double demandRatio = calculateDemandRate(data);
        
        if (demandRatio > 0.7 && stockRatio < 0.3) return "BULLISH";
        else if (demandRatio < 0.3 && stockRatio > 0.7) return "BEARISH";
        else return "NEUTRAL";
    }
    
    private void updateShopItemPrice(String itemId, double buyPrice, double sellPrice) {
        // Update the actual shop item prices
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        for (ShopSection section : sections.values()) {
            ShopItem item = section.getItem(itemId);
            if (item != null) {
                item.setBuyPrice(buyPrice);
                item.setSellPrice(sellPrice);
                break;
            }
        }
    }
    
    private void updateShopItemStock(String itemId, int stock) {
        // Update the actual shop item stock
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        for (ShopSection section : sections.values()) {
            ShopItem item = section.getItem(itemId);
            if (item != null) {
                item.setStock(stock);
                break;
            }
        }
    }
    
    /**
     * Market data class
     */
    private static class MarketData {
        private final double baseBuyPrice;
        private final double baseSellPrice;
        private final int maxStock;
        private double currentBuyPrice;
        private double currentSellPrice;
        private int currentStock;
        private int buyTransactions;
        private int sellTransactions;
        private long lastTransactionTime;
        private String trend = "STABLE";
        private String sentiment = "NEUTRAL";
        
        public MarketData(double baseBuyPrice, double baseSellPrice, int maxStock, double demand, 
                         int buyTransactions, int sellTransactions, long lastTransactionTime) {
            this.baseBuyPrice = baseBuyPrice;
            this.baseSellPrice = baseSellPrice;
            this.maxStock = maxStock;
            this.currentBuyPrice = baseBuyPrice;
            this.currentSellPrice = baseSellPrice;
            this.currentStock = maxStock;
            this.buyTransactions = buyTransactions;
            this.sellTransactions = sellTransactions;
            this.lastTransactionTime = lastTransactionTime;
        }
        
        // Getters and setters
        public double getBaseBuyPrice() { return baseBuyPrice; }
        public double getBaseSellPrice() { return baseSellPrice; }
        public int getMaxStock() { return maxStock; }
        public double getCurrentBuyPrice() { return currentBuyPrice; }
        public void setCurrentBuyPrice(double currentBuyPrice) { this.currentBuyPrice = currentBuyPrice; }
        public double getCurrentSellPrice() { return currentSellPrice; }
        public void setCurrentSellPrice(double currentSellPrice) { this.currentSellPrice = currentSellPrice; }
        public int getCurrentStock() { return currentStock; }
        public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
        public int getBuyTransactions() { return buyTransactions; }
        public void setBuyTransactions(int buyTransactions) { this.buyTransactions = buyTransactions; }
        public int getSellTransactions() { return sellTransactions; }
        public void setSellTransactions(int sellTransactions) { this.sellTransactions = sellTransactions; }
        public long getLastTransactionTime() { return lastTransactionTime; }
        public void setLastTransactionTime(long lastTransactionTime) { this.lastTransactionTime = lastTransactionTime; }
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    }
    
    /**
     * Price history class
     */
    private static class PriceHistory {
        private final LinkedList<Double> prices = new LinkedList<>();
        
        public void addPrice(double price) {
            prices.addLast(price);
            if (prices.size() > 100) { // Keep last 100 prices
                prices.removeFirst();
            }
        }
        
        public List<Double> getPrices() {
            return new ArrayList<>(prices);
        }
    }
}