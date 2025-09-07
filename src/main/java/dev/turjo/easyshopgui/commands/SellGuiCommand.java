package dev.turjo.easyshopgui.commands;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SellGui;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /sellgui
 */
public class SellGuiCommand implements CommandExecutor {
    
    private final EasyShopGUI plugin;
    
    public SellGuiCommand(EasyShopGUI plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            Logger.debug("Non-player tried to use /sellgui command");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("easyshopgui.sellgui")) {
            player.sendMessage("§cYou don't have permission to use the sell GUI!");
            Logger.debug("Player " + player.getName() + " lacks permission for /sellgui");
            return true;
        }
        
        // Open sell GUI
        SellGui sellGui = new SellGui(plugin, player);
        sellGui.open();
        
        Logger.debug("Opened SellGui for player: " + player.getName() + " via command");
        player.sendMessage("§a💸 Opened sell interface! Drag items to sell them.");
        
        return true;
    }
}