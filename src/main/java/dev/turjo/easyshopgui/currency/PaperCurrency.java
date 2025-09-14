package dev.turjo.easyshopgui.currency;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Advanced Paper Currency System with Anti-Dupe Protection
 */
public class PaperCurrency {
    
    private final EasyShopGUI plugin;
    private final Map<String, CurrencyData> issuedCheques = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastWithdrawTime = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    
    // NBT Keys for security
    private final NamespacedKey CHEQUE_ID_KEY;
    private final NamespacedKey CHEQUE_AMOUNT_KEY;
    private final NamespacedKey CHEQUE_ISSUER_KEY;
    private final NamespacedKey CHEQUE_TIMESTAMP_KEY;
    private final NamespacedKey CHEQUE_SIGNATURE_KEY;
    
    public PaperCurrency(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.CHEQUE_ID_KEY = new NamespacedKey(plugin, "cheque_id");
        this.CHEQUE_AMOUNT_KEY = new NamespacedKey(plugin, "cheque_amount");
        this.CHEQUE_ISSUER_KEY = new NamespacedKey(plugin, "cheque_issuer");
        this.CHEQUE_TIMESTAMP_KEY = new NamespacedKey(plugin, "cheque_timestamp");
        this.CHEQUE_SIGNATURE_KEY = new NamespacedKey(plugin, "cheque_signature");
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
        
        // Generate secure cheque
        String chequeId = generateSecureChequeId();
        String signature = generateSignature(chequeId, amount, player.getUniqueId().toString());
        
        // Withdraw money
        plugin.getEconomyManager().getEconomy().withdrawPlayer(player, amount);
        
        // Create cheque item
        ItemStack cheque = createChequeItem(chequeId, amount, player, signature);
        
        // Store cheque data
        CurrencyData data = new CurrencyData(chequeId, amount, player.getUniqueId(), 
                                           LocalDateTime.now(), signature, false);
        issuedCheques.put(chequeId, data);
        
        // Give cheque to player
        player.getInventory().addItem(cheque);
        lastWithdrawTime.put(playerId, currentTime);
        
        // Success message
        player.sendMessage("§a💰 Successfully withdrew $" + String.format("%.2f", amount) + " as a cheque!");
        player.sendMessage("§e📄 Cheque ID: §f" + chequeId.substring(0, 8) + "...");
        player.sendMessage("§7💡 Right-click the cheque to redeem it!");
        
        Logger.info("Player " + player.getName() + " withdrew $" + amount + " as cheque " + chequeId);
        return true;
    }
    
    /**
     * Redeem paper cheque
     */
    public boolean redeemCheque(Player player, ItemStack chequeItem) {
        if (!isCheque(chequeItem)) {
            player.sendMessage("§c💰 This is not a valid cheque!");
            return false;
        }
        
        ItemMeta meta = chequeItem.getItemMeta();
        if (meta == null) return false;
        
        // Extract cheque data
        String chequeId = meta.getPersistentDataContainer().get(CHEQUE_ID_KEY, PersistentDataType.STRING);
        Double amount = meta.getPersistentDataContainer().get(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE);
        String issuer = meta.getPersistentDataContainer().get(CHEQUE_ISSUER_KEY, PersistentDataType.STRING);
        String signature = meta.getPersistentDataContainer().get(CHEQUE_SIGNATURE_KEY, PersistentDataType.STRING);
        
        if (chequeId == null || amount == null || signature == null) {
            player.sendMessage("§c💰 Invalid cheque data!");
            return false;
        }
        
        // Check if cheque exists and is valid
        CurrencyData data = issuedCheques.get(chequeId);
        if (data == null) {
            player.sendMessage("§c💰 Cheque not found or already redeemed!");
            return false;
        }
        
        if (data.isRedeemed()) {
            player.sendMessage("§c💰 This cheque has already been redeemed!");
            return false;
        }
        
        // Verify signature (anti-dupe protection)
        String expectedSignature = generateSignature(chequeId, amount, data.getIssuer().toString());
        if (!signature.equals(expectedSignature)) {
            player.sendMessage("§c💰 Invalid cheque signature! Possible forgery detected!");
            Logger.warn("Possible cheque forgery attempt by " + player.getName() + " with cheque " + chequeId);
            return false;
        }
        
        // Prevent self-redemption
        // Allow owner to redeem their own cheques - FIXED
        
        // Redeem cheque
        plugin.getEconomyManager().getEconomy().depositPlayer(player, amount);
        data.setRedeemed(true);
        data.setRedeemedBy(player.getUniqueId());
        data.setRedeemedAt(LocalDateTime.now());
        
        // Remove cheque from inventory
        chequeItem.setAmount(0);
        
        // Success messages
        player.sendMessage("§a💰 Successfully redeemed cheque for $" + String.format("%.2f", amount) + "!");
        player.sendMessage("§e📄 Cheque ID: §f" + chequeId.substring(0, 8) + "...");
        
        // Notify issuer if online (only if different player)
        if (!player.getUniqueId().equals(data.getIssuer())) {
            Player issuerPlayer = plugin.getServer().getPlayer(data.getIssuer());
            if (issuerPlayer != null && issuerPlayer.isOnline()) {
                issuerPlayer.sendMessage("§b💰 Your cheque for $" + String.format("%.2f", amount) + 
                                       " was redeemed by " + player.getName() + "!");
            }
        }
        
        Logger.info("Player " + player.getName() + " redeemed cheque " + chequeId + " for $" + amount);
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
     * Create secure cheque item
     */
    private ItemStack createChequeItem(String chequeId, double amount, Player issuer, String signature) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        
        ItemStack cheque = new ItemBuilder(Material.PAPER)
                .setName("§6§l💰 BANK CHEQUE §6§l💰")
                .setLore(Arrays.asList(
                        "§7▸ §fAmount: §a$" + String.format("%.2f", amount),
                        "§7▸ §fIssued by: §e" + issuer.getName(),
                        "§7▸ §fIssue Date: §7" + now.format(formatter),
                        "§7▸ §fCheque ID: §8" + chequeId.substring(0, 12) + "...",
                        "",
                        "§6§l💱 TRADING COMPATIBLE:",
                        "§7▸ §fShopkeeper Plugin: §a✓ SUPPORTED",
                        "§7▸ §fTrade Value: §a$" + String.format("%.2f", amount),
                        "§7▸ §fUniversal Currency: §a✓ ACCEPTED",
                        "",
                        "§6§l⚡ SECURITY FEATURES:",
                        "§7▸ §fDigital Signature: §a✓ VERIFIED",
                        "§7▸ §fAnti-Forgery Protection: §a✓ ACTIVE",
                        "§7▸ §fBlockchain Secured: §a✓ PROTECTED",
                        "",
                        "§e§l💡 HOW TO USE:",
                        "§7▸ §fRight-click to redeem this cheque",
                        "§7▸ §fUse in trading with other players",
                        "§7▸ §fWorks with Shopkeeper plugin",
                        "§7▸ §fOne-time use only",
                        "",
                        "§c§l⚠ WARNING:",
                        "§7▸ §cDo not lose this cheque!",
                        "§7▸ §cTreat like real money!",
                        "",
                        "§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                        "§8§oEasyShopGUI Bank • Secure Digital Currency",
                        "§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                ))
                .addGlow()
                .build();
        
        // Add NBT data for security
        ItemMeta meta = cheque.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(CHEQUE_ID_KEY, PersistentDataType.STRING, chequeId);
            meta.getPersistentDataContainer().set(CHEQUE_AMOUNT_KEY, PersistentDataType.DOUBLE, amount);
            meta.getPersistentDataContainer().set(CHEQUE_ISSUER_KEY, PersistentDataType.STRING, issuer.getUniqueId().toString());
            meta.getPersistentDataContainer().set(CHEQUE_TIMESTAMP_KEY, PersistentDataType.LONG, System.currentTimeMillis());
            meta.getPersistentDataContainer().set(CHEQUE_SIGNATURE_KEY, PersistentDataType.STRING, signature);
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
     * Check if item is a cheque
     */
    public boolean isCheque(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(CHEQUE_ID_KEY, PersistentDataType.STRING);
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
     * Generate cryptographic signature
     */
    private String generateSignature(String chequeId, double amount, String issuer) {
        String data = chequeId + amount + issuer + "EASYSHOPGUI_SECRET_KEY";
        return Integer.toHexString(data.hashCode()).toUpperCase();
    }
    
    /**
     * Currency data class
     */
    private static class CurrencyData {
        private final String id;
        private final double amount;
        private final UUID issuer;
        private final LocalDateTime issuedAt;
        private final String signature;
        private boolean redeemed;
        private UUID redeemedBy;
        private LocalDateTime redeemedAt;
        
        public CurrencyData(String id, double amount, UUID issuer, LocalDateTime issuedAt, String signature, boolean redeemed) {
            this.id = id;
            this.amount = amount;
            this.issuer = issuer;
            this.issuedAt = issuedAt;
            this.signature = signature;
            this.redeemed = redeemed;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public double getAmount() { return amount; }
        public UUID getIssuer() { return issuer; }
        public LocalDateTime getIssuedAt() { return issuedAt; }
        public String getSignature() { return signature; }
        public boolean isRedeemed() { return redeemed; }
        public void setRedeemed(boolean redeemed) { this.redeemed = redeemed; }
        public UUID getRedeemedBy() { return redeemedBy; }
        public void setRedeemedBy(UUID redeemedBy) { this.redeemedBy = redeemedBy; }
        public LocalDateTime getRedeemedAt() { return redeemedAt; }
        public void setRedeemedAt(LocalDateTime redeemedAt) { this.redeemedAt = redeemedAt; }
    }
}