package dev.turjo.easyshopgui.placeholders;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.PricingUtil;
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
        // Fixed: this was hardcoded to always return "0" despite
        // TransactionManager.getTransactionCount(player) already existing and working -
        // it was simply never called from here.
        if (params.equals("transactions")) {
            return String.valueOf(plugin.getTransactionManager().getTransactionCount(player));
        }
        
        // %easyshopgui_discount%
        if (params.equals("discount")) {
            return String.valueOf(PricingUtil.getDiscountPercent(player));
        }

        // %easyshopgui_sell_multiplier%
        if (params.equals("sell_multiplier")) {
            return String.valueOf(PricingUtil.getSellMultiplier(player));
        }
        
        return null;
    }
}