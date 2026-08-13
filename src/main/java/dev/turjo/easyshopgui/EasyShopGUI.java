package dev.turjo.easyshopgui;

import dev.turjo.easyshopgui.commands.ShopCommand;
import dev.turjo.easyshopgui.config.ConfigManager;
import dev.turjo.easyshopgui.database.DatabaseManager;
import dev.turjo.easyshopgui.economy.EconomyManager;
import dev.turjo.easyshopgui.gui.GuiManager;
import dev.turjo.easyshopgui.hooks.HookManager;
import dev.turjo.easyshopgui.listeners.PlayerListener;
import dev.turjo.easyshopgui.listeners.GuiListener;
import dev.turjo.easyshopgui.listeners.CurrencyListener;
import dev.turjo.easyshopgui.managers.PlayerPreferencesManager;
import dev.turjo.easyshopgui.managers.TransactionManager;
import dev.turjo.easyshopgui.currency.PaperCurrency;
import dev.turjo.easyshopgui.marketplace.AIMarketplace;
import dev.turjo.easyshopgui.commands.WithdrawCommand;
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
 * @version 2.0.0
 */
public final class EasyShopGUI extends JavaPlugin {

    private static EasyShopGUI instance;
    private long startTime;
    
    // Core Managers
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private GuiManager guiManager;
    private TransactionManager transactionManager;
    private PlayerPreferencesManager playerPreferencesManager;
    private HookManager hookManager;
    private CronScheduler cronScheduler;
    private UpdateChecker updateChecker;
    private PaperCurrency paperCurrency;
    private AIMarketplace aiMarketplace;

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

            // Debug mode must be set as early as possible so every subsequent
            // initialization step logs correctly. Fallback default now matches
            // config.yml's actual shipped default (false) rather than disagreeing
            // with it - a "Supreme Edition" production plugin should not default to
            // verbose debug logging if the config key is ever missing/fails to parse.
            Logger.setDebugMode(configManager.getMainConfig().getBoolean("plugin.debug", false));
            
            // Initialize database. A failure here degrades gracefully rather than
            // disabling the whole plugin - TransactionManager/PaperCurrency's database
            // calls are already null/uninitialized-safe (they just skip persistence),
            // so the core shop GUI, buying, and selling all keep working with this
            // one piece running in-memory-only. Unlike economy below, the database is
            // not something every feature depends on, so it doesn't warrant a hard stop.
            databaseManager = new DatabaseManager(this);
            if (!databaseManager.initialize()) {
                Logger.warn("Database initialization failed - transaction history and " +
                        "cheque audit stats will not persist across restarts this session, " +
                        "but the shop itself will work normally. Check the error above and " +
                        "your config.yml's database settings.");
            }
            
            // Initialize economy
            economyManager = new EconomyManager(this);
            if (!economyManager.setupEconomy()) {
                Logger.error("Failed to setup economy! Vault is required.");
                return false;
            }
            
            // Initialize managers
            guiManager = new GuiManager(this);
            transactionManager = new TransactionManager(this);
            playerPreferencesManager = new PlayerPreferencesManager(this);
            hookManager = new HookManager(this);
            cronScheduler = new CronScheduler(this);
            updateChecker = new UpdateChecker(this);
            paperCurrency = new PaperCurrency(this);
            aiMarketplace = new AIMarketplace(this);
            
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
            
            if (getCommand("withdraw") != null) {
                getCommand("withdraw").setExecutor(new WithdrawCommand(this));
                getCommand("withdraw").setTabCompleter(new WithdrawCommand(this));
            } else {
                Logger.warn("Command 'withdraw' not found in plugin.yml");
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
        getServer().getPluginManager().registerEvents(new CurrencyListener(this), this);
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

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public PlayerPreferencesManager getPlayerPreferencesManager() {
        return playerPreferencesManager;
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
    
    public PaperCurrency getPaperCurrency() {
        return paperCurrency;
    }
    
    public AIMarketplace getAiMarketplace() {
        return aiMarketplace;
    }
    
    public long getStartTime() {
        return startTime;
    }
}
