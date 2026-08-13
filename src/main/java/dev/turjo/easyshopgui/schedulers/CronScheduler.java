package dev.turjo.easyshopgui.schedulers;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.Logger;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Handles the periodic restock job for limited-stock items.
 *
 * Previously this class only logged a message and did nothing on both start and
 * shutdown, despite config.yml already defining limited-stock.restock-interval (a
 * simple "every N minutes" interval) as if this were live. This implements that
 * interval-based restock for real.
 *
 * config.yml also defines scheduler.schedules.daily-restock / weekly-reset as true cron
 * expressions ("0 0 0 * * ?" etc.) - implementing real cron parsing/evaluation is a
 * separate, larger feature (either a cron-parsing dependency or hand-rolled evaluation
 * logic) rather than a fix to existing broken behaviour, so those two keys remain
 * reserved for a future version. Only the simple minutes-based interval is wired up
 * here; the class name "CronScheduler" is a legacy name from the original scaffolding
 * and doesn't reflect true cron support.
 */
public class CronScheduler {
    
    private final EasyShopGUI plugin;
    private BukkitTask restockTask;
    
    public CronScheduler(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start the restock scheduler, if enabled in config.
     */
    public void startSchedulers() {
        boolean enabled = plugin.getConfigManager().getMainConfig().getBoolean("limited-stock.enabled", true);
        if (!enabled) {
            Logger.info("Limited stock restocking is disabled in config.yml");
            return;
        }

        int intervalMinutes = plugin.getConfigManager().getMainConfig().getInt("limited-stock.restock-interval", 60);
        long intervalTicks = intervalMinutes * 60L * 20L; // minutes -> ticks (20 ticks/sec)

        restockTask = Bukkit.getScheduler().runTaskTimer(plugin, this::performRestock, intervalTicks, intervalTicks);

        Logger.info("Restock scheduler started (every " + intervalMinutes + " minutes)");
    }
    
    /**
     * Reset every limited-stock item (stock != -1) back to its configured default
     * stock. Runs on the main thread since it mutates ShopItem fields that GUIs read
     * on the main thread (see ShopItem's volatile fields for the cross-thread case -
     * this one doesn't need that since it's already on the main thread via
     * runTaskTimer).
     */
    private void performRestock() {
        int defaultStock = plugin.getConfigManager().getMainConfig().getInt("limited-stock.default-stock", 64);
        boolean announce = plugin.getConfigManager().getMainConfig().getBoolean("limited-stock.announce-restocks", true);

        int restockedCount = 0;
        for (ShopSection section : plugin.getGuiManager().getSections().values()) {
            for (ShopItem item : section.getItems()) {
                if (item.getStock() != -1) {
                    item.setStock(defaultStock);
                    restockedCount++;
                }
            }
        }

        Logger.info("Restocked " + restockedCount + " limited-stock items");

        if (announce && restockedCount > 0) {
            Bukkit.broadcastMessage(MessageUtils.colorize(
                    "&a&l✦ &eThe shop has been restocked! &a&l✦"));
        }
    }
    
    /**
     * Stop the restock scheduler.
     */
    public void shutdown() {
        if (restockTask != null && !restockTask.isCancelled()) {
            restockTask.cancel();
        }
        Logger.info("Restock scheduler stopped");
    }
}
