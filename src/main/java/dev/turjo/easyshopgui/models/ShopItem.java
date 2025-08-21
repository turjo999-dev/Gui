package dev.turjo.easyshopgui.models;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Represents a shop item with all its properties
 */
public class ShopItem {
    
    private String id;
    private String displayName;
    private String description;
    private Material material;
    private double buyPrice;
    private double sellPrice;
    private int stock;
    private String demand;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private String permission;
    private boolean glowing;
    private String nbtData;
    
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
     * Create ItemStack from this shop item
     */
    public ItemStack createItemStack(int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore != null) {
                meta.setLore(lore);
            }
            
            if (enchantments != null) {
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
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
}