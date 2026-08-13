package dev.turjo.easyshopgui.hooks;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detects optional companion plugins and exposes their presence to the rest of the
 * plugin.
 *
 * Previously this was a one-line stub, and plugin.yml/config.yml each independently
 * declared different, disjoint lists of "supported" plugins that no code ever actually
 * checked for - EcoEnchants/AdvancedEnchantments/ExcellentEnchants/CrazyEnchantments/
 * EpicSpawners/UpgradeableSpawners were named in plugin.yml's softdepend, while
 * DiscordSRV/Citizens were named in config.yml's hooks section, and neither set
 * appeared anywhere in the Java source. This class now genuinely checks for all of
 * them (both lists, kept in one place so they can't drift apart again) and logs what's
 * actually installed, giving isHooked() as a real query point for any feature that
 * wants to branch on a companion plugin being present. Building full integrations with
 * each of these (e.g. actually reading EcoEnchants' custom enchant registry, posting to
 * a DiscordSRV webhook) is a larger feature per plugin, not a bug fix - this makes the
 * detection layer real and honest rather than silently promising integrations that
 * don't exist.
 */
public class HookManager {
    
    private final EasyShopGUI plugin;
    private final Map<String, Boolean> hookedPlugins = new LinkedHashMap<>();

    // Every plugin either plugin.yml or config.yml has ever advertised support for,
    // consolidated in one place.
    private static final String[] KNOWN_COMPANION_PLUGINS = {
            "PlaceholderAPI",
            "EcoEnchants",
            "AdvancedEnchantments",
            "ExcellentEnchants",
            "CrazyEnchantments",
            "EpicSpawners",
            "UpgradeableSpawners",
            "DiscordSRV",
            "Citizens"
    };
    
    public HookManager(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Detect all known companion plugins and log what's present. Safe to call even if
     * none are installed - this only reads the plugin manager, it doesn't require any
     * of them.
     */
    public void setupHooks() {
        Logger.info("Detecting companion plugins...");

        int found = 0;
        for (String pluginName : KNOWN_COMPANION_PLUGINS) {
            boolean present = Bukkit.getPluginManager().getPlugin(pluginName) != null
                    && Bukkit.getPluginManager().isPluginEnabled(pluginName);
            hookedPlugins.put(pluginName, present);
            if (present) {
                found++;
                Logger.info("  ✓ Found " + pluginName + " - integration available");
            }
        }

        if (found == 0) {
            Logger.info("No companion plugins detected. EasyShopGUI works standalone; " +
                    "install any of the plugins listed in plugin.yml's softdepend for deeper integration.");
        } else {
            Logger.info("Companion plugin detection complete: " + found + "/" + KNOWN_COMPANION_PLUGINS.length + " found.");
        }
    }

    /**
     * Whether a given companion plugin was detected and is enabled.
     */
    public boolean isHooked(String pluginName) {
        return hookedPlugins.getOrDefault(pluginName, false);
    }

    /**
     * Snapshot of every known companion plugin and whether it was detected.
     */
    public Map<String, Boolean> getDetectedPlugins() {
        return hookedPlugins;
    }
}
