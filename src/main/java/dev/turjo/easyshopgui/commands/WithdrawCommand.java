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
 * Command handler for paper currency withdrawal
 */
public class WithdrawCommand implements CommandExecutor, TabCompleter {
    
    private final EasyShopGUI plugin;
    
    public WithdrawCommand(EasyShopGUI plugin) {
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
        if (!player.hasPermission("easyshopgui.withdraw")) {
            player.sendMessage("§cYou don't have permission to withdraw money as cheques!");
            return true;
        }
        
        // Check arguments
        if (args.length != 1) {
            player.sendMessage("§c💰 Usage: /withdraw <amount>");
            player.sendMessage("§e💡 Example: /withdraw 100");
            return true;
        }
        
        // Parse amount
        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c💰 Invalid amount! Please enter a valid number.");
            return true;
        }
        
        // Withdraw cheque
        boolean success = plugin.getPaperCurrency().withdrawCheque(player, amount);
        
        if (!success) {
            // Error message already sent by withdrawCheque method
            return true;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Suggest common amounts
            completions.addAll(Arrays.asList("10", "50", "100", "500", "1000", "5000", "10000"));
        }
        
        return completions;
    }
}