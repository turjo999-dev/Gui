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
import java.io.InputStream;
import java.nio.file.Files;
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
        
        // Create sections directory if it doesn't exist
        File sectionsDir = new File(plugin.getDataFolder(), "sections");
        if (!sectionsDir.exists()) {
            sectionsDir.mkdirs();
            Logger.info("Created sections directory");
        }
        
        // Create default section files if they don't exist
        createDefaultSectionFiles(sectionsDir);
        
        // Load sections from files
        File[] sectionFiles = sectionsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (sectionFiles == null || sectionFiles.length == 0) {
            Logger.error("No section files found! Creating default sections...");
            return ShopData.createDefaultSections();
        }
        
        for (File sectionFile : sectionFiles) {
            try {
                FileConfiguration sectionConfig = YamlConfiguration.loadConfiguration(sectionFile);
                ShopSection section = loadSectionFromFile(sectionConfig, sectionFile.getName());
                if (section != null) {
                    sections.put(section.getId(), section);
                    Logger.info("Loaded section: " + section.getId() + " with " + section.getItems().size() + " items");
                }
            } catch (Exception e) {
                Logger.error("Failed to load section file: " + sectionFile.getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        Logger.info("Loaded " + sections.size() + " shop sections from configuration");
        return sections;
    }
    
    /**
     * Create default section files if they don't exist
     */
    private void createDefaultSectionFiles(File sectionsDir) {
        String[] sectionFiles = {
            "blocks.yml", "ores.yml", "food.yml", "redstone.yml", 
            "farming.yml", "decoration.yml", "potions.yml"
        };
        
        for (String fileName : sectionFiles) {
            File sectionFile = new File(sectionsDir, fileName);
            if (!sectionFile.exists()) {
                try {
                    InputStream inputStream = plugin.getResource("sections/" + fileName);
                    if (inputStream != null) {
                        Files.copy(inputStream, sectionFile.toPath());
                        Logger.info("Created default section file: " + fileName);
                    } else {
                        Logger.warn("Resource not found for section: " + fileName);
                    }
                } catch (Exception e) {
                    Logger.error("Failed to create section file: " + fileName + " - " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Load section from individual file
     */
    private ShopSection loadSectionFromFile(FileConfiguration config, String fileName) {
        ConfigurationSection sectionConfig = config.getConfigurationSection("section");
        if (sectionConfig == null) {
            Logger.error("Invalid section file format - missing 'section' configuration in " + fileName);
            return null;
        }
        
        // Check if section is enabled
        if (!sectionConfig.getBoolean("enabled", true)) {
            Logger.debug("Section is disabled, skipping: " + fileName);
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
            Logger.warn("Invalid material for section " + id + ": " + iconName + ", using STONE");
            icon = Material.STONE;
        }
        
        ShopSection section = new ShopSection(id, name, displayName, icon);
        section.setDescription(sectionConfig.getString("description", ""));
        section.setPermission(sectionConfig.getString("permission", ""));
        
        // Load items
        ConfigurationSection itemsConfig = config.getConfigurationSection("items");
        if (itemsConfig != null) {
            int itemCount = loadItemsForSection(section, itemsConfig);
            Logger.debug("Loaded " + itemCount + " items for section " + id);
        } else {
            Logger.warn("No items section found in " + fileName);
        }
        
        return section;
    }
    
    /**
     * Load items for a specific section
     */
    private int loadItemsForSection(ShopSection section, ConfigurationSection itemsConfig) {
        int itemCount = 0;
        
        for (String itemId : itemsConfig.getKeys(false)) {
            ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(itemId);
            if (itemConfig == null) {
                Logger.warn("Invalid item configuration for " + itemId + " in section " + section.getId() + ", skipping");
                continue;
            }
            
            try {
                // Get material
                String materialName = itemConfig.getString("material", "STONE");
                Material material;
                try {
                    material = Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Logger.warn("Invalid material for item " + itemId + ": " + materialName + ", skipping");
                    continue;
                }
                
                // Get prices
                double buyPrice = itemConfig.getDouble("buy-price", 1.0);
                double sellPrice = itemConfig.getDouble("sell-price", 0.5);
                
                // Get display name
                String displayName = itemConfig.getString("name", itemId);
                
                // Create item
                ShopItem item = new ShopItem(itemId, displayName, material, buyPrice, sellPrice);
                
                // Set additional properties
                item.setDescription(itemConfig.getString("description", ""));
                item.setStock(itemConfig.getInt("stock", -1));
                item.setDemand(itemConfig.getString("demand", "medium"));
                item.setPermission(itemConfig.getString("permission", ""));
                
                // Add to section
                section.addItem(item);
                itemCount++;
                
            } catch (Exception e) {
                Logger.error("Failed to load item " + itemId + " in section " + section.getId() + ": " + e.getMessage());
            }
        }
        
        return itemCount;
    }
}