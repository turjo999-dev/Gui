package dev.turjo.easyshopgui.placeholders;

import dev.turjo.easyshopgui.EasyShopGUI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI expansion for EasyShopGUI
 */
public class EasyShopPlaceholderExpansion extends PlaceholderExpansion {
    
    private final EasyShopGUI plugin;
    
    public EasyShopPlaceholderExpansion(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "easyshopgui";
    }
    
    @Override
    public String getAuthor() {
        return "Turjo";
    }
    
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";
        
        // %easyshopgui_balance%
        if (params.equals("balance")) {
            double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
            return String.format("%.2f", balance);
        }
        
        // %easyshopgui_transactions%
        if (params.equals("transactions")) {
            return "0"; // TODO: Implement transaction count
        }
        
        // %easyshopgui_discount%
        if (params.equals("discount")) {
            if (player.hasPermission("easyshopgui.discount.vip")) return "15";
            if (player.hasPermission("easyshopgui.discount.premium")) return "10";
            if (player.hasPermission("easyshopgui.discount.member")) return "5";
            return "0";
        }
        
        return null;
    }
}