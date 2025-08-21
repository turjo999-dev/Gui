package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Main shop GUI with all sections
 */
public class ShopGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private final String shopName;
    
    public ShopGui(EasyShopGUI plugin, Player player, String shopName) {
        this.plugin = plugin;
        this.player = player;
        this.shopName = shopName;
    }
    
    /**
     * Open the main shop GUI
     */
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, MessageUtils.colorize("&6&l✦ &e&lEASY SHOP GUI &6&l✦"));
        
        // Fill background
        fillBackground(gui);
        
        // Add shop sections
        addShopSections(gui);
        
        // Add navigation and info items
        addNavigationItems(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background with decorative items
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        ItemStack border = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots with background
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
        
        // Add border
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }
    }
    
    /**
     * Add all shop sections
     */
    private void addShopSections(Inventory gui) {
        // Blocks Section
        gui.setItem(10, new ItemBuilder(Material.STONE)
                .setName("&6&l⛏ &e&lBLOCKS SECTION")
                .setLore(Arrays.asList(
                        "&7▸ &fAll building blocks",
                        "&7▸ &fStone, Wood, Ores",
                        "&7▸ &fDecorative blocks",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Tools & Weapons Section
        gui.setItem(11, new ItemBuilder(Material.DIAMOND_SWORD)
                .setName("&c&l⚔ &e&lTOOLS & WEAPONS")
                .setLore(Arrays.asList(
                        "&7▸ &fSwords, Axes, Pickaxes",
                        "&7▸ &fBows, Crossbows",
                        "&7▸ &fEnchanted gear",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Armor Section
        gui.setItem(12, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .setName("&9&l🛡 &e&lARMOR SECTION")
                .setLore(Arrays.asList(
                        "&7▸ &fAll armor types",
                        "&7▸ &fLeather to Netherite",
                        "&7▸ &fEnchanted armor",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Food Section
        gui.setItem(13, new ItemBuilder(Material.GOLDEN_APPLE)
                .setName("&6&l🍎 &e&lFOOD SECTION")
                .setLore(Arrays.asList(
                        "&7▸ &fAll food items",
                        "&7▸ &fPotions & Brewing",
                        "&7▸ &fSpecial foods",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Redstone Section
        gui.setItem(14, new ItemBuilder(Material.REDSTONE)
                .setName("&4&l⚡ &e&lREDSTONE SECTION")
                .setLore(Arrays.asList(
                        "&7▸ &fRedstone components",
                        "&7▸ &fPistons, Repeaters",
                        "&7▸ &fAutomation items",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Farming Section
        gui.setItem(15, new ItemBuilder(Material.WHEAT)
                .setName("&2&l🌾 &e&lFARMING SECTION")
                .setLore(Arrays.asList(
                        "&7▸ &fSeeds & Crops",
                        "&7▸ &fFarming tools",
                        "&7▸ &fAnimal items",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Decoration Section
        gui.setItem(16, new ItemBuilder(Material.FLOWER_POT)
                .setName("&d&l🌸 &e&lDECORATION")
                .setLore(Arrays.asList(
                        "&7▸ &fFlowers & Plants",
                        "&7▸ &fDecorative blocks",
                        "&7▸ &fBanners & Paintings",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Spawners Section (Premium)
        gui.setItem(19, new ItemBuilder(Material.SPAWNER)
                .setName("&5&l👹 &e&lSPAWNERS")
                .setLore(Arrays.asList(
                        "&7▸ &fAll mob spawners",
                        "&7▸ &fCustom spawners",
                        "&7▸ &fUpgradeable spawners",
                        "",
                        "&5&l⭐ &dPREMIUM SECTION",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Enchanted Books Section
        gui.setItem(20, new ItemBuilder(Material.ENCHANTED_BOOK)
                .setName("&3&l📚 &e&lENCHANTED BOOKS")
                .setLore(Arrays.asList(
                        "&7▸ &fAll enchantments",
                        "&7▸ &fCustom enchants",
                        "&7▸ &fRare enchantments",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Potions Section
        gui.setItem(21, new ItemBuilder(Material.POTION)
                .setName("&8&l🧪 &e&lPOTIONS & EFFECTS")
                .setLore(Arrays.asList(
                        "&7▸ &fAll potion types",
                        "&7▸ &fSplash potions",
                        "&7▸ &fLingering potions",
                        "",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Rare Items Section
        gui.setItem(22, new ItemBuilder(Material.NETHER_STAR)
                .setName("&f&l⭐ &e&lRARE ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fNether stars",
                        "&7▸ &fDragon eggs",
                        "&7▸ &fSpecial items",
                        "",
                        "&5&l⭐ &dEXCLUSIVE SECTION",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Seasonal Section
        gui.setItem(23, new ItemBuilder(Material.JACK_O_LANTERN)
                .setName("&6&l🎃 &e&lSEASONAL ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fHoliday items",
                        "&7▸ &fSeasonal decorations",
                        "&7▸ &fLimited time offers",
                        "",
                        "&6&l🎉 &eEVENT SECTION",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
        
        // Custom Items Section
        gui.setItem(24, new ItemBuilder(Material.COMMAND_BLOCK)
                .setName("&c&l⚙ &e&lCUSTOM ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fServer custom items",
                        "&7▸ &fPlugin items",
                        "&7▸ &fUnique gear",
                        "",
                        "&c&l⚡ &cSERVER EXCLUSIVE",
                        "&a&l➤ &aClick to browse!"
                ))
                .addGlow()
                .build());
    }
    
    /**
     * Add navigation and info items
     */
    private void addNavigationItems(Inventory gui) {
        // Player balance
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        gui.setItem(4, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&6&l💰 &e&lYOUR BALANCE")
                .setLore(Arrays.asList(
                        "&7▸ &fCurrent Balance:",
                        "&a&l$" + String.format("%.2f", balance),
                        "",
                        "&7▸ &fEarn money by:",
                        "&7  • &aSelling items",
                        "&7  • &aCompleting jobs",
                        "&7  • &aVoting for server"
                ))
                .addGlow()
                .build());
        
        // Search function
        gui.setItem(37, new ItemBuilder(Material.COMPASS)
                .setName("&b&l🔍 &e&lSEARCH ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fQuickly find items",
                        "&7▸ &fSearch by name",
                        "&7▸ &fFilter by category",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
        
        // Transaction history
        gui.setItem(38, new ItemBuilder(Material.BOOK)
                .setName("&3&l📋 &e&lTRANSACTION HISTORY")
                .setLore(Arrays.asList(
                        "&7▸ &fView purchase history",
                        "&7▸ &fTrack your spending",
                        "&7▸ &fExport data",
                        "",
                        "&a&l➤ &aClick to view!"
                ))
                .build());
        
        // Shop settings
        gui.setItem(39, new ItemBuilder(Material.COMPARATOR)
                .setName("&7&l⚙ &e&lSHOP SETTINGS")
                .setLore(Arrays.asList(
                        "&7▸ &fConfirm purchases: &aON",
                        "&7▸ &fSound effects: &aON",
                        "&7▸ &fNotifications: &aON",
                        "",
                        "&a&l➤ &aClick to configure!"
                ))
                .build());
        
        // Quick sell
        gui.setItem(40, new ItemBuilder(Material.HOPPER)
                .setName("&c&l💸 &e&lQUICK SELL")
                .setLore(Arrays.asList(
                        "&7▸ &fSell inventory items",
                        "&7▸ &fBulk selling",
                        "&7▸ &fInstant money",
                        "",
                        "&a&l➤ &aClick to sell!"
                ))
                .build());
        
        // Shop info
        gui.setItem(41, new ItemBuilder(Material.PAPER)
                .setName("&e&l📄 &e&lSHOP INFORMATION")
                .setLore(Arrays.asList(
                        "&7▸ &fShop Name: &a" + shopName,
                        "&7▸ &fTotal Items: &a2,847",
                        "&7▸ &fCategories: &a13",
                        "&7▸ &fLast Updated: &aToday",
                        "",
                        "&7▸ &fDynamic Pricing: &aENABLED",
                        "&7▸ &fStock System: &aENABLED"
                ))
                .build());
        
        // Close button
        gui.setItem(43, new ItemBuilder(Material.BARRIER)
                .setName("&c&l✖ &e&lCLOSE SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fClose this menu",
                        "",
                        "&c&l➤ &cClick to close!"
                ))
                .build());
    }
}