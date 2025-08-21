package dev.turjo.easyshopgui.economy;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Economy manager for handling all economy operations
 */
public class EconomyManager {
    
    private final EasyShopGUI plugin;
    private Economy economy;
    
    public EconomyManager(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Setup economy provider
     */
    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Logger.error("Vault not found! Economy features will be disabled.");
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Logger.error("No economy provider found! Please install an economy plugin.");
            return false;
        }
        
        economy = rsp.getProvider();
        Logger.info("Economy provider found: " + economy.getName());
        return true;
    }
    
    /**
     * Get economy provider
     */
    public Economy getEconomy() {
        return economy;
    }
}