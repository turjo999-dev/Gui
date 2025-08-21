package dev.turjo.easyshopgui.commands;

import dev.turjo.easyshopgui.EasyShopGUI;
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
                plugin.getConfigManager().reloadAllConfigs();
                sender.sendMessage("§aConfiguration reloaded successfully!");
                break;
                
            case "update":
                plugin.getUpdateChecker().checkForUpdates();
                sender.sendMessage("§aChecking for updates...");
                break;
                
            case "info":
                sendPluginInfo(sender);
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
        sender.sendMessage("§e/eshop reload §7- Reload configuration");
        sender.sendMessage("§e/eshop update §7- Check for updates");
        sender.sendMessage("§e/eshop info §7- Show plugin information");
        sender.sendMessage("§e/eshop help §7- Show this help message");
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
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("shop")) {
            if (args.length == 1) {
                // Add shop names here
                completions.addAll(Arrays.asList("main", "blocks", "items", "tools", "food"));
            }
        } else if (command.getName().equalsIgnoreCase("eshop")) {
            if (args.length == 1) {
                completions.addAll(Arrays.asList("reload", "update", "info", "help"));
            }
        }
        
        return completions;
    }
}