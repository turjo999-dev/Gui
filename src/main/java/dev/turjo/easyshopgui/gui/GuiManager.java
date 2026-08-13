package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.data.ShopDataLoader;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for all GUI operations.
 *
 * This is the single source of truth for "which GUI session is a player currently in".
 * Every GUI must be opened through this class so that exactly one tracking map exists
 * per GUI type. Previously, some entry points (e.g. the /sellgui command) populated a
 * different map than the one GuiListener actually read from, which meant clicks and
 * drags inside GUIs opened that way were never recognized as valid - selling silently
 * failed and drag-protection on fixed buttons never activated. Routing every open()
 * through here removes that entire class of bug at the source.
 */
public class GuiManager {

    private final EasyShopGUI plugin;
    // volatile: reloadSections() reassigns this field on the main thread, while
    // AIMarketplace's async price/stock updater (a separate background thread) reads it
    // via getSections() and iterates the section list concurrently. volatile ensures the
    // background thread always observes a fully-published, up-to-date map reference
    // rather than a stale one cached in a CPU register/thread-local view.
    private volatile Map<String, ShopSection> sections;

    // Active GUI session tracking - one map per GUI type, all owned here.
    private final Map<Player, QuickSellGui> activeQuickSellGuis = new ConcurrentHashMap<>();
    private final Map<Player, SearchGui> activeSearchGuis = new ConcurrentHashMap<>();
    private final Map<Player, TransactionHistoryGui> activeTransactionGuis = new ConcurrentHashMap<>();
    private final Map<Player, SectionGui> activeSectionGuis = new ConcurrentHashMap<>();
    private final Map<Player, ItemDetailGui> activeItemDetailGuis = new ConcurrentHashMap<>();

    // Per-player navigation state (which section/page a player is currently browsing)
    private final Map<Player, String> playerCurrentSection = new ConcurrentHashMap<>();
    private final Map<Player, Integer> playerCurrentPage = new ConcurrentHashMap<>();

    public GuiManager(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.sections = new HashMap<>();
        Logger.debug("GuiManager initialized");
        loadSections();
    }

    /**
     * Load sections from configuration
     */
    private void loadSections() {
        ShopDataLoader loader = new ShopDataLoader(plugin);
        this.sections = loader.loadSections();
        Logger.info("GuiManager loaded " + sections.size() + " sections");
        sections.keySet().forEach(key -> Logger.debug("Available section: " + key));
    }

    /**
     * Reload sections from configuration
     */
    public void reloadSections() {
        loadSections();
        Logger.info("Sections reloaded");
    }

    /**
     * Open shop GUI for player
     */
    public void openShop(Player player, String shopName) {
        ShopGui shopGui = new ShopGui(plugin, player, shopName);
        Logger.debug("Opening shop for player: " + player.getName());
        shopGui.open();
    }

    /**
     * Open section GUI for player (first page)
     */
    public void openSection(Player player, String sectionId) {
        openSection(player, sectionId, 0);
    }

    /**
     * Open section GUI for player at a specific page. This is the single entry point
     * for opening a section - every caller (main shop clicks, pagination, refreshes)
     * goes through here so navigation state and the tracking map can never drift apart.
     */
    public void openSection(Player player, String sectionId, int page) {
        ShopSection section = sections.get(sectionId);
        Logger.debug("Attempting to open section: " + sectionId + " for player: " + player.getName());

        if (section != null) {
            Logger.debug("Section found with " + section.getItems().size() + " items");
            playerCurrentSection.put(player, sectionId);
            playerCurrentPage.put(player, page);

            SectionGui sectionGui = new SectionGui(plugin, player, section);
            sectionGui.setCurrentPage(page);
            activeSectionGuis.put(player, sectionGui);
            sectionGui.open();
        } else {
            player.sendMessage("§cSection not found: " + sectionId + ". Available sections: " + String.join(", ", sections.keySet()));
        }
    }

    /**
     * Re-open whatever section/page the player was last browsing (used after a
     * buy/sell transaction to refresh prices and balance without losing their place).
     */
    public void refreshCurrentSection(Player player) {
        String sectionId = playerCurrentSection.get(player);
        if (sectionId != null) {
            int page = playerCurrentPage.getOrDefault(player, 0);
            openSection(player, sectionId, page);
        }
    }

    /**
     * Open item detail GUI
     */
    public void openItemDetail(Player player, String sectionId, String itemId) {
        ShopSection section = sections.get(sectionId);
        if (section != null) {
            var shopItem = section.getItem(itemId);
            if (shopItem != null) {
                ItemDetailGui itemGui = new ItemDetailGui(plugin, player, shopItem, sectionId);
                activeItemDetailGuis.put(player, itemGui);
                itemGui.open();
            } else {
                player.sendMessage("§cItem not found: " + itemId);
            }
        } else {
            player.sendMessage("§cSection not found: " + sectionId);
        }
    }

    /**
     * Open the Quick Sell GUI for a player. Used by both the in-shop button and the
     * /sell (/sellgui, /quicksell) command so there is exactly one code path and one
     * tracking map for this GUI, no matter how it was opened.
     */
    public void openQuickSell(Player player) {
        QuickSellGui quickSellGui = new QuickSellGui(plugin, player);
        activeQuickSellGuis.put(player, quickSellGui);
        quickSellGui.open();
    }

    /**
     * Open the Search GUI (empty search box) for a player.
     */
    public void openSearch(Player player) {
        SearchGui searchGui = new SearchGui(plugin, player);
        activeSearchGuis.put(player, searchGui);
        searchGui.open();
    }

    /**
     * Open the Transaction History GUI for a player.
     */
    public void openTransactionHistory(Player player) {
        TransactionHistoryGui historyGui = new TransactionHistoryGui(plugin, player);
        activeTransactionGuis.put(player, historyGui);
        historyGui.open();
    }

    /**
     * Open the Shop Settings GUI for a player.
     */
    public void openSettings(Player player) {
        new ShopSettingsGui(plugin, player).open();
    }

    /**
     * Open the AI Marketplace overview GUI for a player.
     */
    public void openMarketplace(Player player) {
        new MarketplaceGui(plugin, player).open();
    }

    /**
     * Clear all tracked GUI sessions for a player. Called on inventory close / logout
     * so stale references don't leak.
     */
    public void clearSessions(Player player) {
        activeQuickSellGuis.remove(player);
        activeSearchGuis.remove(player);
        activeTransactionGuis.remove(player);
        activeSectionGuis.remove(player);
        activeItemDetailGuis.remove(player);
    }

    /**
     * Get all sections
     */
    public Map<String, ShopSection> getSections() {
        return sections;
    }

    /**
     * Get active GUI tracking maps
     */
    public Map<Player, QuickSellGui> getActiveQuickSellGuis() {
        return activeQuickSellGuis;
    }

    public Map<Player, SearchGui> getActiveSearchGuis() {
        return activeSearchGuis;
    }

    public Map<Player, TransactionHistoryGui> getActiveTransactionGuis() {
        return activeTransactionGuis;
    }

    public Map<Player, SectionGui> getActiveSectionGuis() {
        return activeSectionGuis;
    }

    public Map<Player, ItemDetailGui> getActiveItemDetailGuis() {
        return activeItemDetailGuis;
    }

    public String getPlayerCurrentSection(Player player) {
        return playerCurrentSection.get(player);
    }

    public int getPlayerCurrentPage(Player player) {
        return playerCurrentPage.getOrDefault(player, 0);
    }

    public void setPlayerCurrentPage(Player player, int page) {
        playerCurrentPage.put(player, page);
    }
}
