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
 * Quick sell GUI
 */
public class QuickSellGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    
    public QuickSellGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&c&l💸 &e&lQUICK SELL"));
        
        // Add quick sell interface
        gui.setItem(22, new ItemBuilder(Material.HOPPER)
                .setName("&c&l💸 &e&lSELL INVENTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fSell all sellable items",
                        "&7▸ &fBulk selling available",
                        "&7▸ &fInstant money transfer",
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