package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.Transaction;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enhanced transaction history GUI with proper navigation
 */
public class TransactionHistoryGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private int currentPage = 0;
    private List<Transaction> transactions = new ArrayList<>();
    
    public TransactionHistoryGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        loadTransactions();
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&3&l📋 &e&lTRANSACTION HISTORY"));
        
        // Fill background
        fillBackground(gui);
        
        // Add transactions
        addTransactions(gui);
        
        // Add navigation
        addNavigation(gui);
        
        // Add statistics
        addStatistics(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
    }
    
    /**
     * Load player transactions
     */
    private void loadTransactions() {
        // TODO: Load from database
        // For now, create some sample transactions
        transactions.clear();
        
        // Sample transactions for demonstration
        transactions.add(new Transaction("BUY", "Diamond", 5, 400.0, new Date(System.currentTimeMillis() - 3600000)));
        transactions.add(new Transaction("SELL", "Iron Ingot", 32, 384.0, new Date(System.currentTimeMillis() - 7200000)));
        transactions.add(new Transaction("BUY", "Golden Apple", 2, 200.0, new Date(System.currentTimeMillis() - 10800000)));
        transactions.add(new Transaction("SELL", "Wheat", 64, 128.0, new Date(System.currentTimeMillis() - 14400000)));
        transactions.add(new Transaction("BUY", "Stone", 128, 128.0, new Date(System.currentTimeMillis() - 18000000)));
    }
    
    /**
     * Add transactions to GUI
     */
    private void addTransactions(Inventory gui) {
        if (transactions.isEmpty()) {
            gui.setItem(22, new ItemBuilder(Material.BARRIER)
                    .setName("&c&l❌ &e&lNO TRANSACTIONS")
                    .setLore(Arrays.asList(
                            "&7▸ &fYou haven't made any transactions yet",
                            "&7▸ &fStart buying or selling items!",
                            "",
                            "&b&l💡 &bTip: Use /shop to get started!"
                    ))
                    .build());
            return;
        }
        
        // Display transactions (7 per page)
        int startIndex = currentPage * 7;
        int[] transactionSlots = {19, 20, 21, 22, 23, 24, 25};
        
        for (int i = 0; i < transactionSlots.length && (startIndex + i) < transactions.size(); i++) {
            Transaction transaction = transactions.get(startIndex + i);
            gui.setItem(transactionSlots[i], createTransactionItem(transaction, startIndex + i + 1));
        }
    }
    
    /**
     * Create transaction item
     */
    private ItemStack createTransactionItem(Transaction transaction, int number) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        String typeColor = transaction.getType().equals("BUY") ? "&a" : "&c";
        String typeSymbol = transaction.getType().equals("BUY") ? "💰" : "💸";
        
        Material displayMaterial = getTransactionMaterial(transaction.getItemName());
        
        return new ItemBuilder(displayMaterial)
                .setName(typeColor + "&l" + typeSymbol + " &e&l#" + number + " - " + transaction.getType())
                .setLore(Arrays.asList(
                        "&7▸ &fItem: &e" + transaction.getItemName(),
                        "&7▸ &fQuantity: &e" + transaction.getQuantity(),
                        "&7▸ &fAmount: " + typeColor + "$" + String.format("%.2f", transaction.getAmount()),
                        "&7▸ &fDate: &7" + dateFormat.format(transaction.getDate()),
                        "&7▸ &fType: " + typeColor + transaction.getType(),
                        "",
                        "&7▸ &fTransaction ID: &8#" + transaction.hashCode()
                ))
                .addGlow(transaction.getType().equals("BUY"))
                .build());
    }
    
    /**
     * Get material for transaction display
     */
    private Material getTransactionMaterial(String itemName) {
        switch (itemName.toLowerCase()) {
            case "diamond": return Material.DIAMOND;
            case "iron ingot": return Material.IRON_INGOT;
            case "golden apple": return Material.GOLDEN_APPLE;
            case "wheat": return Material.WHEAT;
            case "stone": return Material.STONE;
            default: return Material.PAPER;
        }
    }
    
    /**
     * Add statistics
     */
    private void addStatistics(Inventory gui) {
        double totalSpent = transactions.stream()
                .filter(t -> t.getType().equals("BUY"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        double totalEarned = transactions.stream()
                .filter(t -> t.getType().equals("SELL"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        long buyCount = transactions.stream()
                .filter(t -> t.getType().equals("BUY"))
                .count();
        
        long sellCount = transactions.stream()
                .filter(t -> t.getType().equals("SELL"))
                .count();
        
        gui.setItem(4, new ItemBuilder(Material.EMERALD)
                .setName("&a&l📊 &e&lTRANSACTION STATISTICS")
                .setLore(Arrays.asList(
                        "&7▸ &fTotal Transactions: &e" + transactions.size(),
                        "&7▸ &fPurchases: &a" + buyCount,
                        "&7▸ &fSales: &c" + sellCount,
                        "",
                        "&7▸ &fTotal Spent: &c$" + String.format("%.2f", totalSpent),
                        "&7▸ &fTotal Earned: &a$" + String.format("%.2f", totalEarned),
                        "&7▸ &fNet Profit: " + (totalEarned - totalSpent >= 0 ? "&a" : "&c") + 
                              "$" + String.format("%.2f", totalEarned - totalSpent),
                        "",
                        "&7▸ &fPage: &e" + (currentPage + 1) + "/" + getTotalPages()
                ))
                .addGlow()
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
        
        // Previous page
        if (currentPage > 0) {
            gui.setItem(48, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .setName("&e&l← &e&lPREVIOUS PAGE")
                    .setLore(Arrays.asList(
                            "&7▸ &fPage " + currentPage + " of " + getTotalPages(),
                            "",
                            "&a&l➤ &aClick to go back!"
                    ))
                    .build());
        }
        
        // Next page
        if (hasNextPage()) {
            gui.setItem(50, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .setName("&e&l→ &e&lNEXT PAGE")
                    .setLore(Arrays.asList(
                            "&7▸ &fPage " + (currentPage + 2) + " of " + getTotalPages(),
                            "",
                            "&a&l➤ &aClick to continue!"
                    ))
                    .build());
        }
        
        // Filter options
        gui.setItem(46, new ItemBuilder(Material.HOPPER)
                .setName("&b&l🔍 &e&lFILTER OPTIONS")
                .setLore(Arrays.asList(
                        "&7▸ &fShow only purchases",
                        "&7▸ &fShow only sales",
                        "&7▸ &fShow all transactions",
                        "",
                        "&b&l➤ &bClick to filter!"
                ))
                .build());
                .build());
        
        // Export data
        gui.setItem(52, new ItemBuilder(Material.WRITABLE_BOOK)
                .setName("&6&l📄 &e&lEXPORT DATA")
                .setLore(Arrays.asList(
                        "&7▸ &fExport transaction history",
                        "&7▸ &fGenerate detailed report",
                        "",
                        "&6&l➤ &6Click to export!"
                ))
                .build());
        
        // Refresh
        gui.setItem(53, new ItemBuilder(Material.COMPASS)
                .setName("&a&l🔄 &e&lREFRESH")
                .setLore(Arrays.asList(
                        "&7▸ &fReload transaction data",
                        "&7▸ &fUpdate statistics",
                        "",
                        "&a&l➤ &aClick to refresh!"
                ))
                .build());
    }
    
    /**
     * Navigation methods
     */
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
    
    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) transactions.size() / 7));
    }
    
    private boolean hasNextPage() {
        return (currentPage + 1) < getTotalPages();
    }
}