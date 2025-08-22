package dev.turjo.easyshopgui.data;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads shop data from configuration files
 */
public class ShopDataLoader {
    
    private final EasyShopGUI plugin;
    
    public ShopDataLoader(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load all shop sections from configuration
     */
    public Map<String, ShopSection> loadSections() {
        Map<String, ShopSection> sections = new HashMap<>();
        
        FileConfiguration sectionsConfig = plugin.getConfigManager().getSections();
        FileConfiguration itemsConfig = plugin.getConfigManager().getItems();
        
        if (sectionsConfig == null || itemsConfig == null) {
            Logger.error("Configuration files not found! Using default data.");
            return ShopData.createDefaultSections();
        }
        
        ConfigurationSection sectionsSection = sectionsConfig.getConfigurationSection("sections");
        if (sectionsSection == null) {
            Logger.error("No sections found in configuration!");
            return ShopData.createDefaultSections();
        }
        
        for (String sectionId : sectionsSection.getKeys(false)) {
            ConfigurationSection sectionConfig = sectionsSection.getConfigurationSection(sectionId);
            if (sectionConfig == null) continue;
            
            // Check if section is enabled
            if (!sectionConfig.getBoolean("enabled", true)) {
                Logger.debug("Section " + sectionId + " is disabled, skipping...");
                continue;
            }
            
            // Create section
            String name = sectionConfig.getString("name", sectionId);
            String displayName = sectionConfig.getString("display-name", name);
            String iconName = sectionConfig.getString("icon", "STONE");
            
            Material icon;
            try {
                icon = Material.valueOf(iconName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Logger.warn("Invalid material for section " + sectionId + ": " + iconName);
                icon = Material.STONE;
            }
            
            ShopSection section = new ShopSection(sectionId, name, displayName, icon);
            section.setDescription(sectionConfig.getString("description", ""));
            section.setPermission(sectionConfig.getString("permission", ""));
            
            // Load items for this section
            loadItemsForSection(section, itemsConfig);
            
            sections.put(sectionId, section);
            Logger.debug("Loaded section: " + sectionId + " with " + section.getItems().size() + " items");
        }
        
        Logger.info("Loaded " + sections.size() + " shop sections from configuration");
        return sections;
    }
    
    /**
     * Load items for a specific section
     */
    private void loadItemsForSection(ShopSection section, FileConfiguration itemsConfig) {
        ConfigurationSection sectionItems = itemsConfig.getConfigurationSection(section.getId());
        if (sectionItems == null) {
            Logger.warn("No items found for section: " + section.getId());
            return;
        }
        
        for (String itemId : sectionItems.getKeys(false)) {
            ConfigurationSection itemConfig = sectionItems.getConfigurationSection(itemId);
            if (itemConfig == null) continue;
            
            try {
                // Get material
                String materialName = itemConfig.getString("material", "STONE");
                Material material = Material.valueOf(materialName.toUpperCase());
                
                // Get prices
                double buyPrice = itemConfig.getDouble("buy-price", 1.0);
                double sellPrice = itemConfig.getDouble("sell-price", 0.5);
                
                // Create item
                ShopItem item = new ShopItem(itemId, itemConfig.getString("name", itemId), material, buyPrice, sellPrice);
                
                // Set additional properties
                item.setDescription(itemConfig.getString("description", ""));
                item.setStock(itemConfig.getInt("stock", -1));
                item.setDemand(itemConfig.getString("demand", "medium"));
                item.setPermission(itemConfig.getString("permission", ""));
                
                // Add to section
                section.addItem(item);
                
            } catch (Exception e) {
                Logger.error("Failed to load item " + itemId + " in section " + section.getId() + ": " + e.getMessage());
            }
        }
    }
}