package dev.turjo.easyshopgui.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for building ItemStacks
 */
public class ItemBuilder {
    
    private ItemStack item;
    private ItemMeta meta;
    
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }
    
    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }
    
    /**
     * Set display name
     */
    public ItemBuilder setName(String name) {
        if (meta != null) {
            meta.setDisplayName(MessageUtils.colorize(name));
        }
        return this;
    }
    
    /**
     * Set lore
     */
    public ItemBuilder setLore(List<String> lore) {
        if (meta != null) {
            meta.setLore(MessageUtils.colorize(lore));
        }
        return this;
    }
    
    /**
     * Set lore from array
     */
    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }
    
    /**
     * Add lore line
     */
    public ItemBuilder addLore(String line) {
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = Arrays.asList(MessageUtils.colorize(line));
            } else {
                lore.add(MessageUtils.colorize(line));
            }
            meta.setLore(lore);
        }
        return this;
    }
    
    /**
     * Set amount
     */
    public ItemBuilder setAmount(int amount) {
        item.setAmount(Math.max(1, Math.min(amount, 64)));
        return this;
    }
    
    /**
     * Add enchantment
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }
    
    /**
     * Add glow effect
     */
    public ItemBuilder addGlow() {
        return addGlow(true);
    }
    
    /**
     * Add glow effect conditionally
     */
    public ItemBuilder addGlow(boolean condition) {
        if (condition && meta != null) {
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }
    
    /**
     * Set skull owner (for player heads)
     */
    public ItemBuilder setSkullOwner(String owner) {
        if (meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(owner);
        }
        return this;
    }
    
    /**
     * Add item flags
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        if (meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }
    
    /**
     * Set unbreakable
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }
    
    /**
     * Build the ItemStack
     */
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}