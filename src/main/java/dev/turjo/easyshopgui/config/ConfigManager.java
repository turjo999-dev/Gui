package dev.turjo.easyshopgui.config;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all configuration files for the plugin
 */
public class ConfigManager {
    
    private final EasyShopGUI plugin;
    private final Map<String, FileConfiguration> configs;
    private final Map<String, File> configFiles;
    
    public ConfigManager(EasyShopGUI plugin) {
        this.plugin = plugin;
        this.configs = new HashMap<>();
        this.configFiles = new HashMap<>();
    }
    
    /**
     * Load all configuration files
     */
    public void loadConfigs() {
        // Create plugin directory if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Load main configuration files
        loadConfig("config.yml");
        loadConfig("messages.yml");
        loadConfig("shops.yml");
        loadConfig("items.yml");
        loadConfig("guis.yml");
        
        // Load language files
        loadLanguageFiles();
        
        Logger.info("Configuration files loaded successfully!");
    }
    
    /**
     * Load a specific configuration file
     */
    private void loadConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        
        // Create file if it doesn't exist
        if (!configFile.exists()) {
            createDefaultConfig(fileName, configFile);
        }
        
        // Load configuration
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(fileName, config);
        configFiles.put(fileName, configFile);
        
        Logger.debug("Loaded configuration: " + fileName);
    }
    
    /**
     * Create default configuration file from resources
     */
    private void createDefaultConfig(String fileName, File configFile) {
        try {
            InputStream inputStream = plugin.getResource(fileName);
            if (inputStream != null) {
                Files.copy(inputStream, configFile.toPath());
                Logger.info("Created default configuration: " + fileName);
            } else {
                // Create empty file if resource doesn't exist
                configFile.createNewFile();
                Logger.warn("Resource not found, created empty file: " + fileName);
            }
        } catch (IOException e) {
            Logger.error("Failed to create configuration file: " + fileName);
            e.printStackTrace();
        }
    }
    
    /**
     * Load language files
     */
    private void loadLanguageFiles() {
        File langDir = new File(plugin.getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        
        // Create default language files
        String[] languages = {"en_US", "es_ES", "fr_FR", "de_DE", "it_IT", "pt_BR", "ru_RU", "zh_CN", "ja_JP", "ko_KR"};
        
        for (String lang : languages) {
            String fileName = "messages_" + lang + ".yml";
            File langFile = new File(langDir, fileName);
            
            if (!langFile.exists()) {
                try {
                    InputStream inputStream = plugin.getResource("languages/" + fileName);
                    if (inputStream != null) {
                        Files.copy(inputStream, langFile.toPath());
                    }
                } catch (IOException e) {
                    Logger.debug("Language file not found: " + fileName);
                }
            }
        }
    }
    
    /**
     * Get configuration by name
     */
    public FileConfiguration getConfig(String configName) {
        return configs.get(configName);
    }
    
    /**
     * Get main plugin configuration
     */
    public FileConfiguration getMainConfig() {
        return getConfig("config.yml");
    }
    
    /**
     * Get messages configuration
     */
    public FileConfiguration getMessages() {
        return getConfig("messages.yml");
    }
    
    /**
     * Get shops configuration
     */
    public FileConfiguration getShops() {
        return getConfig("shops.yml");
    }
    
    /**
     * Get items configuration
     */
    public FileConfiguration getItems() {
        return getConfig("items.yml");
    }
    
    /**
     * Get GUIs configuration
     */
    public FileConfiguration getGuis() {
        return getConfig("guis.yml");
    }
    
    /**
     * Save configuration file
     */
    public void saveConfig(String configName) {
        try {
            FileConfiguration config = configs.get(configName);
            File configFile = configFiles.get(configName);
            
            if (config != null && configFile != null) {
                config.save(configFile);
                Logger.debug("Saved configuration: " + configName);
            }
        } catch (IOException e) {
            Logger.error("Failed to save configuration: " + configName);
            e.printStackTrace();
        }
    }
    
    /**
     * Save all configuration files
     */
    public void saveAllConfigs() {
        for (String configName : configs.keySet()) {
            saveConfig(configName);
        }
    }
    
    /**
     * Reload configuration file
     */
    public void reloadConfig(String configName) {
        File configFile = configFiles.get(configName);
        if (configFile != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            configs.put(configName, config);
            Logger.debug("Reloaded configuration: " + configName);
        }
    }
    
    /**
     * Reload all configuration files
     */
    public void reloadAllConfigs() {
        for (String configName : configs.keySet()) {
            reloadConfig(configName);
        }
        Logger.info("All configurations reloaded!");
    }
}