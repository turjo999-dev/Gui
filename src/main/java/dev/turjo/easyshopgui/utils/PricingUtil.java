package dev.turjo.easyshopgui.utils;

import org.bukkit.entity.Player;

/**
 * Single source of truth for permission-based discounts and sell multipliers.
 *
 * Previously this logic (VIP/premium/member discount tiers) was copy-pasted across
 * four different classes purely for tooltip display, and was never actually applied
 * when money changed hands - players saw a discount promised in the GUI but paid full
 * price at checkout. Centralizing it here means the number shown in every tooltip is
 * now mathematically the same number used by every purchase and sale.
 */
public final class PricingUtil {

    private PricingUtil() {
    }

    /**
     * Discount percentage (0-100) applied to buy prices for this player, based on the
     * highest discount permission they hold.
     */
    public static int getDiscountPercent(Player player) {
        if (player.hasPermission("easyshopgui.discount.vip")) return 15;
        if (player.hasPermission("easyshopgui.discount.premium")) return 10;
        if (player.hasPermission("easyshopgui.discount.member")) return 5;
        return 0;
    }

    /**
     * Sell multiplier (1.0 = no bonus) applied to sell prices for this player, based on
     * the highest multiplier permission they hold.
     */
    public static double getSellMultiplier(Player player) {
        if (player.hasPermission("easyshopgui.multiplier.vip")) return 1.5;
        if (player.hasPermission("easyshopgui.multiplier.premium")) return 1.3;
        if (player.hasPermission("easyshopgui.multiplier.member")) return 1.1;
        return 1.0;
    }

    /**
     * Apply this player's discount to a base buy price. This is the number that should
     * actually be charged - GUIs must display this value, not the raw market price, so
     * the tooltip and the checkout always agree.
     */
    public static double applyBuyDiscount(Player player, double baseBuyPrice) {
        int discount = getDiscountPercent(player);
        if (discount <= 0) return baseBuyPrice;
        return baseBuyPrice * (1.0 - (discount / 100.0));
    }

    /**
     * Apply this player's sell multiplier to a base sell price. This is the number that
     * should actually be paid out - GUIs must display this value, not the raw market
     * price, so the tooltip and the checkout always agree.
     */
    public static double applySellMultiplier(Player player, double baseSellPrice) {
        double multiplier = getSellMultiplier(player);
        if (multiplier == 1.0) return baseSellPrice;
        return baseSellPrice * multiplier;
    }
}
