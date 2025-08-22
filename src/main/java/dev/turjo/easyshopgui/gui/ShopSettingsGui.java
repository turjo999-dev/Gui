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
 * Shop settings GUI
 */
public class ShopSettingsGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    
    public ShopSettingsGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&7&l⚙ &e&lSHOP SETTINGS"));
        
        // Add settings interface
        gui.setItem(22, new ItemBuilder(Material.COMPARATOR)
                .setName("&7&l⚙ &e&lPERSONAL SETTINGS")
                .setLore(Arrays.asList(
                        "&7▸ &fConfirm purchases: &aON",
                        "&7▸ &fSound effects: &aON",
                        "&7▸ &fNotifications: &aON",
                        "&7▸ &fPrice alerts: &cOFF",
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