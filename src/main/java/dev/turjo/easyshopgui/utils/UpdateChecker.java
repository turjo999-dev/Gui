package dev.turjo.easyshopgui.utils;

import dev.turjo.easyshopgui.EasyShopGUI;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Checks the Spigot resource API for a newer published version of this plugin than the
 * one currently running.
 *
 * Previously this was a stub that only logged "Checking for updates..." and did
 * nothing. Runs fully asynchronously and fails silently (falls back to a debug-level
 * log) if the check can't complete - a failed update check must never block or delay
 * plugin startup, since server operators without outbound internet access (or with it
 * temporarily down) still need the plugin to enable normally.
 *
 * NOTE: SPIGOT_RESOURCE_ID below is a placeholder. Set it to this plugin's actual
 * Spigot/Polymart resource ID once it has one; until then this safely no-ops (a
 * resource ID of 0 will get a 404 from the API, which is treated as "check failed,
 * skip silently" like any other network failure).
 */
public class UpdateChecker {
    
    private final EasyShopGUI plugin;
    private static final int SPIGOT_RESOURCE_ID = 0; // TODO: set once published

    public UpdateChecker(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Check for plugin updates asynchronously. Logs the result; never throws back to
     * the caller.
     */
    public void checkForUpdates() {
        if (SPIGOT_RESOURCE_ID <= 0) {
            Logger.debug("Update checker not configured (no resource ID set), skipping");
            return;
        }

        Logger.info("Checking for updates...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + SPIGOT_RESOURCE_ID);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                String latestVersion;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    latestVersion = reader.readLine();
                }

                String currentVersion = plugin.getDescription().getVersion();

                if (latestVersion != null && !latestVersion.trim().equalsIgnoreCase(currentVersion.trim())) {
                    Logger.info("A new version of EasyShopGUI is available: " + latestVersion +
                            " (currently running " + currentVersion + ")");
                } else {
                    Logger.info("EasyShopGUI is up to date (" + currentVersion + ")");
                }

            } catch (Exception e) {
                // Network unavailable, API down, etc. - never fatal, just skip.
                Logger.debug("Update check failed (this is not fatal): " + e.getMessage());
            }
        });
    }
}
