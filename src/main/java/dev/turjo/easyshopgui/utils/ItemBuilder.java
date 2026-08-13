package dev.turjo.easyshopgui.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
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
     * Add a single lore line, preserving any existing lore.
     *
     * Uses a mutable ArrayList rather than Arrays.asList(). Arrays.asList() returns a
     * fixed-size list backed by an array; calling addLore() a second time on the same
     * builder used to throw UnsupportedOperationException the moment it tried to add()
     * to a list created that way on the first call.
     */
    public ItemBuilder addLore(String line) {
        if (meta != null) {
            List<String> existing = meta.getLore();
            List<String> lore = existing != null ? new ArrayList<>(existing) : new ArrayList<>();
            lore.add(MessageUtils.colorize(line));
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Append multiple lore lines at once, preserving any existing lore.
     */
    public ItemBuilder addLoreLines(List<String> lines) {
        if (meta != null && lines != null) {
            List<String> existing = meta.getLore();
            List<String> lore = existing != null ? new ArrayList<>(existing) : new ArrayList<>();
            for (String line : lines) {
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
     * Set skull owner by name (for player heads).
     *
     * @deprecated Prefer {@link #setSkullOwner(OfflinePlayer)}. SkullMeta#setOwner(String)
     * is deprecated upstream in Spigot/Paper - it resolves a name to a profile via a
     * name-based lookup rather than using an already-known UUID, which is slower and can
     * fail for names Mojang no longer serves lookups for. Kept for callers that only have
     * a String available.
     */
    @Deprecated
    public ItemBuilder setSkullOwner(String owner) {
        if (meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(owner);
        }
        return this;
    }

    /**
     * Set skull owner from an already-resolved player (for player heads). Preferred over
     * the String overload - uses the player's UUID/profile directly instead of a
     * name-based lookup.
     */
    public ItemBuilder setSkullOwner(OfflinePlayer owner) {
        if (meta instanceof SkullMeta && owner != null) {
            ((SkullMeta) meta).setOwningPlayer(owner);
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