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
 * AI-Powered Marketplace with Real-Time Dynamic Pricing and Smart Stock Management
 */
public class AIMarketplace {
    
    private final EasyShopGUI plugin;
    private final Map<String, MarketData> marketData = new ConcurrentHashMap<>();
    private final Map<String, PriceHistory> priceHistory = new ConcurrentHashMap<>();
    private final Map<String, StockData> stockData = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    // AI Configuration
    private final double PRICE_VOLATILITY;
    private final double DEMAND_INFLUENCE = 0.8;
    private final double SUPPLY_INFLUENCE = 0.6;
    private final double TREND_INFLUENCE = 0.4;
    private final int HISTORY_SIZE = 100;
    private final int UPDATE_INTERVAL;
    
    public AIMarketplace(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.PRICE_VOLATILITY = plugin.getConfigManager().getMainConfig().getDouble("ai-marketplace.price-volatility", 0.15);
        this.UPDATE_INTERVAL = plugin.getConfigManager().getMainConfig().getInt("ai-marketplace.update-interval", 300);
        
        if (plugin.getConfigManager().getMainConfig().getBoolean("ai-marketplace.enabled", true)) {
            initializeMarketData();
            startAIEngine();
        }
    }
    
    /**
     * Initialize market data for all items
     */
    private void initializeMarketData() {
        try {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    Map<String, ShopSection> sections = plugin.getGuiManager().getSections();

                    if (sections == null || sections.isEmpty()) {
                        Logger.warn("No sections available for AI Marketplace initialization");
                        return;
                    }

                    for (ShopSection section : sections.values()) {
                        for (ShopItem item : section.getItems()) {
                            String itemId = item.getId();

                            MarketData data = new MarketData(
                                item.getBuyPrice(),
                                item.getSellPrice(),
                                item.getStock() == -1 ? 1000 : item.getStock(),
                                calculateInitialDemand(item),
                                0, 0,
                                System.currentTimeMillis()
                            );

                            marketData.put(itemId, data);
                            priceHistory.put(itemId, new PriceHistory());

                            StockData stock = new StockData(
                                item.getStock() == -1 ? 1000 : item.getStock(),
                                item.getStock() == -1 ? 1000 : item.getStock(),
                                System.currentTimeMillis()
                            );
                            stockData.put(itemId, stock);

                            Logger.debug("Initialized market data for: " + itemId);
                        }
                    }

                    Logger.info("AI Marketplace initialized with " + marketData.size() + " items");
                } catch (Exception e) {
                    Logger.error("Error initializing AI Marketplace: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 20L);
        } catch (Exception e) {
            Logger.error("Error scheduling AI Marketplace initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Start the AI engine with real-time updates
     */
    private void startAIEngine() {
        // Real-time price adjustment engine (every 30 seconds)
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    adjustPricesWithAI();
                } catch (Exception e) {
                    Logger.error("Error in AI price adjustment: " + e.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(plugin, 600L, 600L); // 30 seconds
        
        // Stock management engine (every 1 minute)
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    manageStockWithAI();
                } catch (Exception e) {
                    Logger.error("Error in AI stock management: " + e.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L); // 1 minute
        
        // Market analysis engine (every 5 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    analyzeMarketTrends();
                } catch (Exception e) {
                    Logger.error("Error in market analysis: " + e.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(plugin, 6000L, 6000L); // 5 minutes
        
        Logger.info("AI Marketplace engines started - Real-time pricing active!");
    }
    
    /**
     * AI-powered price adjustment with real-time updates
     */
    private void adjustPricesWithAI() {
        for (Map.Entry<String, MarketData> entry : marketData.entrySet()) {
            String itemId = entry.getKey();
            MarketData data = entry.getValue();
            
            try {
                // Calculate AI factors safely
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
                
                // Apply realistic constraints
                priceMultiplier = Math.max(0.3, Math.min(3.0, priceMultiplier));
                
                // Update prices
                double newBuyPrice = data.getBaseBuyPrice() * priceMultiplier;
                double newSellPrice = data.getBaseSellPrice() * priceMultiplier;
                
                data.setCurrentBuyPrice(newBuyPrice);
                data.setCurrentSellPrice(newSellPrice);
                
                // Record price history safely
                PriceHistory history = priceHistory.get(itemId);
                if (history != null) {
                    history.addPrice(newBuyPrice);
                }
                
                // Update shop item in real-time
                updateShopItemPrice(itemId, newBuyPrice, newSellPrice);
                
                Logger.debug("AI adjusted price for " + itemId + ": Buy $" + String.format("%.2f", newBuyPrice) + 
                           ", Sell $" + String.format("%.2f", newSellPrice));
                
            } catch (Exception e) {
                Logger.debug("Error adjusting price for " + itemId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Enhanced stock management with real-time updates
     */
    private void manageStockWithAI() {
        for (Map.Entry<String, StockData> entry : stockData.entrySet()) {
            String itemId = entry.getKey();
            StockData stock = entry.getValue();
            MarketData market = marketData.get(itemId);
            
            if (market == null) continue;
            
            try {
                // Calculate restock needs
                double demandRate = calculateDemandRate(market);
                double stockLevel = (double) stock.getCurrentStock() / Math.max(1, stock.getMaxStock());
                long timeSinceRestock = System.currentTimeMillis() - stock.getLastRestockTime();
                
                // AI restocking decision
                boolean shouldRestock = false;
                int restockAmount = 0;
                
                if (stockLevel < 0.1) {
                    // Critical stock level - emergency restock
                    restockAmount = (int) (stock.getMaxStock() * 0.8);
                    shouldRestock = true;
                    Logger.debug("Emergency restock triggered for " + itemId);
                } else if (stockLevel < 0.3 && demandRate > 0.6) {
                    // High demand, low stock - smart restock
                    restockAmount = (int) (stock.getMaxStock() * 0.6);
                    shouldRestock = true;
                    Logger.debug("High demand restock triggered for " + itemId);
                } else if (stockLevel < 0.5 && timeSinceRestock > 1800000) { // 30 minutes
                    // Regular restock cycle
                    restockAmount = (int) (stock.getMaxStock() * 0.4);
                    shouldRestock = true;
                    Logger.debug("Regular restock triggered for " + itemId);
                }
                
                if (shouldRestock) {
                    int newStock = Math.min(stock.getCurrentStock() + restockAmount, stock.getMaxStock());
                    stock.setCurrentStock(newStock);
                    stock.setLastRestockTime(System.currentTimeMillis());
                    
                    // Update shop item stock in real-time
                    updateShopItemStock(itemId, newStock);
                    
                    Logger.debug("AI restocked " + itemId + " to " + newStock + " units");
                }
                
            } catch (Exception e) {
                Logger.debug("Error managing stock for " + itemId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Process purchase and update stock/pricing
     */
    public boolean processPurchase(String itemId, int quantity) {
        StockData stock = stockData.get(itemId);
        MarketData market = marketData.get(itemId);
        
        if (stock == null || market == null) return false;
        
        // Check stock availability
        if (stock.getCurrentStock() != -1 && stock.getCurrentStock() < quantity) {
            return false; // Insufficient stock
        }
        
        // Update stock
        if (stock.getCurrentStock() != -1) {
            stock.setCurrentStock(Math.max(0, stock.getCurrentStock() - quantity));
            updateShopItemStock(itemId, stock.getCurrentStock());
        }
        
        // Record transaction for AI learning
        market.setBuyTransactions(market.getBuyTransactions() + quantity);
        market.setLastTransactionTime(System.currentTimeMillis());
        
        // Immediate price adjustment based on purchase
        adjustPriceAfterTransaction(itemId, "BUY", quantity);
        
        Logger.debug("Processed purchase: " + quantity + "x " + itemId + ", Stock: " + stock.getCurrentStock());
        return true;
    }
    
    /**
     * Process sale and update stock/pricing
     */
    public void processSale(String itemId, int quantity) {
        StockData stock = stockData.get(itemId);
        MarketData market = marketData.get(itemId);
        
        if (stock == null || market == null) return;
        
        // Update stock (add sold items back to shop)
        if (stock.getCurrentStock() != -1) {
            stock.setCurrentStock(Math.min(stock.getCurrentStock() + quantity, stock.getMaxStock()));
            updateShopItemStock(itemId, stock.getCurrentStock());
        }
        
        // Record transaction for AI learning
        market.setSellTransactions(market.getSellTransactions() + quantity);
        market.setLastTransactionTime(System.currentTimeMillis());
        
        // Immediate price adjustment based on sale
        adjustPriceAfterTransaction(itemId, "SELL", quantity);
        
        Logger.debug("Processed sale: " + quantity + "x " + itemId + ", Stock: " + stock.getCurrentStock());
    }
    
    /**
     * Immediate price adjustment after transaction
     */
    private void adjustPriceAfterTransaction(String itemId, String type, int quantity) {
        MarketData data = marketData.get(itemId);
        if (data == null) return;
        
        try {
            double priceChange = 0.0;
            
            if (type.equals("BUY")) {
                // Buying increases demand, increases price
                priceChange = (quantity / 64.0) * 0.02; // 2% per stack
            } else if (type.equals("SELL")) {
                // Selling increases supply, decreases price
                priceChange = -(quantity / 64.0) * 0.015; // -1.5% per stack
            }
            
            // Apply immediate price change
            double currentMultiplier = data.getCurrentBuyPrice() / data.getBaseBuyPrice();
            double newMultiplier = Math.max(0.5, Math.min(2.0, currentMultiplier + priceChange));
            
            double newBuyPrice = data.getBaseBuyPrice() * newMultiplier;
            double newSellPrice = data.getBaseSellPrice() * newMultiplier;
            
            data.setCurrentBuyPrice(newBuyPrice);
            data.setCurrentSellPrice(newSellPrice);
            
            // Update shop item immediately
            updateShopItemPrice(itemId, newBuyPrice, newSellPrice);
            
            Logger.debug("Immediate price adjustment for " + itemId + ": " + (priceChange > 0 ? "+" : "") + 
                        String.format("%.1f", priceChange * 100) + "%");
            
        } catch (Exception e) {
            Logger.debug("Error in immediate price adjustment: " + e.getMessage());
        }
    }
    
    /**
     * Safe trend factor calculation
     */
    private double calculateTrendFactor(String itemId) {
        try {
            PriceHistory history = priceHistory.get(itemId);
            if (history == null) return 0.0;
            
            List<Double> prices = history.getPrices();
            if (prices == null || prices.size() < 6) return 0.0;
            
            int size = prices.size();
            
            // Safe index calculation
            int recentStart = Math.max(0, size - 3);
            int recentEnd = size;
            int olderStart = Math.max(0, size - 6);
            int olderEnd = Math.max(0, size - 3);
            
            // Ensure valid ranges
            if (recentStart >= recentEnd || olderStart >= olderEnd) return 0.0;
            
            List<Double> recentPrices = prices.subList(recentStart, recentEnd);
            List<Double> olderPrices = prices.subList(olderStart, olderEnd);
            
            if (recentPrices.isEmpty() || olderPrices.isEmpty()) return 0.0;
            
            double recentAvg = recentPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double olderAvg = olderPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            if (olderAvg == 0) return 0.0;
            return ((recentAvg - olderAvg) / olderAvg) * 0.5;
            
        } catch (Exception e) {
            Logger.debug("Error calculating trend factor for " + itemId + ": " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Calculate demand factor
     */
    private double calculateDemandFactor(MarketData data) {
        try {
            long timeSinceLastTransaction = System.currentTimeMillis() - data.getLastTransactionTime();
            double timeDecay = Math.exp(-timeSinceLastTransaction / 3600000.0); // 1 hour decay
            
            double buyDemand = data.getBuyTransactions() * timeDecay;
            double sellSupply = data.getSellTransactions() * timeDecay;
            
            return Math.tanh((buyDemand - sellSupply) / 100.0) * PRICE_VOLATILITY;
        } catch (Exception e) {
            Logger.debug("Error calculating demand factor: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Calculate supply factor
     */
    private double calculateSupplyFactor(MarketData data) {
        try {
            StockData stock = stockData.get(data.toString());
            if (stock == null) return 0.0;
            
            double stockRatio = (double) stock.getCurrentStock() / Math.max(1, stock.getMaxStock());
            return (0.5 - stockRatio) * PRICE_VOLATILITY;
        } catch (Exception e) {
            Logger.debug("Error calculating supply factor: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Calculate volatility factor
     */
    private double calculateVolatilityFactor() {
        return (random.nextGaussian() * 0.02); // 2% random volatility
    }
    
    /**
     * Analyze market trends safely
     */
    private void analyzeMarketTrends() {
        Logger.debug("AI analyzing market trends...");
        
        for (Map.Entry<String, MarketData> entry : marketData.entrySet()) {
            String itemId = entry.getKey();
            MarketData data = entry.getValue();
            
            try {
                PriceHistory history = priceHistory.get(itemId);
                
                // Trend analysis
                String trend = analyzePriceTrend(history);
                data.setTrend(trend);
                
                // Market sentiment
                String sentiment = calculateMarketSentiment(data);
                data.setSentiment(sentiment);
                
                Logger.debug("Item " + itemId + " - Trend: " + trend + ", Sentiment: " + sentiment);
            } catch (Exception e) {
                Logger.debug("Error analyzing trends for " + itemId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Safe price trend analysis
     */
    private String analyzePriceTrend(PriceHistory history) {
        try {
            if (history == null) return "STABLE";
            
            List<Double> prices = history.getPrices();
            if (prices == null || prices.size() < 3) return "STABLE";
            
            int size = prices.size();
            if (size < 3) return "STABLE";
            
            double recent = prices.get(size - 1);
            double older = prices.get(Math.max(0, size - 3));
            
            if (older == 0) return "STABLE";
            
            double change = (recent - older) / older;
            
            if (change > 0.05) return "RISING";
            else if (change < -0.05) return "FALLING";
            else return "STABLE";
            
        } catch (Exception e) {
            Logger.debug("Error analyzing price trend: " + e.getMessage());
            return "STABLE";
        }
    }
    
    /**
     * Calculate market sentiment
     */
    private String calculateMarketSentiment(MarketData data) {
        try {
            double demandRatio = calculateDemandRate(data);
            
            if (demandRatio > 0.7) return "BULLISH";
            else if (demandRatio < 0.3) return "BEARISH";
            else return "NEUTRAL";
        } catch (Exception e) {
            return "NEUTRAL";
        }
    }
    
    /**
     * Calculate demand rate safely
     */
    private double calculateDemandRate(MarketData data) {
        try {
            long timeSinceLastTransaction = System.currentTimeMillis() - data.getLastTransactionTime();
            if (timeSinceLastTransaction > 3600000) return 0.1; // Low demand if no transactions in 1 hour
            
            return Math.min(1.0, (data.getBuyTransactions() + data.getSellTransactions()) / 50.0);
        } catch (Exception e) {
            return 0.1;
        }
    }
    
    /**
     * Calculate initial demand based on item type
     */
    private double calculateInitialDemand(ShopItem item) {
        String materialName = item.getMaterial().toString();
        
        // Precious materials
        if (materialName.contains("DIAMOND") || materialName.contains("EMERALD") || 
            materialName.contains("NETHERITE")) {
            return 0.9;
        }
        
        // Common metals
        if (materialName.contains("IRON") || materialName.contains("GOLD") || 
            materialName.contains("REDSTONE")) {
            return 0.7;
        }
        
        // Building blocks
        if (materialName.contains("STONE") || materialName.contains("DIRT") || 
            materialName.contains("COBBLESTONE")) {
            return 0.3;
        }
        
        // Food items
        if (materialName.contains("BREAD") || materialName.contains("APPLE") || 
            materialName.contains("BEEF") || materialName.contains("CHICKEN")) {
            return 0.6;
        }
        
        return 0.5; // Default demand
    }
    
    /**
     * Update shop item price in real-time
     */
    private void updateShopItemPrice(String itemId, double buyPrice, double sellPrice) {
        try {
            Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
            for (ShopSection section : sections.values()) {
                ShopItem item = section.getItem(itemId);
                if (item != null) {
                    item.setBuyPrice(buyPrice);
                    item.setSellPrice(sellPrice);
                    break;
                }
            }
        } catch (Exception e) {
            Logger.debug("Error updating shop item price: " + e.getMessage());
        }
    }
    
    /**
     * Update shop item stock in real-time
     */
    private void updateShopItemStock(String itemId, int stock) {
        try {
            Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
            for (ShopSection section : sections.values()) {
                ShopItem item = section.getItem(itemId);
                if (item != null) {
                    item.setStock(stock);
                    break;
                }
            }
        } catch (Exception e) {
            Logger.debug("Error updating shop item stock: " + e.getMessage());
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
        StockData stock = stockData.get(itemId);
        return stock != null ? stock.getCurrentStock() : -1;
    }
    
    /**
     * Record transaction for AI learning
     */
    public void recordTransaction(String itemId, String type, int quantity, double price) {
        try {
            if (type.equals("BUY")) {
                processPurchase(itemId, quantity);
            } else if (type.equals("SELL")) {
                processSale(itemId, quantity);
            }
        } catch (Exception e) {
            Logger.debug("Error recording transaction: " + e.getMessage());
        }
    }
    
    /**
     * Market data class
     */
    private static class MarketData {
        private final double baseBuyPrice;
        private final double baseSellPrice;
        private double currentBuyPrice;
        private double currentSellPrice;
        private int buyTransactions;
        private int sellTransactions;
        private long lastTransactionTime;
        private String trend = "STABLE";
        private String sentiment = "NEUTRAL";
        
        public MarketData(double baseBuyPrice, double baseSellPrice, int maxStock, double demand, 
                         int buyTransactions, int sellTransactions, long lastTransactionTime) {
            this.baseBuyPrice = baseBuyPrice;
            this.baseSellPrice = baseSellPrice;
            this.currentBuyPrice = baseBuyPrice;
            this.currentSellPrice = baseSellPrice;
            this.buyTransactions = buyTransactions;
            this.sellTransactions = sellTransactions;
            this.lastTransactionTime = lastTransactionTime;
        }
        
        // Getters and setters
        public double getBaseBuyPrice() { return baseBuyPrice; }
        public double getBaseSellPrice() { return baseSellPrice; }
        public double getCurrentBuyPrice() { return currentBuyPrice; }
        public void setCurrentBuyPrice(double currentBuyPrice) { this.currentBuyPrice = currentBuyPrice; }
        public double getCurrentSellPrice() { return currentSellPrice; }
        public void setCurrentSellPrice(double currentSellPrice) { this.currentSellPrice = currentSellPrice; }
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
     * Stock data class
     */
    private static class StockData {
        private int currentStock;
        private final int maxStock;
        private long lastRestockTime;
        
        public StockData(int currentStock, int maxStock, long lastRestockTime) {
            this.currentStock = currentStock;
            this.maxStock = maxStock;
            this.lastRestockTime = lastRestockTime;
        }
        
        public int getCurrentStock() { return currentStock; }
        public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
        public int getMaxStock() { return maxStock; }
        public long getLastRestockTime() { return lastRestockTime; }
        public void setLastRestockTime(long lastRestockTime) { this.lastRestockTime = lastRestockTime; }
    }
    
    /**
     * Price history class
     */
    private static class PriceHistory {
        private final LinkedList<Double> prices = new LinkedList<>();
        
        public void addPrice(double price) {
            synchronized (prices) {
                prices.addLast(price);
                if (prices.size() > HISTORY_SIZE) {
                    prices.removeFirst();
                }
            }
        }
        
        public List<Double> getPrices() {
            synchronized (prices) {
                return new ArrayList<>(prices);
            }
        }
    }
}