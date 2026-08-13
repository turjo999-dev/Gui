package dev.turjo.easyshopgui.managers;

import dev.turjo.easyshopgui.EasyShopGUI;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player shop preferences (sound effects, purchase confirmations, chat
 * feedback on transactions). Backs the toggles shown in ShopSettingsGui.
 *
 * Preferences are session-scoped (reset on rejoin), matching how the rest of this
 * plugin's per-player state already works (GuiManager's navigation tracking, etc.) -
 * there is no persistence layer wired up for per-player data yet, only for shared
 * server-wide state (transactions/cheque stats, see DatabaseManager). Defaults are
 * chosen so a player who never opens Settings gets the same experience as before these
 * toggles existed.
 */
public class PlayerPreferencesManager {

    private final EasyShopGUI plugin;
    private final Map<UUID, Boolean> soundEffectsEnabled = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> purchaseConfirmEnabled = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> transactionMessagesEnabled = new ConcurrentHashMap<>();

    public PlayerPreferencesManager(EasyShopGUI plugin) {
        this.plugin = plugin;
    }

    public boolean isSoundEffectsEnabled(Player player) {
        return soundEffectsEnabled.getOrDefault(player.getUniqueId(), true);
    }

    public void toggleSoundEffects(Player player) {
        soundEffectsEnabled.put(player.getUniqueId(), !isSoundEffectsEnabled(player));
    }

    public boolean isPurchaseConfirmEnabled(Player player) {
        return purchaseConfirmEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public void togglePurchaseConfirm(Player player) {
        purchaseConfirmEnabled.put(player.getUniqueId(), !isPurchaseConfirmEnabled(player));
    }

    public boolean isTransactionMessagesEnabled(Player player) {
        return transactionMessagesEnabled.getOrDefault(player.getUniqueId(), true);
    }

    public void toggleTransactionMessages(Player player) {
        transactionMessagesEnabled.put(player.getUniqueId(), !isTransactionMessagesEnabled(player));
    }

    /**
     * Clear preferences for a player (called on quit to avoid unbounded map growth on
     * servers with high player churn).
     */
    public void clear(Player player) {
        UUID id = player.getUniqueId();
        soundEffectsEnabled.remove(id);
        purchaseConfirmEnabled.remove(id);
        transactionMessagesEnabled.remove(id);
    }
}
