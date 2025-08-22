package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.data.ShopDataLoader;
import dev.turjo.easyshopgui.models.ShopSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Manager for handling GUI operations
 */
public class GuiManager {
    
    private final EasyShopGUI plugin;
    private final Map<String, ShopSection> sections;
    
    public GuiManager(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.sections = new HashMap<>();
        loadSections();
    }
    
    /**
     * Load sections from configuration
     */
    private void loadSections() {
        ShopDataLoader loader = new ShopDataLoader(plugin);
        Map<String, ShopSection> loadedSections = loader.loadSections();
        this.sections.putAll(loadedSections);
    }
    
    /**
     * Reload sections from configuration
     */
    public void reloadSections() {
        this.sections.clear();
        loadSections();
    }
    
    /**
     * Open shop GUI for player
     */
    public void openShop(Player player, String shopName) {
        ShopGui shopGui = new ShopGui(plugin, player, shopName);
        shopGui.open();
    }
    
    /**
     * Open section GUI for player
     */
    public void openSection(Player player, String sectionId) {
        ShopSection section = sections.get(sectionId);
        if (section != null) {
            SectionGui sectionGui = new SectionGui(plugin, player, section);
            sectionGui.open();
        } else {
            player.sendMessage("§cSection not found: " + sectionId);
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