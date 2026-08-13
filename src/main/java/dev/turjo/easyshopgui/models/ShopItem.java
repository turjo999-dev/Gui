package dev.turjo.easyshopgui.models;

import dev.turjo.easyshopgui.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a shop item with all its properties
 */
public class ShopItem {
    
    private String id;
    private String displayName;
    private String description;
    private Material material;
    // These three fields are written from a background thread by AIMarketplace's
    // scheduled price/stock updates (runTaskTimerAsynchronously) while being read on
    // the main thread by every GUI that renders this item. volatile guarantees the main
    // thread always sees the latest value written by the async updater rather than a
    // possibly stale cached copy - plain double/int fields give no such guarantee across
    // threads.
    private volatile double buyPrice;
    private volatile double sellPrice;
    private volatile int stock;
    private String demand;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private String permission;
    private boolean glowing;
    private String nbtData;
    // Only set for items whose Material is POTION/SPLASH_POTION/LINGERING_POTION.
    // Without this, every potion entry would render as an identical, effect-less item
    // in both the shop GUI and the player's inventory after purchase - Material alone
    // can't distinguish "Potion of Swiftness" from "Potion of Healing", since Bukkit
    // represents that distinction entirely in PotionMeta, not in the Material enum.
    private PotionType potionType;
    
    public ShopItem(String id, String displayName, Material material, double buyPrice, double sellPrice) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = -1; // Unlimited by default
        this.demand = "medium";
        this.description = "A quality item from our shop";
    }
    
    /**
     * Create ItemStack from this shop item, including name, lore, enchantments, glow,
     * and (for potion items) the actual potion effect - so what the player receives on
     * purchase matches what the shop GUI showed them.
     *
     * displayName/lore are stored raw (with literal "&" color codes) since they come
     * straight from YAML - colorize() them here rather than assuming a caller already
     * did, since the previous single call site (GuiListener.buyItem) skipped this
     * method entirely and just did `new ItemStack(material, amount)`, delivering a
     * completely bare, unnamed stack with no lore regardless of what the section YAML
     * configured.
     */
    public ItemStack createItemStack(int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(MessageUtils.colorize(displayName));
            if (lore != null) {
                meta.setLore(lore.stream().map(MessageUtils::colorize).collect(Collectors.toList()));
            }
            
            if (enchantments != null) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }

            if (potionType != null && meta instanceof PotionMeta) {
                ((PotionMeta) meta).setBasePotionType(potionType);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    
    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public String getDemand() { return demand; }
    public void setDemand(String demand) { this.demand = demand; }
    
    public List<String> getLore() { return lore; }
    public void setLore(List<String> lore) { this.lore = lore; }
    
    public Map<Enchantment, Integer> getEnchantments() { return enchantments; }
    public void setEnchantments(Map<Enchantment, Integer> enchantments) { this.enchantments = enchantments; }
    
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    
    public boolean isGlowing() { return glowing; }
    public void setGlowing(boolean glowing) { this.glowing = glowing; }
    
    public String getNbtData() { return nbtData; }
    public void setNbtData(String nbtData) { this.nbtData = nbtData; }

    public PotionType getPotionType() { return potionType; }
    public void setPotionType(PotionType potionType) { this.potionType = potionType; }
}