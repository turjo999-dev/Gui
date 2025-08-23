package dev.turjo.easyshopgui.data;

import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * Default shop data for fallback
 */
public class ShopData {
    
    /**
     * Create default sections if none are found
     */
    public static Map<String, ShopSection> createDefaultSections() {
        Map<String, ShopSection> sections = new HashMap<>();
        
        // Blocks Section
        ShopSection blocks = new ShopSection("blocks", "Blocks", "&6&l⛏ &e&lBLOCKS SECTION", Material.STONE);
        blocks.setDescription("All building blocks and construction materials");
        blocks.addItem(new ShopItem("stone", "&7Stone", Material.STONE, 1.0, 0.5));
        blocks.addItem(new ShopItem("cobblestone", "&7Cobblestone", Material.COBBLESTONE, 0.8, 0.4));
        blocks.addItem(new ShopItem("dirt", "&6Dirt", Material.DIRT, 0.5, 0.2));
        blocks.addItem(new ShopItem("grass_block", "&aGrass Block", Material.GRASS_BLOCK, 1.2, 0.6));
        sections.put("blocks", blocks);
        
        // Ores Section
        ShopSection ores = new ShopSection("ores", "Ores & Minerals", "&7&l💎 &e&lORES & MINERALS", Material.DIAMOND_ORE);
        ores.setDescription("Raw ores, refined materials and precious gems");
        ores.addItem(new ShopItem("coal", "&8Coal", Material.COAL, 5.0, 2.5));
        ores.addItem(new ShopItem("iron_ingot", "&7Iron Ingot", Material.IRON_INGOT, 12.0, 6.0));
        ores.addItem(new ShopItem("gold_ingot", "&6Gold Ingot", Material.GOLD_INGOT, 20.0, 10.0));
        ores.addItem(new ShopItem("diamond", "&bDiamond", Material.DIAMOND, 80.0, 40.0));
        sections.put("ores", ores);
        
        // Food Section
        ShopSection food = new ShopSection("food", "Food", "&6&l🍎 &e&lFOOD SECTION", Material.GOLDEN_APPLE);
        food.setDescription("All food items, consumables and cooking ingredients");
        food.addItem(new ShopItem("apple", "&cApple", Material.APPLE, 3.0, 1.5));
        food.addItem(new ShopItem("bread", "&6Bread", Material.BREAD, 5.0, 2.5));
        food.addItem(new ShopItem("cooked_beef", "&6Cooked Beef", Material.COOKED_BEEF, 8.0, 4.0));
        food.addItem(new ShopItem("golden_apple", "&6Golden Apple", Material.GOLDEN_APPLE, 100.0, 50.0));
        sections.put("food", food);
        
        // Redstone Section
        ShopSection redstone = new ShopSection("redstone", "Redstone", "&4&l⚡ &e&lREDSTONE SECTION", Material.REDSTONE);
        redstone.setDescription("Redstone components, automation and electrical items");
        redstone.addItem(new ShopItem("redstone", "&cRedstone", Material.REDSTONE, 6.0, 3.0));
        redstone.addItem(new ShopItem("repeater", "&7Repeater", Material.REPEATER, 25.0, 12.5));
        redstone.addItem(new ShopItem("comparator", "&7Comparator", Material.COMPARATOR, 30.0, 15.0));
        redstone.addItem(new ShopItem("piston", "&7Piston", Material.PISTON, 40.0, 20.0));
        sections.put("redstone", redstone);
        
        // Farming Section
        ShopSection farming = new ShopSection("farming", "Farming", "&2&l🌾 &e&lFARMING SECTION", Material.WHEAT);
        farming.setDescription("Seeds, crops, farming tools and animal products");
        farming.addItem(new ShopItem("wheat_seeds", "&eWheat Seeds", Material.WHEAT_SEEDS, 1.0, 0.5));
        farming.addItem(new ShopItem("wheat", "&eWheat", Material.WHEAT, 2.0, 1.0));
        farming.addItem(new ShopItem("carrot", "&6Carrot", Material.CARROT, 2.5, 1.25));
        farming.addItem(new ShopItem("potato", "&6Potato", Material.POTATO, 2.5, 1.25));
        sections.put("farming", farming);
        
        // Decoration Section
        ShopSection decoration = new ShopSection("decoration", "Decoration", "&d&l🌸 &e&lDECORATION", Material.FLOWER_POT);
        decoration.setDescription("Flowers, banners, carpets, lighting and decorative items");
        decoration.addItem(new ShopItem("dandelion", "&eDandelion", Material.DANDELION, 3.0, 1.5));
        decoration.addItem(new ShopItem("poppy", "&cPoppy", Material.POPPY, 3.0, 1.5));
        decoration.addItem(new ShopItem("torch", "&eTorch", Material.TORCH, 2.0, 1.0));
        decoration.addItem(new ShopItem("flower_pot", "&6Flower Pot", Material.FLOWER_POT, 15.0, 7.5));
        sections.put("decoration", decoration);
        
        return sections;
    }
}