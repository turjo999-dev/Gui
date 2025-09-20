package dev.turjo.easyshopgui.commands;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.QuickSellGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler for shop commands
 */
public class ShopCommand implements CommandExecutor, TabCompleter {
    
    private final EasyShopGUI plugin;
    
    public ShopCommand(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Handle /shop command
        if (command.getName().equalsIgnoreCase("shop")) {
            return handleShopCommand(sender, args);
        }
        
        // Handle /sell command
        if (command.getName().equalsIgnoreCase("sell")) {
            return handleSellCommand(sender, args);
        }
        
        // Handle /eshop command
        if (command.getName().equalsIgnoreCase("eshop")) {
            return handleAdminCommand(sender, args);
        }
        
        return false;
    }
    
    /**
     * Handle shop command
     */
    private boolean handleShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("easyshopgui.use")) {
            player.sendMessage("§cYou don't have permission to use shops!");
            return true;
        }
        
        // Open default shop or specific shop
        String shopName = args.length > 0 ? args[0] : "main";
        plugin.getGuiManager().openShop(player, shopName);
        
        return true;
    }
    
    /**
     * Handle sell command
     */
    private boolean handleSellCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("easyshopgui.sell")) {
            player.sendMessage("§cYou don't have permission to use the sell GUI!");
            return true;
        }
        
        // Use the SAME QuickSell GUI as the main shop
        QuickSellGui quickSellGui = new QuickSellGui(plugin, player);
        
        // Store in the listener's tracking map for proper event handling
        if (plugin.getServer().getPluginManager().isPluginEnabled(plugin)) {
            // Get the GuiListener instance and store the GUI
            plugin.getGuiManager().getActiveQuickSellGuis().put(player, quickSellGui);
        }
        
        quickSellGui.open();
        player.sendMessage("§a💸 Quick Sell GUI opened! Drop items to sell them.");
        
        return true;
    }
    
    /**
     * Handle admin command
     */
    private boolean handleAdminCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("easyshopgui.admin")) {
            sender.sendMessage("§cYou don't have permission to use admin commands!");
            return true;
        }
        
        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
            case "rl":
                plugin.getConfigManager().reloadAllConfigs();
                plugin.getGuiManager().reloadSections();
                sender.sendMessage("§aConfiguration reloaded successfully!");
                break;
                
            case "update":
                plugin.getUpdateChecker().checkForUpdates();
                sender.sendMessage("§aChecking for updates...");
                break;
                
            case "info":
            case "version":
            case "ver":
                sendPluginInfo(sender);
                break;
                
            case "debug":
                boolean currentDebug = plugin.getConfigManager().getMainConfig().getBoolean("plugin.debug", false);
                plugin.getConfigManager().getMainConfig().set("plugin.debug", !currentDebug);
                plugin.getConfigManager().saveConfig("config.yml");
                dev.turjo.easyshopgui.utils.Logger.setDebugMode(!currentDebug);
                sender.sendMessage("§aDebug mode " + (!currentDebug ? "§aenabled" : "§cdisabled") + "!");
                break;
                
            case "stats":
            case "statistics":
                sendStatistics(sender);
                break;
                
            case "help":
                sendAdminHelp(sender);
                break;
                
            default:
                sender.sendMessage("§cUnknown subcommand: " + subCommand);
                sendAdminHelp(sender);
                break;
        }
        
        return true;
    }
    
    /**
     * Send admin help message
     */
    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== EasyShopGUI Admin Commands ===");
        sender.sendMessage("§e/eshop reload §7(§e/eshop rl§7) - Reload configuration");
        sender.sendMessage("§e/eshop update §7- Check for updates");
        sender.sendMessage("§e/eshop info §7(§e/eshop ver§7) - Show plugin information");
        sender.sendMessage("§e/eshop debug §7- Toggle debug mode");
        sender.sendMessage("§e/eshop stats §7- Show plugin statistics");
        sender.sendMessage("§e/eshop help §7- Show this help message");
        sender.sendMessage("§6§l=== Player Commands ===");
        sender.sendMessage("§e/shop §7- Open main shop");
        sender.sendMessage("§e/sell §7- Open quick sell GUI");
    }
    
    /**
     * Send plugin information
     */
    private void sendPluginInfo(CommandSender sender) {
        sender.sendMessage("§6§l=== EasyShopGUI Information ===");
        sender.sendMessage("§eVersion: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§eAuthor: §f" + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage("§eWebsite: §f" + plugin.getDescription().getWebsite());
        sender.sendMessage("§eDescription: §f" + plugin.getDescription().getDescription());
    }
    
    /**
     * Send plugin statistics
     */
    private void sendStatistics(CommandSender sender) {
        sender.sendMessage("§6§l=== EasyShopGUI Statistics ===");
        sender.sendMessage("§eTotal Sections: §f" + plugin.getGuiManager().getSections().size());
        int totalItems = plugin.getGuiManager().getSections().values().stream()
                .mapToInt(section -> section.getItems().size()).sum();
        sender.sendMessage("§eTotal Items: §f" + totalItems);
        sender.sendMessage("§eDebug Mode: §f" + (dev.turjo.easyshopgui.utils.Logger.isDebugMode() ? "§aEnabled" : "§cDisabled"));
        sender.sendMessage("§eUptime: §f" + getUptime());
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("shop")) {
            if (args.length == 1) {
                // Add shop names here
                completions.addAll(Arrays.asList("main", "blocks", "items", "tools", "food"));
            }
        } else if (command.getName().equalsIgnoreCase("sell")) {
            // No tab completions for sell command
        } else if (command.getName().equalsIgnoreCase("eshop")) {
            if (args.length == 1) {
                completions.addAll(Arrays.asList("reload", "rl", "update", "info", "version", "ver", "debug", "stats", "statistics", "help"));
            }
        }
        
        return completions;
    }
    
    private String getUptime() {
        long uptime = System.currentTimeMillis() - plugin.getStartTime();
        long seconds = uptime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%dh %dm %ds", hours % 24, minutes % 60, seconds % 60);
    }
}