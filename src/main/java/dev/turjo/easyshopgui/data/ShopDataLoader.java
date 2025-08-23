package dev.turjo.easyshopgui.data;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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
        
        // Load sections from separate files
        File sectionsDir = new File(plugin.getDataFolder(), "sections");
        if (!sectionsDir.exists()) {
            Logger.error("Sections directory not found! Creating default sections...");
            return ShopData.createDefaultSections();
        }
        
        File[] sectionFiles = sectionsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (sectionFiles == null || sectionFiles.length == 0) {
            Logger.error("No section files found! Creating default sections...");
            return ShopData.createDefaultSections();
        }
        
        for (File sectionFile : sectionFiles) {
            try {
                FileConfiguration sectionConfig = YamlConfiguration.loadConfiguration(sectionFile);
                ShopSection section = loadSectionFromFile(sectionConfig);
                if (section != null) {
                    sections.put(section.getId(), section);
                    Logger.debug("Loaded section: " + section.getId() + " with " + section.getItems().size() + " items");
                }
            } catch (Exception e) {
                Logger.error("Failed to load section file: " + sectionFile.getName() + " - " + e.getMessage());
            }
        }
        
        Logger.info("Loaded " + sections.size() + " shop sections from configuration");
        return sections;
    }
    
    /**
     * Load section from individual file
     */
    private ShopSection loadSectionFromFile(FileConfiguration config) {
        ConfigurationSection sectionConfig = config.getConfigurationSection("section");
        if (sectionConfig == null) {
            Logger.error("Invalid section file format - missing 'section' configuration");
            return null;
        }
        
        // Check if section is enabled
        if (!sectionConfig.getBoolean("enabled", true)) {
            Logger.debug("Section is disabled, skipping...");
            return null;
        }
        
        // Create section
        String id = sectionConfig.getString("id", "unknown");
        String name = sectionConfig.getString("name", id);
        String displayName = sectionConfig.getString("display-name", name);
        String iconName = sectionConfig.getString("icon", "STONE");
        
        Material icon;
        try {
            icon = Material.valueOf(iconName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Logger.warn("Invalid material for section " + id + ": " + iconName);
            icon = Material.STONE;
        }
        
        ShopSection section = new ShopSection(id, name, displayName, icon);
        section.setDescription(sectionConfig.getString("description", ""));
        section.setPermission(sectionConfig.getString("permission", ""));
        
        // Load items
        ConfigurationSection itemsConfig = config.getConfigurationSection("items");
        if (itemsConfig != null) {
            loadItemsForSection(section, itemsConfig);
        }
        
        return section;
    }
    
    /**
     * Load items for a specific section
     */
    private void loadItemsForSection(ShopSection section, ConfigurationSection itemsConfig) {
        for (String itemId : itemsConfig.getKeys(false)) {
            ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(itemId);
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