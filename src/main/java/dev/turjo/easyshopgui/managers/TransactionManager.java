package dev.turjo.easyshopgui.managers;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.Transaction;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for handling transaction operations
 */
public class TransactionManager {
    
    private final EasyShopGUI plugin;
    private final Map<UUID, List<Transaction>> playerTransactions;
    private final int MAX_TRANSACTIONS_PER_PLAYER = 100;
    
    public TransactionManager(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.playerTransactions = new ConcurrentHashMap<>();
    }
    
    /**
     * Record a transaction
     */
    public void recordTransaction(Player player, String type, String itemName, int quantity, double amount) {
        UUID playerId = player.getUniqueId();
        
        Transaction transaction = new Transaction(type, itemName, quantity, amount, new Date());
        transaction.setPlayerId(playerId.toString());
        
        List<Transaction> transactions = playerTransactions.computeIfAbsent(playerId, k -> new ArrayList<>());
        
        // Add transaction to the beginning of the list (most recent first)
        transactions.add(0, transaction);
        
        // Limit the number of transactions per player
        if (transactions.size() > MAX_TRANSACTIONS_PER_PLAYER) {
            transactions.remove(transactions.size() - 1);
        }
        
        Logger.debug("Recorded transaction for " + player.getName() + ": " + type + " " + quantity + "x " + itemName + " for $" + amount);
        
        // TODO: Save to database if enabled
        saveTransactionToDatabase(transaction);
    }
    
    /**
     * Get transactions for a player
     */
    public List<Transaction> getPlayerTransactions(Player player) {
        return playerTransactions.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    
    /**
     * Get recent transactions for a player
     */
    public List<Transaction> getRecentTransactions(Player player, int limit) {
        List<Transaction> allTransactions = getPlayerTransactions(player);
        return allTransactions.subList(0, Math.min(limit, allTransactions.size()));
    }
    
    /**
     * Clear transactions for a player
     */
    public void clearPlayerTransactions(Player player) {
        playerTransactions.remove(player.getUniqueId());
        Logger.debug("Cleared transactions for " + player.getName());
    }
    
    /**
     * Get total spent by player
     */
    public double getTotalSpent(Player player) {
        return getPlayerTransactions(player).stream()
                .filter(t -> t.getType().equals("BUY"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Get total earned by player
     */
    public double getTotalEarned(Player player) {
        return getPlayerTransactions(player).stream()
                .filter(t -> t.getType().equals("SELL"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Get transaction count for player
     */
    public int getTransactionCount(Player player) {
        return getPlayerTransactions(player).size();
    }
    
    /**
     * Save transaction to database (placeholder)
     */
    private void saveTransactionToDatabase(Transaction transaction) {
        // TODO: Implement database saving
        // This would save the transaction to MySQL/SQLite database
    }
    
    /**
     * Load transactions from database (placeholder)
     */
    public void loadPlayerTransactions(Player player) {
        // TODO: Implement database loading
        // This would load transactions from MySQL/SQLite database
    }
}