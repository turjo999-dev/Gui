package dev.turjo.easyshopgui;

import dev.turjo.easyshopgui.commands.ShopCommand;
import dev.turjo.easyshopgui.config.ConfigManager;
import dev.turjo.easyshopgui.database.DatabaseManager;
import dev.turjo.easyshopgui.economy.EconomyManager;
import dev.turjo.easyshopgui.gui.GuiManager;
import dev.turjo.easyshopgui.hooks.HookManager;
import dev.turjo.easyshopgui.listeners.PlayerListener;
import dev.turjo.easyshopgui.listeners.GuiListener;
import dev.turjo.easyshopgui.managers.ShopManager;
import dev.turjo.easyshopgui.managers.TransactionManager;
import dev.turjo.easyshopgui.placeholders.EasyShopPlaceholderExpansion;
import dev.turjo.easyshopgui.schedulers.CronScheduler;
import dev.turjo.easyshopgui.utils.Logger;
import dev.turjo.easyshopgui.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * EasyShopGUI - Premium Minecraft Shop Plugin
 * 
 * @author Turjo
 * @version 1.0.0
 */
public final class EasyShopGUI extends JavaPlugin {

    private static EasyShopGUI instance;
    private long startTime;
    
    // Core Managers
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private ShopManager shopManager;
    private GuiManager guiManager;
    private TransactionManager transactionManager;
    private HookManager hookManager;
    private CronScheduler cronScheduler;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;
        startTime = System.currentTimeMillis();
        
        Logger.info("Starting EasyShopGUI v" + getDescription().getVersion());
        Logger.info("Developed by Turjo - Premium Minecraft Shop Plugin");
        
        // Initialize core components
        if (!initializePlugin()) {
            Logger.error("Failed to initialize plugin! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        Logger.info("EasyShopGUI has been successfully enabled!");
        Logger.info("Thank you for using EasyShopGUI - The Ultimate Shop Solution!");
    }

    @Override
    public void onDisable() {
        Logger.info("Disabling EasyShopGUI...");
        
        // Shutdown schedulers
        if (cronScheduler != null) {
            cronScheduler.shutdown();
        }
        
        // Close database connections
        if (databaseManager != null) {
            databaseManager.closeConnections();
        }
        
        Logger.info("EasyShopGUI has been disabled successfully!");
    }

    /**
     * Initialize all plugin components
     * @return true if successful, false otherwise
     */
    private boolean initializePlugin() {
        try {
            // Initialize configuration
            configManager = new ConfigManager(this);
            configManager.loadConfigs();
            
            // Initialize database
            databaseManager = new DatabaseManager(this);
            if (!databaseManager.initialize()) {
                Logger.error("Failed to initialize database!");
                return false;
            }
            
            // Initialize economy
            economyManager = new EconomyManager(this);
            if (!economyManager.setupEconomy()) {
                Logger.error("Failed to setup economy! Vault is required.");
                return false;
            }
            
            // Initialize managers
            shopManager = new ShopManager(this);
            guiManager = new GuiManager(this);
            transactionManager = new TransactionManager(this);
            hookManager = new HookManager(this);
            cronScheduler = new CronScheduler(this);
            updateChecker = new UpdateChecker(this);
            
            // Register commands
            registerCommands();
            
            // Register listeners
            registerListeners();
            
            // Setup hooks
            hookManager.setupHooks();
            
            // Register PlaceholderAPI expansion
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new EasyShopPlaceholderExpansion(this).register();
                Logger.info("PlaceholderAPI expansion registered!");
            }
            
            // Start schedulers
            cronScheduler.startSchedulers();
            
            // Check for updates
            updateChecker.checkForUpdates();
            
            // Enable debug logging
            Logger.setDebugMode(configManager.getMainConfig().getBoolean("plugin.debug", true));
            
            return true;
            
        } catch (Exception e) {
            Logger.error("Error during plugin initialization: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Register plugin commands
     */
    private void registerCommands() {
        try {
            ShopCommand shopCommand = new ShopCommand(this);
            
            if (getCommand("shop") != null) {
                getCommand("shop").setExecutor(shopCommand);
                getCommand("shop").setTabCompleter(shopCommand);
            } else {
                Logger.warn("Command 'shop' not found in plugin.yml");
            }
            
            if (getCommand("sell") != null) {
                getCommand("sell").setExecutor(shopCommand);
                getCommand("sell").setTabCompleter(shopCommand);
            } else {
                Logger.warn("Command 'sell' not found in plugin.yml");
            }
            
            if (getCommand("eshop") != null) {
                getCommand("eshop").setExecutor(shopCommand);
                getCommand("eshop").setTabCompleter(shopCommand);
            } else {
                Logger.warn("Command 'eshop' not found in plugin.yml");
            }
            
            Logger.info("Commands registered successfully!");
        } catch (Exception e) {
            Logger.error("Error registering commands: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Register event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
    }

    // Getters for managers
    public static EasyShopGUI getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public CronScheduler getCronScheduler() {
        return cronScheduler;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    
    public long getStartTime() {
        return startTime;
    }
}