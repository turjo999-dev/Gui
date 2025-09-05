package dev.turjo.easyshopgui.commands;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.gui.SellGui;
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
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("easyshopgui.sellgui")) {
            player.sendMessage("§cYou don't have permission to use the sell GUI!");
            return true;
        }
        
        // Open sell GUI
        SellGui sellGui = new SellGui(plugin, player);
        sellGui.open();
        
        return true;
    }
}