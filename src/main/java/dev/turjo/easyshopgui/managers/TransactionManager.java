package dev.turjo.easyshopgui.managers;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.Transaction;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for handling transaction operations.
 *
 * Transactions are kept in memory for fast access (GUIs read this on the main thread
 * and can't wait on a database round-trip) and persisted to the database
 * asynchronously in the background. saveTransactionToDatabase()/loadPlayerTransactions()
 * were previously empty TODO stubs, so every transaction was lost on restart despite
 * MySQL/HikariCP being full project dependencies - this now actually uses them.
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
        synchronized (transactions) {
            transactions.add(0, transaction);
            
            // Limit the number of transactions per player
            if (transactions.size() > MAX_TRANSACTIONS_PER_PLAYER) {
                transactions.remove(transactions.size() - 1);
            }
        }
        
        Logger.debug("Recorded transaction for " + player.getName() + ": " + type + " " + quantity + "x " + itemName + " for $" + amount);
        
        saveTransactionToDatabase(player, transaction);
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
     * Clear transactions for a player (in-memory only - does not delete database history)
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
     * Save a transaction to the database asynchronously. Runs off the main thread since
     * blocking JDBC calls on the main thread would stall the whole server on every
     * purchase/sale.
     */
    private void saveTransactionToDatabase(Player player, Transaction transaction) {
        if (plugin.getDatabaseManager() == null || !plugin.getDatabaseManager().isInitialized()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO eshop_transactions " +
                    "(player_uuid, player_name, transaction_type, item_name, amount, price, timestamp) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, transaction.getPlayerId());
                stmt.setString(2, player.getName());
                stmt.setString(3, transaction.getType());
                stmt.setString(4, transaction.getItemName());
                stmt.setInt(5, transaction.getQuantity());
                stmt.setDouble(6, transaction.getAmount());
                stmt.setLong(7, transaction.getDate().getTime());

                stmt.executeUpdate();

            } catch (SQLException e) {
                Logger.error("Failed to save transaction to database: " + e.getMessage());
            }
        });
    }
    
    /**
     * Load a player's transaction history from the database into memory. Called on
     * join (see PlayerListener) so transaction history and history GUI content survive
     * a restart rather than starting empty every session.
     */
    public void loadPlayerTransactions(Player player) {
        if (plugin.getDatabaseManager() == null || !plugin.getDatabaseManager().isInitialized()) {
            return;
        }

        UUID playerId = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "SELECT transaction_type, item_name, amount, price, timestamp FROM eshop_transactions " +
                    "WHERE player_uuid = ? ORDER BY timestamp DESC LIMIT ?";

            List<Transaction> loaded = new ArrayList<>();

            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                stmt.setInt(2, MAX_TRANSACTIONS_PER_PLAYER);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Transaction transaction = new Transaction(
                                rs.getString("transaction_type"),
                                rs.getString("item_name"),
                                rs.getInt("amount"),
                                rs.getDouble("price"),
                                new Date(rs.getLong("timestamp"))
                        );
                        transaction.setPlayerId(playerId.toString());
                        loaded.add(transaction);
                    }
                }

                if (!loaded.isEmpty()) {
                    playerTransactions.put(playerId, loaded);
                    Logger.debug("Loaded " + loaded.size() + " transactions for " + player.getName());
                }

            } catch (SQLException e) {
                Logger.error("Failed to load transactions for " + player.getName() + ": " + e.getMessage());
            }
        });
    }
}
