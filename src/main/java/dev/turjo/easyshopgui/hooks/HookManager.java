package dev.turjo.easyshopgui.hooks;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;

/**
 * Manager for handling plugin hooks
 */
public class HookManager {
    
    private final EasyShopGUI plugin;
    
    public HookManager(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Setup all plugin hooks
     */
    public void setupHooks() {
        Logger.info("Setting up plugin hooks...");
        // TODO: Implement plugin hooks
    }
}