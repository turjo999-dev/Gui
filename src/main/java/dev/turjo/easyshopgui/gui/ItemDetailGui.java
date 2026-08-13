package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.PricingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Detailed item GUI with buy/sell options and quantity controls.
 *
 * Title is prefixed with "SECTION" so it is recognised by GuiListener's shop-GUI
 * detector (isShopGUI). Previously the title was just the item's display name, which
 * almost never matched any of the fixed keyword substrings the click handler checks
 * for - so the inventory click event was never cancelled here. In practice that meant
 * every button (buy, sell, quantity +/-) was inert, and worse, players could freely
 * drag the displayed item and decorative blocks out of the GUI into their own
 * inventory since nothing was stopping the default pickup behaviour.
 */
public class ItemDetailGui {

    private final EasyShopGUI plugin;
    private final Player player;
    private final ShopItem shopItem;
    private final String sectionId;
    private int quantity = 1;

    public ItemDetailGui(EasyShopGUI plugin, Player player, ShopItem shopItem, String sectionId) {
        this.plugin = plugin;
        this.player = player;
        this.shopItem = shopItem;
        this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Open the item detail GUI
     */
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 45,
                MessageUtils.colorize("&8§l» &6&l✦ ITEM DETAILS &6&l✦ &8§l«  &7[SECTION]"));

        fillBackground(gui);
        addMainItem(gui);
        addQuantityControls(gui);
        addActionButtons(gui);
        addPlayerInfo(gui);
        addNavigation(gui);

        player.openInventory(gui);
    }

    /**
     * Fill background with a refined two-tone frame
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        ItemStack border = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        ItemStack accent = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }

        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : borderSlots) {
            gui.setItem(slot, border);
        }

        int[] accentSlots = {2, 3, 5, 6};
        for (int slot : accentSlots) {
            gui.setItem(slot, accent);
        }
    }

    /**
     * Add main item display
     */
    private void addMainItem(Inventory gui) {
        double buyPrice = PricingUtil.applyBuyDiscount(player, shopItem.getBuyPrice());
        double sellPrice = PricingUtil.applySellMultiplier(player, shopItem.getSellPrice());
        double totalBuyPrice = buyPrice * quantity;
        double totalSellPrice = sellPrice * quantity;
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        int discount = PricingUtil.getDiscountPercent(player);

        ItemStack displayItem = new ItemBuilder(shopItem.getMaterial())
                .setName("&6&l⭐ " + shopItem.getDisplayName() + " &6&l⭐")
                .setLore(Arrays.asList(
                        "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                        "&7" + shopItem.getDescription(),
                        "&8▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪▪",
                        "",
                        "&7▸ &fQuantity Selected: &e" + quantity,
                        "&7▸ &fMax Stack Size: &e" + shopItem.getMaterial().getMaxStackSize(),
                        "",
                        "&6&l💰 PRICING",
                        discount > 0
                            ? "&a  Buy (Each): &f$" + String.format("%.2f", buyPrice) + " &7(&a-" + discount + "%&7)"
                            : "&a  Buy (Each): &f$" + String.format("%.2f", buyPrice),
                        "&a  Buy (Total): &f$" + String.format("%.2f", totalBuyPrice),
                        "&c  Sell (Each): &f$" + String.format("%.2f", sellPrice),
                        "&c  Sell (Total): &f$" + String.format("%.2f", totalSellPrice),
                        "",
                        "&6&l📦 STOCK",
                        "&7▸ &fAvailable: " + (shopItem.getStock() == -1 ? "&aUnlimited" : "&e" + shopItem.getStock()),
                        "&7▸ &fDemand: " + getDemandColor(shopItem.getDemand()) + shopItem.getDemand().toUpperCase(),
                        "",
                        "&7▸ &fYour Balance: " + (balance >= totalBuyPrice ? "&a" : "&c") + "$" + String.format("%.2f", balance),
                        "&7▸ &fCan Afford: " + (balance >= totalBuyPrice ? "&a✓ YES" : "&c✗ NO")
                ))
                .setAmount(Math.min(quantity, 64))
                .addGlow()
                .build();

        gui.setItem(13, displayItem);
    }

    /**
     * Add quantity controls
     */
    private void addQuantityControls(Inventory gui) {
        gui.setItem(10, new ItemBuilder(Material.RED_CONCRETE)
                .setName("&c&l-5 &e&lQUANTITY")
                .setLore(Arrays.asList(
                        "&7▸ &fDecrease by 5",
                        "&7▸ &fCurrent: &e" + quantity,
                        "",
                        "&c&l➤ &cClick to decrease!"
                ))
                .build());

        gui.setItem(11, new ItemBuilder(Material.ORANGE_CONCRETE)
                .setName("&c&l-1 &e&lQUANTITY")
                .setLore(Arrays.asList(
                        "&7▸ &fDecrease by 1",
                        "&7▸ &fCurrent: &e" + quantity,
                        "",
                        "&c&l➤ &cClick to decrease!"
                ))
                .build());

        gui.setItem(12, new ItemBuilder(Material.YELLOW_CONCRETE)
                .setName("&e&l📊 &e&lCURRENT QUANTITY")
                .setLore(Arrays.asList(
                        "&7▸ &fSelected Amount: &e" + quantity,
                        "&7▸ &fMax Possible: &e" + shopItem.getMaterial().getMaxStackSize(),
                        "",
                        "&e&l⚡ &eAdjust with buttons!"
                ))
                .setAmount(Math.min(quantity, 64))
                .build());

        gui.setItem(14, new ItemBuilder(Material.LIME_CONCRETE)
                .setName("&a&l+1 &e&lQUANTITY")
                .setLore(Arrays.asList(
                        "&7▸ &fIncrease by 1",
                        "&7▸ &fCurrent: &e" + quantity,
                        "",
                        "&a&l➤ &aClick to increase!"
                ))
                .build());

        gui.setItem(15, new ItemBuilder(Material.GREEN_CONCRETE)
                .setName("&a&l+5 &e&lQUANTITY")
                .setLore(Arrays.asList(
                        "&7▸ &fIncrease by 5",
                        "&7▸ &fCurrent: &e" + quantity,
                        "",
                        "&a&l➤ &aClick to increase!"
                ))
                .build());

        gui.setItem(19, new ItemBuilder(Material.IRON_INGOT)
                .setName("&7&l⚡ &e&lSET TO 16")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick set to 16",
                        "",
                        "&a&l➤ &aClick to set!"
                ))
                .setAmount(16)
                .build());

        gui.setItem(20, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&6&l⚡ &e&lSET TO 32")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick set to 32",
                        "",
                        "&a&l➤ &aClick to set!"
                ))
                .setAmount(32)
                .build());

        gui.setItem(21, new ItemBuilder(Material.DIAMOND)
                .setName("&b&l⚡ &e&lSET TO 64")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick set to 64",
                        "",
                        "&a&l➤ &aClick to set!"
                ))
                .setAmount(64)
                .build());
    }

    /**
     * Add action buttons
     */
    private void addActionButtons(Inventory gui) {
        double buyPrice = PricingUtil.applyBuyDiscount(player, shopItem.getBuyPrice());
        double sellPrice = PricingUtil.applySellMultiplier(player, shopItem.getSellPrice());
        double totalBuyPrice = buyPrice * quantity;
        double totalSellPrice = sellPrice * quantity;
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        boolean canAfford = balance >= totalBuyPrice;

        gui.setItem(30, new ItemBuilder(canAfford ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setName(canAfford ? "&a&l💰 &e&lBUY ITEMS" : "&c&l❌ &e&lCANNOT AFFORD")
                .setLore(Arrays.asList(
                        "&7▸ &fQuantity: &e" + quantity,
                        "&7▸ &fTotal Cost: &a$" + String.format("%.2f", totalBuyPrice),
                        "&7▸ &fYour Balance: " + (canAfford ? "&a" : "&c") + "$" + String.format("%.2f", balance),
                        "&7▸ &fAfter Purchase: &e$" + String.format("%.2f", balance - totalBuyPrice),
                        "",
                        canAfford ? "&a&l➤ &aClick to purchase!" : "&c&l➤ &cInsufficient funds!"
                ))
                .addGlow(canAfford)
                .build());

        int playerItemCount = getPlayerItemCount();
        boolean canSell = playerItemCount >= quantity;

        gui.setItem(32, new ItemBuilder(canSell ? Material.GOLD_BLOCK : Material.COAL_BLOCK)
                .setName(canSell ? "&6&l💸 &e&lSELL ITEMS" : "&c&l❌ &e&lNOT ENOUGH ITEMS")
                .setLore(Arrays.asList(
                        "&7▸ &fQuantity: &e" + quantity,
                        "&7▸ &fTotal Earn: &6$" + String.format("%.2f", totalSellPrice),
                        "&7▸ &fYou Have: " + (canSell ? "&a" : "&c") + playerItemCount,
                        "&7▸ &fAfter Sale: &e$" + String.format("%.2f", balance + totalSellPrice),
                        "",
                        canSell ? "&6&l➤ &6Click to sell!" : "&c&l➤ &cNot enough items!"
                ))
                .addGlow(canSell)
                .build());

        gui.setItem(31, new ItemBuilder(Material.NETHER_STAR)
                .setName("&d&l⚡ &e&lSELL ALL OF THIS ITEM")
                .setLore(Arrays.asList(
                        "&7▸ &fSell every unit of this item",
                        "&7▸ &fcurrently in your inventory",
                        "&7▸ &fYou Have: &e" + playerItemCount,
                        "",
                        "&d&l➤ &dShift-click to sell all!"
                ))
                .addGlow()
                .build());
    }

    /**
     * Add player info
     */
    private void addPlayerInfo(Inventory gui) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);

        gui.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&6&l👤 &e&l" + player.getName().toUpperCase())
                .setLore(Arrays.asList(
                        "&7▸ &fBalance: &a$" + String.format("%.2f", balance),
                        "&7▸ &fSelected Item: &e" + shopItem.getDisplayName(),
                        "&7▸ &fQuantity: &e" + quantity,
                        "",
                        "&7▸ &fDiscount: &a" + PricingUtil.getDiscountPercent(player) + "%",
                        "&7▸ &fSell Multiplier: &a" + PricingUtil.getSellMultiplier(player) + "x",
                        "&7▸ &fVIP Status: " + (player.hasPermission("easyshopgui.vip") ? "&aActive" : "&7Inactive")
                ))
                .setSkullOwner(player)
                .build());
    }

    /**
     * Add navigation
     */
    private void addNavigation(Inventory gui) {
        gui.setItem(36, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SECTION")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to " + (sectionId != null ? sectionId : "section"),
                        "",
                        "&c&l➤ &cClick to go back!"
                ))
                .build());

        gui.setItem(44, new ItemBuilder(Material.BARRIER)
                .setName("&c&l✖ &e&lCLOSE")
                .setLore(Arrays.asList(
                        "&7▸ &fClose this menu",
                        "",
                        "&c&l➤ &cClick to close!"
                ))
                .build());
    }

    private String getDemandColor(String demand) {
        switch (demand.toLowerCase()) {
            case "high": return "&c";
            case "medium": return "&e";
            case "low": return "&a";
            default: return "&7";
        }
    }

    private int getPlayerItemCount() {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == shopItem.getMaterial()) {
                count += item.getAmount();
            }
        }
        return count;
    }

    // Quantity control methods
    public void increaseQuantity(int amount) {
        quantity = Math.min(quantity + amount, 64);
        open();
    }

    public void decreaseQuantity(int amount) {
        quantity = Math.max(quantity - amount, 1);
        open();
    }

    public void setQuantity(int amount) {
        quantity = Math.max(1, Math.min(amount, 64));
        open();
    }
}
