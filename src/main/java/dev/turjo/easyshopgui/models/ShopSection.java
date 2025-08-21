package dev.turjo.easyshopgui.models;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shop section/category
 */
public class ShopSection {
    
    private String id;
    private String name;
    private String displayName;
    private Material icon;
    private List<ShopItem> items;
    private String permission;
    private boolean enabled;
    
    public ShopSection(String id, String name, String displayName, Material icon) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.items = new ArrayList<>();
        this.enabled = true;
    }
    
    /**
     * Add item to section
     */
    public void addItem(ShopItem item) {
        items.add(item);
    }
    
    /**
     * Remove item from section
     */
    public void removeItem(String itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
    }
    
    /**
     * Get item by ID
     */
    public ShopItem getItem(String itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public Material getIcon() { return icon; }
    public void setIcon(Material icon) { this.icon = icon; }
    
    public List<ShopItem> getItems() { return items; }
    public void setItems(List<ShopItem> items) { this.items = items; }
    
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}