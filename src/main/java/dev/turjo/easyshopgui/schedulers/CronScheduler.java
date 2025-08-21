package dev.turjo.easyshopgui.schedulers;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;

/**
 * Cron scheduler for handling scheduled tasks
 */
public class CronScheduler {
    
    private final EasyShopGUI plugin;
    
    public CronScheduler(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start all schedulers
     */
    public void startSchedulers() {
        Logger.info("Starting cron schedulers...");
        // TODO: Implement cron schedulers
    }
    
    /**
     * Shutdown all schedulers
     */
    public void shutdown() {
        Logger.info("Shutting down cron schedulers...");
        // TODO: Implement scheduler shutdown
    }
}