package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

/**
 * Transaction history GUI
 */
public class TransactionHistoryGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    
    public TransactionHistoryGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&3&l📋 &e&lTRANSACTION HISTORY"));
        
        // Add transaction history interface
        gui.setItem(22, new ItemBuilder(Material.BOOK)
                .setName("&3&l📋 &e&lYOUR TRANSACTIONS")
                .setLore(Arrays.asList(
                        "&7▸ &fView purchase history",
                        "&7▸ &fTrack spending patterns",
                        "&7▸ &fExport transaction data",
                        "",
                        "&e&lComing Soon!"
                ))
                .addGlow()
                .build());
        
        // Back button
        gui.setItem(45, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
        
        player.openInventory(gui);
    }
}