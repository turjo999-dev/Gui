package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.data.ShopDataLoader;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for handling GUI operations
 */
public class GuiManager {
    
    private final EasyShopGUI plugin;
    private Map<String, ShopSection> sections;
    
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
     * Open section GUI for player
     */
    public void openSection(Player player, String sectionId) {
        ShopSection section = sections.get(sectionId);
        Logger.debug("Attempting to open section: " + sectionId + " for player: " + player.getName());
        
        if (section != null) {
            Logger.debug("Section found with " + section.getItems().size() + " items");
            SectionGui sectionGui = new SectionGui(plugin, player, section);
            sectionGui.open();
        } else {
            player.sendMessage("§cSection not found: " + sectionId + ". Available sections: " + String.join(", ", sections.keySet()));
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
                ItemDetailGui itemGui = new ItemDetailGui(plugin, player, shopItem);
                itemGui.open();
            } else {
                player.sendMessage("§cItem not found: " + itemId);
            }
        } else {
            player.sendMessage("§cSection not found: " + sectionId);
        }
    }
    
    /**
     * Get all sections
     */
    public Map<String, ShopSection> getSections() {
        return sections;
    }
}