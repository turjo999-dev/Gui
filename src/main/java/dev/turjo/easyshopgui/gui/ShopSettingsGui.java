package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.PricingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

/**
 * Shop settings GUI - real, working per-player preference toggles.
 *
 * Previously this showed one decorative item with hardcoded "ON"/"OFF" text labeled
 * "Coming Soon!" that did nothing when clicked. Now backs three genuine toggles
 * (PlayerPreferencesManager) that actually change GuiListener's behaviour, plus an
 * admin-only debug toggle.
 */
public class ShopSettingsGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    
    public ShopSettingsGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 45,
                MessageUtils.colorize("&7&l⚙ &e&lSHOP SETTINGS"));

        fillBackground(gui);
        addPreferenceToggles(gui);
        addAccountInfo(gui);
        addNavigation(gui);
        
        player.openInventory(gui);
    }

    private void fillBackground(Inventory gui) {
        var background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
        var border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }
    }
    
    private void addPreferenceToggles(Inventory gui) {
        boolean soundOn = plugin.getPlayerPreferencesManager().isSoundEffectsEnabled(player);
        gui.setItem(20, new ItemBuilder(soundOn ? Material.NOTE_BLOCK : Material.BARRIER)
                .setName("&e&l🔊 &e&lSOUND EFFECTS")
                .setLore(Arrays.asList(
                        "&7▸ &fClick sounds and purchase chimes",
                        "&7▸ &fCurrently: " + (soundOn ? "&aON" : "&cOFF"),
                        "",
                        "&a&l➤ &aClick to toggle!"
                ))
                .addGlow(soundOn)
                .build());

        boolean confirmOn = plugin.getPlayerPreferencesManager().isPurchaseConfirmEnabled(player);
        gui.setItem(22, new ItemBuilder(confirmOn ? Material.LIME_DYE : Material.GRAY_DYE)
                .setName("&e&l✅ &e&lCONFIRM LARGE PURCHASES")
                .setLore(Arrays.asList(
                        "&7▸ &fRequire a second click for stack (64x) buys",
                        "&7▸ &fCurrently: " + (confirmOn ? "&aON" : "&cOFF"),
                        "",
                        "&a&l➤ &aClick to toggle!"
                ))
                .addGlow(confirmOn)
                .build());

        boolean messagesOn = plugin.getPlayerPreferencesManager().isTransactionMessagesEnabled(player);
        gui.setItem(24, new ItemBuilder(messagesOn ? Material.WRITABLE_BOOK : Material.BARRIER)
                .setName("&e&l💬 &e&lTRANSACTION MESSAGES")
                .setLore(Arrays.asList(
                        "&7▸ &fChat confirmation after buy/sell",
                        "&7▸ &fCurrently: " + (messagesOn ? "&aON" : "&cOFF"),
                        "",
                        "&a&l➤ &aClick to toggle!"
                ))
                .addGlow(messagesOn)
                .build());

        if (player.hasPermission("easyshopgui.admin")) {
            boolean debugOn = plugin.getConfigManager().getMainConfig().getBoolean("plugin.debug", false);
            gui.setItem(31, new ItemBuilder(debugOn ? Material.REDSTONE_TORCH : Material.LEVER)
                    .setName("&c&l🛠 &e&lDEBUG MODE &7(Admin)")
                    .setLore(Arrays.asList(
                            "&7▸ &fVerbose console logging",
                            "&7▸ &fCurrently: " + (debugOn ? "&aON" : "&cOFF"),
                            "",
                            "&a&l➤ &aClick to toggle!"
                    ))
                    .addGlow(debugOn)
                    .build());
        }
    }

    private void addAccountInfo(Inventory gui) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        gui.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&6&l👤 &e&l" + player.getName().toUpperCase())
                .setLore(Arrays.asList(
                        "&7▸ &fBalance: &a$" + String.format("%.2f", balance),
                        "&7▸ &fDiscount Tier: &a" + PricingUtil.getDiscountPercent(player) + "%",
                        "&7▸ &fSell Multiplier: &a" + PricingUtil.getSellMultiplier(player) + "x",
                        "&7▸ &fTransactions: &e" + plugin.getTransactionManager().getTransactionCount(player)
                ))
                .setSkullOwner(player)
                .build());
    }
    
    private void addNavigation(Inventory gui) {
        gui.setItem(40, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
    }
}
