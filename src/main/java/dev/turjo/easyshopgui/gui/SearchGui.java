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
 * Search GUI for finding items
 */
public class SearchGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    
    public SearchGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&b&l🔍 &e&lSEARCH ITEMS"));
        
        // Add search interface
        gui.setItem(22, new ItemBuilder(Material.COMPASS)
                .setName("&b&l🔍 &e&lSEARCH FUNCTION")
                .setLore(Arrays.asList(
                        "&7▸ &fType in chat to search",
                        "&7▸ &fSearch by item name",
                        "&7▸ &fFilter by category",
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