package dev.turjo.easyshopgui.currency;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;

/**
 * Advanced Paper Currency System with Anti-Dupe Protection
 */
public class PaperCurrency {
    
    private final EasyShopGUI plugin;
    // Aggregate audit counters (not per-item tracking - cheques are intentionally
    // stackable/anonymous, see isCheque()'s javadoc). Exposed via getStatistics() for
    // admin visibility into how much has been withdrawn vs. redeemed.
    private double totalIssued = 0.0;
    private double totalRedeemed = 0.0;
    private int chequesIssuedCount = 0;
    private int chequesRedeemedCount = 0;
    private final Map<UUID, Long> lastWithdrawTime = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    
    // NBT key: the only piece of data stored on a cheque item. See createChequeItem()
    // and isCheque() for why nothing else (ID/issuer/timestamp/signature) is stored.
    private final NamespacedKey CHEQUE_AMOUNT_KEY;
    
    public PaperCurrency(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.CHEQUE_AMOUNT_KEY = new NamespacedKey(plugin, "cheque_amount");
    }
    
    /**
     * Withdraw money as paper cheque
     */
    public boolean withdrawCheque(Player player, double amount) {
        // Validation checks
        if (amount <= 0) {
            player.sendMessage("§c💰 Amount must be positive!");
            return false;
        }
        
        if (amount > 1000000) {
            player.sendMessage("§c💰 Maximum cheque amount is $1,000,000!");
            return false;
        }
        
        // Anti-spam protection
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (lastWithdrawTime.containsKey(playerId)) {
            long timeDiff = currentTime - lastWithdrawTime.get(playerId);
            if (timeDiff < 2000) { // 2 second cooldown
                player.sendMessage("§c💰 Please wait before withdrawing another cheque!");
                return false;
            }
        }
        
        // Check balance
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        if (balance < amount) {
            player.sendMessage("§c💰 Insufficient funds! You have $" + String.format("%.2f", balance));
            return false;
        }
        
        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§c📦 Your inventory is full!");
            return false;
        }
        
        // Generate a reference ID for the player's receipt message only - not stored on
        // the item itself, since cheques are intentionally anonymous/stackable (storing
        // a unique ID per item would prevent identical-amount cheques from stacking).
        String chequeId = generateSecureChequeId();
        
        // Withdraw money
        plugin.getEconomyManager().getEconomy().withdrawPlayer(player, amount);
        
        // Create cheque item
        ItemStack cheque = createChequeItem(amount);
        
        // Track aggregate issuance for admin stats
        totalIssued += amount;
        chequesIssuedCount++;
        
        // Give cheque to player
        player.getInventory().addItem(cheque);
        lastWithdrawTime.put(playerId, currentTime);
        
        // Success message
        player.sendMessage("§a💰 Successfully withdrew $" + String.format("%.2f", amount) + " as a cheque!");
        player.sendMessage("§e📄 Reference: §f" + chequeId.substring(0, 8) + "...");
        player.sendMessage("§7💡 Right-click the cheque to redeem it!");
        
        Logger.info("Player " + player.getName() + " withdrew $" + amount + " as cheque " + chequeId);
        return true;
    }
    
    /**
     * Redeem paper cheque - SIMPLIFIED for trading compatibility
     */
    public boolean redeemCheque(Player player, ItemStack chequeItem) {
        if (!isCheque(chequeItem)) {
            player.sendMessage("§c💰 This is not a valid cheque!");
            return false;
        }

        ItemMeta meta = chequeItem.getItemMeta();
        if (meta == null) return false;

        // Extract cheque data
        Double amount = meta.getPersistentDataContainer().get(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE);

        if (amount == null || amount <= 0) {
            player.sendMessage("§c💰 Invalid cheque amount!");
            return false;
        }

        // Simple redemption - no per-item tracking, cheques are intentionally
        // stackable/anonymous so they can be traded and used with Shopkeeper-style
        // plugins. We still record aggregate redemption stats for admin visibility.
        plugin.getEconomyManager().getEconomy().depositPlayer(player, amount);
        recordRedemption(amount);

        // Remove cheque from inventory
        chequeItem.setAmount(0);

        // Success messages
        player.sendMessage("§a💰 Successfully redeemed cheque for $" + String.format("%.2f", amount) + "!");

        Logger.info("Player " + player.getName() + " redeemed cheque for $" + amount);
        return true;
    }
    
    /**
     * Enhanced Shopkeeper compatibility - check if cheque can be used as payment
     */
    public boolean canUseAsPayment(ItemStack cheque, double requiredAmount) {
        if (!isCheque(cheque)) return false;
        
        double chequeAmount = getChequeAmount(cheque);
        return Math.abs(chequeAmount - requiredAmount) < 0.01; // Allow small floating point differences
    }
    
    /**
     * Convert cheque to currency value for trading plugins
     */
    public double getTradeValue(ItemStack cheque) {
        return getChequeAmount(cheque);
    }
    
    /**
     * Create SIMPLE cheque item for Shopkeeper compatibility.
     *
     * Deliberately does not store a unique ID, issuer, timestamp, or signature - only
     * the amount. Any of those would make every cheque's ItemMeta unique, which would
     * prevent identical-value cheques from stacking, breaking the "stackable and
     * tradeable" behaviour advertised in the lore below and needed for Shopkeeper-style
     * trade compatibility.
     */
    private ItemStack createChequeItem(double amount) {
        // Create a standardized cheque that can be stacked and traded
        ItemStack cheque = new ItemBuilder(Material.PAPER)
                .setName("§6§l💰 $" + String.format("%.0f", amount) + " CHEQUE")
                .setLore(Arrays.asList(
                        "§7▸ §fValue: §a$" + String.format("%.2f", amount),
                        "",
                        "§6§l💱 SHOPKEEPER COMPATIBLE:",
                        "§7▸ §fWorks with Shopkeeper trades",
                        "§7▸ §fTrade as currency with players",
                        "§7▸ §fStackable and tradeable",
                        "",
                        "§e§l💡 HOW TO USE:",
                        "§7▸ §fRight-click to redeem for money",
                        "§7▸ §fUse in Shopkeeper trades",
                        "§7▸ §fTrade with other players",
                        "",
                        "§8§l━━━━━━━━━━━━━━━━━━━━━━━",
                        "§8§oEasyShopGUI Bank • Universal Currency"
                ))
                .addGlow()
                .build();
        
        // Only store amount - no unique IDs for Shopkeeper compatibility
        ItemMeta meta = cheque.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE, amount);
            cheque.setItemMeta(meta);
        }

        return cheque;
    }
    
    /**
     * Check if item is a valid cheque with specific amount (for Shopkeeper compatibility)
     */
    public boolean isChequeWithAmount(ItemStack item, double requiredAmount) {
        if (!isCheque(item)) return false;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        
        Double chequeAmount = meta.getPersistentDataContainer().get(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE);
        return chequeAmount != null && Math.abs(chequeAmount - requiredAmount) < 0.01; // Allow small floating point differences
    }
    
    /**
     * Get cheque amount from item
     */
    public double getChequeAmount(ItemStack item) {
        if (!isCheque(item)) return 0.0;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0.0;
        
        Double amount = meta.getPersistentDataContainer().get(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE);
        return amount != null ? amount : 0.0;
    }
    
    /**
     * Check if item is a cheque.
     *
     * Fixed: this used to check for CHEQUE_ID_KEY, but createChequeItem() never wrote
     * that key to the item (only CHEQUE_AMOUNT_KEY is written - see that method's
     * comments about staying Shopkeeper-compatible/stackable). That meant isCheque()
     * returned false for every cheque this plugin itself issued, which made every
     * redemption path unusable: /withdraw would deduct real balance and hand the player
     * a paper item that could never be redeemed back through the normal right-click
     * flow. Checking CHEQUE_AMOUNT_KEY instead - the key that is actually written -
     * makes this match the plugin's own output while keeping cheques stackable (no
     * per-item unique ID is required for this check).
     */
    public boolean isCheque(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE);
    }
    
    /**
     * Generate secure cheque ID
     */
    private String generateSecureChequeId() {
        StringBuilder id = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Add timestamp component
        id.append(Long.toHexString(System.currentTimeMillis()).toUpperCase());
        id.append("-");
        
        // Add random component
        for (int i = 0; i < 16; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return id.toString();
    }

    /**
     * Record a redemption in the aggregate audit counters. Called from redeemCheque().
     */
    private synchronized void recordRedemption(double amount) {
        totalRedeemed += amount;
        chequesRedeemedCount++;
    }

    /**
     * Aggregate cheque statistics for admin visibility (e.g. /eshop stats).
     */
    public synchronized ChequeStatistics getStatistics() {
        return new ChequeStatistics(totalIssued, totalRedeemed, chequesIssuedCount, chequesRedeemedCount);
    }

    /**
     * Snapshot of aggregate cheque issuance/redemption totals.
     */
    public static class ChequeStatistics {
        private final double totalIssued;
        private final double totalRedeemed;
        private final int chequesIssuedCount;
        private final int chequesRedeemedCount;

        public ChequeStatistics(double totalIssued, double totalRedeemed, int chequesIssuedCount, int chequesRedeemedCount) {
            this.totalIssued = totalIssued;
            this.totalRedeemed = totalRedeemed;
            this.chequesIssuedCount = chequesIssuedCount;
            this.chequesRedeemedCount = chequesRedeemedCount;
        }

        public double getTotalIssued() { return totalIssued; }
        public double getTotalRedeemed() { return totalRedeemed; }
        public double getOutstanding() { return totalIssued - totalRedeemed; }
        public int getChequesIssuedCount() { return chequesIssuedCount; }
        public int getChequesRedeemedCount() { return chequesRedeemedCount; }
    }
}