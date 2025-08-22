package dev.turjo.easyshopgui.data;

import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Default shop data with balanced economy
 */
public class ShopData {
    
    /**
     * Create default shop sections with balanced pricing
     */
    public static Map<String, ShopSection> createDefaultSections() {
        Map<String, ShopSection> sections = new HashMap<>();
        
        // Blocks Section
        ShopSection blocks = new ShopSection("blocks", "Blocks", "&6&l⛏ &e&lBLOCKS SECTION", Material.STONE);
        blocks.setDescription("All building blocks and construction materials");
        addBlockItems(blocks);
        sections.put("blocks", blocks);
        
        // Ores Section
        ShopSection ores = new ShopSection("ores", "Ores & Minerals", "&7&l💎 &e&lORES & MINERALS", Material.DIAMOND_ORE);
        ores.setDescription("Raw ores and refined materials");
        addOreItems(ores);
        sections.put("ores", ores);
        
        // Food Section
        ShopSection food = new ShopSection("food", "Food", "&6&l🍎 &e&lFOOD SECTION", Material.GOLDEN_APPLE);
        food.setDescription("All food items and consumables");
        addFoodItems(food);
        sections.put("food", food);
        
        // Redstone Section
        ShopSection redstone = new ShopSection("redstone", "Redstone", "&4&l⚡ &e&lREDSTONE SECTION", Material.REDSTONE);
        redstone.setDescription("Redstone components and automation items");
        addRedstoneItems(redstone);
        sections.put("redstone", redstone);
        
        // Farming Section
        ShopSection farming = new ShopSection("farming", "Farming", "&2&l🌾 &e&lFARMING SECTION", Material.WHEAT);
        farming.setDescription("Seeds, crops and farming supplies");
        addFarmingItems(farming);
        sections.put("farming", farming);
        
        // Decoration Section
        ShopSection decoration = new ShopSection("decoration", "Decoration", "&d&l🌸 &e&lDECORATION", Material.FLOWER_POT);
        decoration.setDescription("Flowers, banners and decorative items");
        addDecorationItems(decoration);
        sections.put("decoration", decoration);
        
        // Potions Section
        ShopSection potions = new ShopSection("potions", "Potions", "&8&l🧪 &e&lPOTIONS & EFFECTS", Material.POTION);
        potions.setDescription("Potions, brewing ingredients and magical effects");
        addPotionItems(potions);
        sections.put("potions", potions);
        
        return sections;
    }
    
    /**
     * Add block items with balanced pricing
     */
    private static void addBlockItems(ShopSection section) {
        // Basic building blocks
        section.addItem(createItem("stone", "Stone", Material.STONE, 1.0, 0.5, "Basic building stone"));
        section.addItem(createItem("cobblestone", "Cobblestone", Material.COBBLESTONE, 0.8, 0.4, "Rough stone block"));
        section.addItem(createItem("dirt", "Dirt", Material.DIRT, 0.5, 0.2, "Basic earth block"));
        section.addItem(createItem("grass_block", "Grass Block", Material.GRASS_BLOCK, 1.2, 0.6, "Natural grass block"));
        section.addItem(createItem("sand", "Sand", Material.SAND, 1.0, 0.5, "Fine sand particles"));
        section.addItem(createItem("gravel", "Gravel", Material.GRAVEL, 1.0, 0.5, "Loose stone particles"));
        section.addItem(createItem("clay", "Clay", Material.CLAY, 2.0, 1.0, "Moldable clay block"));
        
        // Wood types
        section.addItem(createItem("oak_log", "Oak Log", Material.OAK_LOG, 2.0, 1.0, "Sturdy oak wood"));
        section.addItem(createItem("birch_log", "Birch Log", Material.BIRCH_LOG, 2.2, 1.1, "Light birch wood"));
        section.addItem(createItem("spruce_log", "Spruce Log", Material.SPRUCE_LOG, 2.1, 1.05, "Dark spruce wood"));
        section.addItem(createItem("jungle_log", "Jungle Log", Material.JUNGLE_LOG, 2.5, 1.25, "Exotic jungle wood"));
        section.addItem(createItem("acacia_log", "Acacia Log", Material.ACACIA_LOG, 2.3, 1.15, "Orange acacia wood"));
        section.addItem(createItem("dark_oak_log", "Dark Oak Log", Material.DARK_OAK_LOG, 2.4, 1.2, "Dark oak wood"));
        
        // Planks
        section.addItem(createItem("oak_planks", "Oak Planks", Material.OAK_PLANKS, 1.5, 0.75, "Processed oak planks"));
        section.addItem(createItem("birch_planks", "Birch Planks", Material.BIRCH_PLANKS, 1.6, 0.8, "Processed birch planks"));
        section.addItem(createItem("spruce_planks", "Spruce Planks", Material.SPRUCE_PLANKS, 1.55, 0.775, "Processed spruce planks"));
        section.addItem(createItem("jungle_planks", "Jungle Planks", Material.JUNGLE_PLANKS, 1.8, 0.9, "Processed jungle planks"));
        section.addItem(createItem("acacia_planks", "Acacia Planks", Material.ACACIA_PLANKS, 1.7, 0.85, "Processed acacia planks"));
        section.addItem(createItem("dark_oak_planks", "Dark Oak Planks", Material.DARK_OAK_PLANKS, 1.75, 0.875, "Processed dark oak planks"));
        
        // Glass and decorative
        section.addItem(createItem("glass", "Glass", Material.GLASS, 3.0, 1.5, "Transparent glass block"));
        section.addItem(createItem("sandstone", "Sandstone", Material.SANDSTONE, 2.5, 1.25, "Compressed sand block"));
        section.addItem(createItem("smooth_sandstone", "Smooth Sandstone", Material.SMOOTH_SANDSTONE, 3.0, 1.5, "Polished sandstone"));
        section.addItem(createItem("red_sandstone", "Red Sandstone", Material.RED_SANDSTONE, 2.8, 1.4, "Red compressed sand"));
        
        // Stone variants
        section.addItem(createItem("granite", "Granite", Material.GRANITE, 1.2, 0.6, "Speckled igneous rock"));
        section.addItem(createItem("diorite", "Diorite", Material.DIORITE, 1.2, 0.6, "White speckled rock"));
        section.addItem(createItem("andesite", "Andesite", Material.ANDESITE, 1.2, 0.6, "Gray volcanic rock"));
        section.addItem(createItem("polished_granite", "Polished Granite", Material.POLISHED_GRANITE, 1.8, 0.9, "Smooth granite"));
        section.addItem(createItem("polished_diorite", "Polished Diorite", Material.POLISHED_DIORITE, 1.8, 0.9, "Smooth diorite"));
        section.addItem(createItem("polished_andesite", "Polished Andesite", Material.POLISHED_ANDESITE, 1.8, 0.9, "Smooth andesite"));
        
        // Wool colors
        section.addItem(createItem("white_wool", "White Wool", Material.WHITE_WOOL, 4.0, 2.0, "Soft white wool"));
        section.addItem(createItem("red_wool", "Red Wool", Material.RED_WOOL, 4.5, 2.25, "Vibrant red wool"));
        section.addItem(createItem("blue_wool", "Blue Wool", Material.BLUE_WOOL, 4.5, 2.25, "Deep blue wool"));
        section.addItem(createItem("green_wool", "Green Wool", Material.GREEN_WOOL, 4.5, 2.25, "Natural green wool"));
        section.addItem(createItem("yellow_wool", "Yellow Wool", Material.YELLOW_WOOL, 4.5, 2.25, "Bright yellow wool"));
        section.addItem(createItem("orange_wool", "Orange Wool", Material.ORANGE_WOOL, 4.5, 2.25, "Vibrant orange wool"));
        section.addItem(createItem("purple_wool", "Purple Wool", Material.PURPLE_WOOL, 4.5, 2.25, "Royal purple wool"));
        section.addItem(createItem("pink_wool", "Pink Wool", Material.PINK_WOOL, 4.5, 2.25, "Soft pink wool"));
        section.addItem(createItem("black_wool", "Black Wool", Material.BLACK_WOOL, 4.5, 2.25, "Dark black wool"));
        section.addItem(createItem("gray_wool", "Gray Wool", Material.GRAY_WOOL, 4.5, 2.25, "Neutral gray wool"));
        section.addItem(createItem("light_gray_wool", "Light Gray Wool", Material.LIGHT_GRAY_WOOL, 4.5, 2.25, "Light gray wool"));
        section.addItem(createItem("cyan_wool", "Cyan Wool", Material.CYAN_WOOL, 4.5, 2.25, "Bright cyan wool"));
        section.addItem(createItem("magenta_wool", "Magenta Wool", Material.MAGENTA_WOOL, 4.5, 2.25, "Vibrant magenta wool"));
        section.addItem(createItem("lime_wool", "Lime Wool", Material.LIME_WOOL, 4.5, 2.25, "Bright lime wool"));
        section.addItem(createItem("brown_wool", "Brown Wool", Material.BROWN_WOOL, 4.5, 2.25, "Natural brown wool"));
        
        // Concrete
        section.addItem(createItem("white_concrete", "White Concrete", Material.WHITE_CONCRETE, 5.0, 2.5, "Solid white concrete"));
        section.addItem(createItem("red_concrete", "Red Concrete", Material.RED_CONCRETE, 5.5, 2.75, "Solid red concrete"));
        section.addItem(createItem("blue_concrete", "Blue Concrete", Material.BLUE_CONCRETE, 5.5, 2.75, "Solid blue concrete"));
        section.addItem(createItem("green_concrete", "Green Concrete", Material.GREEN_CONCRETE, 5.5, 2.75, "Solid green concrete"));
        section.addItem(createItem("yellow_concrete", "Yellow Concrete", Material.YELLOW_CONCRETE, 5.5, 2.75, "Solid yellow concrete"));
        section.addItem(createItem("orange_concrete", "Orange Concrete", Material.ORANGE_CONCRETE, 5.5, 2.75, "Solid orange concrete"));
        section.addItem(createItem("purple_concrete", "Purple Concrete", Material.PURPLE_CONCRETE, 5.5, 2.75, "Solid purple concrete"));
        section.addItem(createItem("pink_concrete", "Pink Concrete", Material.PINK_CONCRETE, 5.5, 2.75, "Solid pink concrete"));
        section.addItem(createItem("black_concrete", "Black Concrete", Material.BLACK_CONCRETE, 5.5, 2.75, "Solid black concrete"));
        section.addItem(createItem("gray_concrete", "Gray Concrete", Material.GRAY_CONCRETE, 5.5, 2.75, "Solid gray concrete"));
        section.addItem(createItem("light_gray_concrete", "Light Gray Concrete", Material.LIGHT_GRAY_CONCRETE, 5.5, 2.75, "Solid light gray concrete"));
        section.addItem(createItem("cyan_concrete", "Cyan Concrete", Material.CYAN_CONCRETE, 5.5, 2.75, "Solid cyan concrete"));
        section.addItem(createItem("magenta_concrete", "Magenta Concrete", Material.MAGENTA_CONCRETE, 5.5, 2.75, "Solid magenta concrete"));
        section.addItem(createItem("lime_concrete", "Lime Concrete", Material.LIME_CONCRETE, 5.5, 2.75, "Solid lime concrete"));
        section.addItem(createItem("brown_concrete", "Brown Concrete", Material.BROWN_CONCRETE, 5.5, 2.75, "Solid brown concrete"));
        
        // Terracotta
        section.addItem(createItem("terracotta", "Terracotta", Material.TERRACOTTA, 3.5, 1.75, "Hardened clay"));
        section.addItem(createItem("white_terracotta", "White Terracotta", Material.WHITE_TERRACOTTA, 4.0, 2.0, "White hardened clay"));
        section.addItem(createItem("red_terracotta", "Red Terracotta", Material.RED_TERRACOTTA, 4.5, 2.25, "Red hardened clay"));
        section.addItem(createItem("blue_terracotta", "Blue Terracotta", Material.BLUE_TERRACOTTA, 4.5, 2.25, "Blue hardened clay"));
        section.addItem(createItem("green_terracotta", "Green Terracotta", Material.GREEN_TERRACOTTA, 4.5, 2.25, "Green hardened clay"));
        
        // Bricks and stone bricks
        section.addItem(createItem("bricks", "Bricks", Material.BRICKS, 6.0, 3.0, "Clay brick block"));
        section.addItem(createItem("stone_bricks", "Stone Bricks", Material.STONE_BRICKS, 4.0, 2.0, "Carved stone bricks"));
        section.addItem(createItem("mossy_stone_bricks", "Mossy Stone Bricks", Material.MOSSY_STONE_BRICKS, 5.0, 2.5, "Overgrown stone bricks"));
        section.addItem(createItem("cracked_stone_bricks", "Cracked Stone Bricks", Material.CRACKED_STONE_BRICKS, 4.5, 2.25, "Weathered stone bricks"));
        section.addItem(createItem("chiseled_stone_bricks", "Chiseled Stone Bricks", Material.CHISELED_STONE_BRICKS, 6.0, 3.0, "Decorated stone bricks"));
        
        // Nether blocks
        section.addItem(createItem("netherrack", "Netherrack", Material.NETHERRACK, 2.0, 1.0, "Nether stone"));
        section.addItem(createItem("nether_bricks", "Nether Bricks", Material.NETHER_BRICKS, 8.0, 4.0, "Dark nether bricks"));
        section.addItem(createItem("red_nether_bricks", "Red Nether Bricks", Material.RED_NETHER_BRICKS, 9.0, 4.5, "Red nether bricks"));
        section.addItem(createItem("soul_sand", "Soul Sand", Material.SOUL_SAND, 15.0, 7.5, "Slows movement"));
        section.addItem(createItem("soul_soil", "Soul Soil", Material.SOUL_SOIL, 12.0, 6.0, "Nether soil"));
        section.addItem(createItem("basalt", "Basalt", Material.BASALT, 3.0, 1.5, "Volcanic rock"));
        section.addItem(createItem("polished_basalt", "Polished Basalt", Material.POLISHED_BASALT, 4.0, 2.0, "Smooth basalt"));
        section.addItem(createItem("blackstone", "Blackstone", Material.BLACKSTONE, 5.0, 2.5, "Dark nether stone"));
        section.addItem(createItem("polished_blackstone", "Polished Blackstone", Material.POLISHED_BLACKSTONE, 6.0, 3.0, "Smooth blackstone"));
        
        // End blocks
        section.addItem(createItem("end_stone", "End Stone", Material.END_STONE, 10.0, 5.0, "End dimension stone"));
        section.addItem(createItem("end_stone_bricks", "End Stone Bricks", Material.END_STONE_BRICKS, 12.0, 6.0, "Carved end stone"));
        section.addItem(createItem("purpur_block", "Purpur Block", Material.PURPUR_BLOCK, 15.0, 7.5, "Purple end block"));
        section.addItem(createItem("purpur_pillar", "Purpur Pillar", Material.PURPUR_PILLAR, 16.0, 8.0, "Purple pillar"));
        
        // Prismarine blocks
        section.addItem(createItem("prismarine", "Prismarine", Material.PRISMARINE, 20.0, 10.0, "Ocean guardian block"));
        section.addItem(createItem("prismarine_bricks", "Prismarine Bricks", Material.PRISMARINE_BRICKS, 25.0, 12.5, "Carved prismarine"));
        section.addItem(createItem("dark_prismarine", "Dark Prismarine", Material.DARK_PRISMARINE, 30.0, 15.0, "Dark ocean block"));
        
        // Ice blocks
        section.addItem(createItem("ice", "Ice", Material.ICE, 8.0, 4.0, "Frozen water"));
        section.addItem(createItem("packed_ice", "Packed Ice", Material.PACKED_ICE, 15.0, 7.5, "Dense ice block"));
        section.addItem(createItem("blue_ice", "Blue Ice", Material.BLUE_ICE, 25.0, 12.5, "Ultra-dense ice"));
        
        // Obsidian
        section.addItem(createItem("obsidian", "Obsidian", Material.OBSIDIAN, 50.0, 25.0, "Volcanic glass"));
        section.addItem(createItem("crying_obsidian", "Crying Obsidian", Material.CRYING_OBSIDIAN, 100.0, 50.0, "Respawn anchor fuel"));
    }
    
    /**
     * Add ore items with balanced pricing
     */
    private static void addOreItems(ShopSection section) {
        // Raw ores
        section.addItem(createItem("coal_ore", "Coal Ore", Material.COAL_ORE, 8.0, 4.0, "Coal-bearing stone"));
        section.addItem(createItem("iron_ore", "Iron Ore", Material.IRON_ORE, 15.0, 7.5, "Iron-bearing stone"));
        section.addItem(createItem("copper_ore", "Copper Ore", Material.COPPER_ORE, 12.0, 6.0, "Copper-bearing stone"));
        section.addItem(createItem("gold_ore", "Gold Ore", Material.GOLD_ORE, 25.0, 12.5, "Gold-bearing stone"));
        section.addItem(createItem("lapis_ore", "Lapis Ore", Material.LAPIS_ORE, 30.0, 15.0, "Lapis lazuli ore"));
        section.addItem(createItem("redstone_ore", "Redstone Ore", Material.REDSTONE_ORE, 20.0, 10.0, "Redstone-bearing stone"));
        section.addItem(createItem("diamond_ore", "Diamond Ore", Material.DIAMOND_ORE, 100.0, 50.0, "Precious diamond ore"));
        section.addItem(createItem("emerald_ore", "Emerald Ore", Material.EMERALD_ORE, 150.0, 75.0, "Rare emerald ore"));
        
        // Deepslate ores
        section.addItem(createItem("deepslate_coal_ore", "Deepslate Coal Ore", Material.DEEPSLATE_COAL_ORE, 10.0, 5.0, "Deep coal ore"));
        section.addItem(createItem("deepslate_iron_ore", "Deepslate Iron Ore", Material.DEEPSLATE_IRON_ORE, 18.0, 9.0, "Deep iron ore"));
        section.addItem(createItem("deepslate_copper_ore", "Deepslate Copper Ore", Material.DEEPSLATE_COPPER_ORE, 15.0, 7.5, "Deep copper ore"));
        section.addItem(createItem("deepslate_gold_ore", "Deepslate Gold Ore", Material.DEEPSLATE_GOLD_ORE, 30.0, 15.0, "Deep gold ore"));
        section.addItem(createItem("deepslate_lapis_ore", "Deepslate Lapis Ore", Material.DEEPSLATE_LAPIS_ORE, 35.0, 17.5, "Deep lapis ore"));
        section.addItem(createItem("deepslate_redstone_ore", "Deepslate Redstone Ore", Material.DEEPSLATE_REDSTONE_ORE, 25.0, 12.5, "Deep redstone ore"));
        section.addItem(createItem("deepslate_diamond_ore", "Deepslate Diamond Ore", Material.DEEPSLATE_DIAMOND_ORE, 120.0, 60.0, "Deep diamond ore"));
        section.addItem(createItem("deepslate_emerald_ore", "Deepslate Emerald Ore", Material.DEEPSLATE_EMERALD_ORE, 180.0, 90.0, "Deep emerald ore"));
        
        // Nether ores
        section.addItem(createItem("nether_gold_ore", "Nether Gold Ore", Material.NETHER_GOLD_ORE, 35.0, 17.5, "Nether gold ore"));
        section.addItem(createItem("nether_quartz_ore", "Nether Quartz Ore", Material.NETHER_QUARTZ_ORE, 40.0, 20.0, "Quartz-bearing netherrack"));
        section.addItem(createItem("ancient_debris", "Ancient Debris", Material.ANCIENT_DEBRIS, 500.0, 250.0, "Netherite source"));
        
        // Processed materials
        section.addItem(createItem("coal", "Coal", Material.COAL, 5.0, 2.5, "Fuel and crafting material"));
        section.addItem(createItem("raw_iron", "Raw Iron", Material.RAW_IRON, 10.0, 5.0, "Unprocessed iron"));
        section.addItem(createItem("raw_copper", "Raw Copper", Material.RAW_COPPER, 8.0, 4.0, "Unprocessed copper"));
        section.addItem(createItem("raw_gold", "Raw Gold", Material.RAW_GOLD, 18.0, 9.0, "Unprocessed gold"));
        section.addItem(createItem("iron_ingot", "Iron Ingot", Material.IRON_INGOT, 12.0, 6.0, "Refined iron"));
        section.addItem(createItem("copper_ingot", "Copper Ingot", Material.COPPER_INGOT, 10.0, 5.0, "Refined copper"));
        section.addItem(createItem("gold_ingot", "Gold Ingot", Material.GOLD_INGOT, 20.0, 10.0, "Refined gold"));
        section.addItem(createItem("diamond", "Diamond", Material.DIAMOND, 80.0, 40.0, "Precious gemstone"));
        section.addItem(createItem("emerald", "Emerald", Material.EMERALD, 120.0, 60.0, "Rare green gem"));
        section.addItem(createItem("lapis_lazuli", "Lapis Lazuli", Material.LAPIS_LAZULI, 15.0, 7.5, "Blue dye material"));
        section.addItem(createItem("redstone", "Redstone Dust", Material.REDSTONE, 5.0, 2.5, "Magical red dust"));
        section.addItem(createItem("quartz", "Nether Quartz", Material.QUARTZ, 25.0, 12.5, "White crystal"));
        section.addItem(createItem("netherite_scrap", "Netherite Scrap", Material.NETHERITE_SCRAP, 400.0, 200.0, "Refined ancient debris"));
        section.addItem(createItem("netherite_ingot", "Netherite Ingot", Material.NETHERITE_INGOT, 2000.0, 1000.0, "Ultimate material"));
        
        // Mineral blocks
        section.addItem(createItem("coal_block", "Coal Block", Material.COAL_BLOCK, 45.0, 22.5, "Compressed coal"));
        section.addItem(createItem("iron_block", "Iron Block", Material.IRON_BLOCK, 108.0, 54.0, "Compressed iron"));
        section.addItem(createItem("copper_block", "Copper Block", Material.COPPER_BLOCK, 90.0, 45.0, "Compressed copper"));
        section.addItem(createItem("gold_block", "Gold Block", Material.GOLD_BLOCK, 180.0, 90.0, "Compressed gold"));
        section.addItem(createItem("diamond_block", "Diamond Block", Material.DIAMOND_BLOCK, 720.0, 360.0, "Compressed diamonds"));
        section.addItem(createItem("emerald_block", "Emerald Block", Material.EMERALD_BLOCK, 1080.0, 540.0, "Compressed emeralds"));
        section.addItem(createItem("lapis_block", "Lapis Block", Material.LAPIS_BLOCK, 135.0, 67.5, "Compressed lapis"));
        section.addItem(createItem("redstone_block", "Redstone Block", Material.REDSTONE_BLOCK, 45.0, 22.5, "Concentrated redstone"));
        section.addItem(createItem("quartz_block", "Quartz Block", Material.QUARTZ_BLOCK, 100.0, 50.0, "Compressed quartz"));
        section.addItem(createItem("netherite_block", "Netherite Block", Material.NETHERITE_BLOCK, 18000.0, 9000.0, "Ultimate block"));
        
        // Amethyst
        section.addItem(createItem("amethyst_shard", "Amethyst Shard", Material.AMETHYST_SHARD, 30.0, 15.0, "Purple crystal shard"));
        section.addItem(createItem("amethyst_block", "Amethyst Block", Material.AMETHYST_BLOCK, 120.0, 60.0, "Purple crystal block"));
        section.addItem(createItem("budding_amethyst", "Budding Amethyst", Material.BUDDING_AMETHYST, 200.0, 100.0, "Growing amethyst"));
        
        // Other minerals
        section.addItem(createItem("flint", "Flint", Material.FLINT, 3.0, 1.5, "Sharp stone chip"));
        section.addItem(createItem("clay_ball", "Clay Ball", Material.CLAY_BALL, 2.0, 1.0, "Moldable clay"));
        section.addItem(createItem("brick", "Brick", Material.BRICK, 4.0, 2.0, "Fired clay"));
        section.addItem(createItem("nether_brick", "Nether Brick", Material.NETHER_BRICK, 6.0, 3.0, "Dark nether brick"));
        section.addItem(createItem("prismarine_shard", "Prismarine Shard", Material.PRISMARINE_SHARD, 15.0, 7.5, "Ocean crystal"));
        section.addItem(createItem("prismarine_crystals", "Prismarine Crystals", Material.PRISMARINE_CRYSTALS, 20.0, 10.0, "Glowing ocean crystal"));
    }
    
    /**
     * Add food items with balanced pricing
     */
    private static void addFoodItems(ShopSection section) {
        // Basic Foods
        section.addItem(createItem("apple", "Apple", Material.APPLE, 3.0, 1.5, "Crisp red apple"));
        section.addItem(createItem("bread", "Bread", Material.BREAD, 5.0, 2.5, "Fresh baked bread"));
        section.addItem(createItem("cooked_beef", "Cooked Beef", Material.COOKED_BEEF, 8.0, 4.0, "Juicy steak"));
        section.addItem(createItem("cooked_porkchop", "Cooked Porkchop", Material.COOKED_PORKCHOP, 8.0, 4.0, "Tender pork"));
        section.addItem(createItem("cooked_chicken", "Cooked Chicken", Material.COOKED_CHICKEN, 6.0, 3.0, "Roasted chicken"));
        section.addItem(createItem("cooked_mutton", "Cooked Mutton", Material.COOKED_MUTTON, 7.0, 3.5, "Cooked sheep meat"));
        section.addItem(createItem("cooked_rabbit", "Cooked Rabbit", Material.COOKED_RABBIT, 6.5, 3.25, "Cooked rabbit meat"));
        
        // Raw meats
        section.addItem(createItem("beef", "Raw Beef", Material.BEEF, 4.0, 2.0, "Raw cow meat"));
        section.addItem(createItem("porkchop", "Raw Porkchop", Material.PORKCHOP, 4.0, 2.0, "Raw pig meat"));
        section.addItem(createItem("chicken", "Raw Chicken", Material.CHICKEN, 3.0, 1.5, "Raw chicken meat"));
        section.addItem(createItem("mutton", "Raw Mutton", Material.MUTTON, 3.5, 1.75, "Raw sheep meat"));
        section.addItem(createItem("rabbit", "Raw Rabbit", Material.RABBIT, 3.25, 1.625, "Raw rabbit meat"));
        
        // Premium Foods
        section.addItem(createItem("golden_apple", "Golden Apple", Material.GOLDEN_APPLE, 100.0, 50.0, "Magical golden apple"));
        section.addItem(createItem("enchanted_golden_apple", "Enchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE, 1000.0, 500.0, "Legendary healing fruit"));
        section.addItem(createItem("golden_carrot", "Golden Carrot", Material.GOLDEN_CARROT, 25.0, 12.5, "Nutritious golden carrot"));
        
        // Vegetables and crops
        section.addItem(createItem("wheat", "Wheat", Material.WHEAT, 2.0, 1.0, "Basic grain"));
        section.addItem(createItem("carrot", "Carrot", Material.CARROT, 2.5, 1.25, "Orange vegetable"));
        section.addItem(createItem("potato", "Potato", Material.POTATO, 2.5, 1.25, "Starchy tuber"));
        section.addItem(createItem("baked_potato", "Baked Potato", Material.BAKED_POTATO, 4.0, 2.0, "Cooked potato"));
        section.addItem(createItem("beetroot", "Beetroot", Material.BEETROOT, 3.0, 1.5, "Red root vegetable"));
        section.addItem(createItem("beetroot_soup", "Beetroot Soup", Material.BEETROOT_SOUP, 8.0, 4.0, "Hearty vegetable soup"));
        section.addItem(createItem("mushroom_stew", "Mushroom Stew", Material.MUSHROOM_STEW, 6.0, 3.0, "Savory mushroom soup"));
        section.addItem(createItem("suspicious_stew", "Suspicious Stew", Material.SUSPICIOUS_STEW, 15.0, 7.5, "Mysterious flower stew"));
        
        // Fish
        section.addItem(createItem("cod", "Raw Cod", Material.COD, 3.0, 1.5, "Fresh white fish"));
        section.addItem(createItem("salmon", "Raw Salmon", Material.SALMON, 3.5, 1.75, "Fresh pink fish"));
        section.addItem(createItem("cooked_cod", "Cooked Cod", Material.COOKED_COD, 6.0, 3.0, "Flaky white fish"));
        section.addItem(createItem("cooked_salmon", "Cooked Salmon", Material.COOKED_SALMON, 7.0, 3.5, "Pink salmon fillet"));
        section.addItem(createItem("tropical_fish", "Tropical Fish", Material.TROPICAL_FISH, 10.0, 5.0, "Exotic colorful fish"));
        section.addItem(createItem("pufferfish", "Pufferfish", Material.PUFFERFISH, 15.0, 7.5, "Dangerous spiky fish"));
        
        // Sweet Treats
        section.addItem(createItem("cake", "Cake", Material.CAKE, 20.0, 10.0, "Delicious birthday cake"));
        section.addItem(createItem("cookie", "Cookie", Material.COOKIE, 4.0, 2.0, "Sweet chocolate chip cookie"));
        section.addItem(createItem("pumpkin_pie", "Pumpkin Pie", Material.PUMPKIN_PIE, 8.0, 4.0, "Seasonal pumpkin pie"));
        
        // Beverages
        section.addItem(createItem("milk_bucket", "Milk Bucket", Material.MILK_BUCKET, 15.0, 7.5, "Fresh cow milk"));
        section.addItem(createItem("honey_bottle", "Honey Bottle", Material.HONEY_BOTTLE, 12.0, 6.0, "Sweet bee honey"));
        
        // Fruits
        section.addItem(createItem("melon_slice", "Melon Slice", Material.MELON_SLICE, 3.0, 1.5, "Refreshing melon"));
        section.addItem(createItem("sweet_berries", "Sweet Berries", Material.SWEET_BERRIES, 4.0, 2.0, "Sweet forest berries"));
        section.addItem(createItem("glow_berries", "Glow Berries", Material.GLOW_BERRIES, 8.0, 4.0, "Glowing cave berries"));
        section.addItem(createItem("chorus_fruit", "Chorus Fruit", Material.CHORUS_FRUIT, 50.0, 25.0, "Teleportation fruit"));
        
        // Dried kelp and other sea foods
        section.addItem(createItem("dried_kelp", "Dried Kelp", Material.DRIED_KELP, 2.0, 1.0, "Dried seaweed"));
        section.addItem(createItem("kelp", "Kelp", Material.KELP, 1.5, 0.75, "Fresh seaweed"));
        
        // Rotten flesh (for some reason people buy this)
        section.addItem(createItem("rotten_flesh", "Rotten Flesh", Material.ROTTEN_FLESH, 1.0, 0.5, "Zombie meat"));
        section.addItem(createItem("spider_eye", "Spider Eye", Material.SPIDER_EYE, 8.0, 4.0, "Poison ingredient"));
        
        // Eggs and animal products
        section.addItem(createItem("egg", "Egg", Material.EGG, 3.0, 1.5, "Chicken egg"));
        section.addItem(createItem("leather", "Leather", Material.LEATHER, 8.0, 4.0, "Animal hide"));
        section.addItem(createItem("rabbit_hide", "Rabbit Hide", Material.RABBIT_HIDE, 4.0, 2.0, "Small animal hide"));
        section.addItem(createItem("rabbit_foot", "Rabbit's Foot", Material.RABBIT_FOOT, 35.0, 17.5, "Lucky charm"));
    }
    
    /**
     * Add redstone items
     */
    private static void addRedstoneItems(ShopSection section) {
        // Basic redstone
        section.addItem(createItem("redstone", "Redstone Dust", Material.REDSTONE, 5.0, 2.5, "Magical red dust"));
        section.addItem(createItem("redstone_block", "Redstone Block", Material.REDSTONE_BLOCK, 45.0, 22.5, "Concentrated redstone"));
        
        // Components
        section.addItem(createItem("repeater", "Redstone Repeater", Material.REPEATER, 15.0, 7.5, "Signal repeater"));
        section.addItem(createItem("comparator", "Redstone Comparator", Material.COMPARATOR, 20.0, 10.0, "Signal comparator"));
        section.addItem(createItem("piston", "Piston", Material.PISTON, 25.0, 12.5, "Mechanical pusher"));
        section.addItem(createItem("sticky_piston", "Sticky Piston", Material.STICKY_PISTON, 35.0, 17.5, "Pulling piston"));
        
        // Advanced components
        section.addItem(createItem("observer", "Observer", Material.OBSERVER, 30.0, 15.0, "Block update detector"));
        section.addItem(createItem("hopper", "Hopper", Material.HOPPER, 40.0, 20.0, "Item transport"));
        section.addItem(createItem("dropper", "Dropper", Material.DROPPER, 25.0, 12.5, "Item dispenser"));
        section.addItem(createItem("dispenser", "Dispenser", Material.DISPENSER, 30.0, 15.0, "Automatic dispenser"));
        
        // Power sources
        section.addItem(createItem("lever", "Lever", Material.LEVER, 8.0, 4.0, "Manual switch"));
        section.addItem(createItem("stone_button", "Stone Button", Material.STONE_BUTTON, 5.0, 2.5, "Temporary switch"));
        section.addItem(createItem("oak_button", "Oak Button", Material.OAK_BUTTON, 4.0, 2.0, "Wooden switch"));
        section.addItem(createItem("stone_pressure_plate", "Stone Pressure Plate", Material.STONE_PRESSURE_PLATE, 10.0, 5.0, "Weight sensor"));
        section.addItem(createItem("oak_pressure_plate", "Oak Pressure Plate", Material.OAK_PRESSURE_PLATE, 8.0, 4.0, "Wooden weight sensor"));
        section.addItem(createItem("heavy_weighted_pressure_plate", "Heavy Weighted Pressure Plate", Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 15.0, 7.5, "Iron pressure plate"));
        section.addItem(createItem("light_weighted_pressure_plate", "Light Weighted Pressure Plate", Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 12.0, 6.0, "Gold pressure plate"));
        
        // Redstone lamps and torches
        section.addItem(createItem("redstone_lamp", "Redstone Lamp", Material.REDSTONE_LAMP, 20.0, 10.0, "Controllable light"));
        section.addItem(createItem("redstone_torch", "Redstone Torch", Material.REDSTONE_TORCH, 6.0, 3.0, "Power source"));
        section.addItem(createItem("tripwire_hook", "Tripwire Hook", Material.TRIPWIRE_HOOK, 12.0, 6.0, "Invisible trigger"));
        
        // Rails
        section.addItem(createItem("rail", "Rail", Material.RAIL, 8.0, 4.0, "Minecart track"));
        section.addItem(createItem("powered_rail", "Powered Rail", Material.POWERED_RAIL, 15.0, 7.5, "Accelerating track"));
        section.addItem(createItem("detector_rail", "Detector Rail", Material.DETECTOR_RAIL, 12.0, 6.0, "Detection track"));
        section.addItem(createItem("activator_rail", "Activator Rail", Material.ACTIVATOR_RAIL, 10.0, 5.0, "Activation track"));
        
        // Minecarts
        section.addItem(createItem("minecart", "Minecart", Material.MINECART, 50.0, 25.0, "Basic minecart"));
        section.addItem(createItem("chest_minecart", "Chest Minecart", Material.CHEST_MINECART, 70.0, 35.0, "Storage minecart"));
        section.addItem(createItem("furnace_minecart", "Furnace Minecart", Material.FURNACE_MINECART, 80.0, 40.0, "Powered minecart"));
        section.addItem(createItem("hopper_minecart", "Hopper Minecart", Material.HOPPER_MINECART, 90.0, 45.0, "Collection minecart"));
        section.addItem(createItem("tnt_minecart", "TNT Minecart", Material.TNT_MINECART, 100.0, 50.0, "Explosive minecart"));
        
        // Doors and gates
        section.addItem(createItem("oak_door", "Oak Door", Material.OAK_DOOR, 12.0, 6.0, "Wooden door"));
        section.addItem(createItem("iron_door", "Iron Door", Material.IRON_DOOR, 25.0, 12.5, "Metal door"));
        section.addItem(createItem("oak_trapdoor", "Oak Trapdoor", Material.OAK_TRAPDOOR, 10.0, 5.0, "Wooden trapdoor"));
        section.addItem(createItem("iron_trapdoor", "Iron Trapdoor", Material.IRON_TRAPDOOR, 20.0, 10.0, "Metal trapdoor"));
        section.addItem(createItem("oak_fence_gate", "Oak Fence Gate", Material.OAK_FENCE_GATE, 15.0, 7.5, "Wooden gate"));
        
        // Target and daylight sensor
        section.addItem(createItem("target", "Target", Material.TARGET, 25.0, 12.5, "Redstone target"));
        section.addItem(createItem("daylight_detector", "Daylight Detector", Material.DAYLIGHT_DETECTOR, 30.0, 15.0, "Light sensor"));
        
        // Note block and jukebox
        section.addItem(createItem("note_block", "Note Block", Material.NOTE_BLOCK, 20.0, 10.0, "Musical block"));
        section.addItem(createItem("jukebox", "Jukebox", Material.JUKEBOX, 40.0, 20.0, "Music player"));
        
        // Slime and honey blocks
        section.addItem(createItem("slime_block", "Slime Block", Material.SLIME_BLOCK, 45.0, 22.5, "Bouncy block"));
        section.addItem(createItem("honey_block", "Honey Block", Material.HONEY_BLOCK, 36.0, 18.0, "Sticky block"));
        
        // TNT
        section.addItem(createItem("tnt", "TNT", Material.TNT, 50.0, 25.0, "Explosive block"));
        
        // Redstone components materials
        section.addItem(createItem("slime_ball", "Slime Ball", Material.SLIME_BALL, 10.0, 5.0, "Sticky material"));
        section.addItem(createItem("string", "String", Material.STRING, 3.0, 1.5, "Spider silk"));
    }
    
    /**
     * Add farming items
     */
    private static void addFarmingItems(ShopSection section) {
        // Seeds
        section.addItem(createItem("wheat_seeds", "Wheat Seeds", Material.WHEAT_SEEDS, 1.0, 0.5, "Basic crop seeds"));
        section.addItem(createItem("carrot", "Carrot Seeds", Material.CARROT, 1.5, 0.75, "Orange vegetable seeds"));
        section.addItem(createItem("potato", "Potato Seeds", Material.POTATO, 1.5, 0.75, "Starchy tuber seeds"));
        section.addItem(createItem("beetroot_seeds", "Beetroot Seeds", Material.BEETROOT_SEEDS, 2.0, 1.0, "Red vegetable seeds"));
        section.addItem(createItem("pumpkin_seeds", "Pumpkin Seeds", Material.PUMPKIN_SEEDS, 3.0, 1.5, "Large orange fruit seeds"));
        section.addItem(createItem("melon_seeds", "Melon Seeds", Material.MELON_SEEDS, 3.0, 1.5, "Sweet fruit seeds"));
        section.addItem(createItem("cocoa_beans", "Cocoa Beans", Material.COCOA_BEANS, 4.0, 2.0, "Chocolate ingredient"));
        section.addItem(createItem("sweet_berries", "Sweet Berries", Material.SWEET_BERRIES, 4.0, 2.0, "Sweet forest berries"));
        section.addItem(createItem("glow_berries", "Glow Berries", Material.GLOW_BERRIES, 8.0, 4.0, "Glowing cave berries"));
        
        // Farming tools
        section.addItem(createItem("wooden_hoe", "Wooden Hoe", Material.WOODEN_HOE, 5.0, 2.5, "Basic farming tool"));
        section.addItem(createItem("stone_hoe", "Stone Hoe", Material.STONE_HOE, 12.0, 6.0, "Improved farming tool"));
        section.addItem(createItem("iron_hoe", "Iron Hoe", Material.IRON_HOE, 35.0, 17.5, "Efficient farming tool"));
        section.addItem(createItem("diamond_hoe", "Diamond Hoe", Material.DIAMOND_HOE, 150.0, 75.0, "Premium farming tool"));
        section.addItem(createItem("netherite_hoe", "Netherite Hoe", Material.NETHERITE_HOE, 400.0, 200.0, "Ultimate farming tool"));
        
        // Animal items
        section.addItem(createItem("egg", "Egg", Material.EGG, 3.0, 1.5, "Chicken egg"));
        section.addItem(createItem("leather", "Leather", Material.LEATHER, 8.0, 4.0, "Animal hide"));
        section.addItem(createItem("feather", "Feather", Material.FEATHER, 4.0, 2.0, "Bird feather"));
        section.addItem(createItem("bone", "Bone", Material.BONE, 5.0, 2.5, "Skeleton bone"));
        section.addItem(createItem("string", "String", Material.STRING, 3.0, 1.5, "Spider silk"));
        section.addItem(createItem("wool", "Wool", Material.WHITE_WOOL, 4.0, 2.0, "Sheep wool"));
        section.addItem(createItem("rabbit_hide", "Rabbit Hide", Material.RABBIT_HIDE, 4.0, 2.0, "Small animal hide"));
        
        // Breeding items
        section.addItem(createItem("lead", "Lead", Material.LEAD, 15.0, 7.5, "Animal leash"));
        section.addItem(createItem("name_tag", "Name Tag", Material.NAME_TAG, 25.0, 12.5, "Pet naming tag"));
        section.addItem(createItem("saddle", "Saddle", Material.SADDLE, 50.0, 25.0, "Horse riding equipment"));
        
        // Fertilizer and growth
        section.addItem(createItem("bone_meal", "Bone Meal", Material.BONE_MEAL, 4.0, 2.0, "Plant fertilizer"));
        section.addItem(createItem("composter", "Composter", Material.COMPOSTER, 30.0, 15.0, "Organic waste processor"));
        
        // Crops and produce
        section.addItem(createItem("wheat", "Wheat", Material.WHEAT, 2.0, 1.0, "Basic grain"));
        section.addItem(createItem("carrot", "Carrot", Material.CARROT, 2.5, 1.25, "Orange vegetable"));
        section.addItem(createItem("potato", "Potato", Material.POTATO, 2.5, 1.25, "Starchy tuber"));
        section.addItem(createItem("beetroot", "Beetroot", Material.BEETROOT, 3.0, 1.5, "Red root vegetable"));
        section.addItem(createItem("pumpkin", "Pumpkin", Material.PUMPKIN, 8.0, 4.0, "Large orange fruit"));
        section.addItem(createItem("melon", "Melon", Material.MELON, 6.0, 3.0, "Sweet green fruit"));
        section.addItem(createItem("sugar_cane", "Sugar Cane", Material.SUGAR_CANE, 3.0, 1.5, "Sweet plant"));
        section.addItem(createItem("bamboo", "Bamboo", Material.BAMBOO, 2.0, 1.0, "Fast-growing plant"));
        section.addItem(createItem("cactus", "Cactus", Material.CACTUS, 4.0, 2.0, "Desert plant"));
        section.addItem(createItem("kelp", "Kelp", Material.KELP, 1.5, 0.75, "Sea plant"));
        section.addItem(createItem("sea_pickle", "Sea Pickle", Material.SEA_PICKLE, 6.0, 3.0, "Ocean plant"));
        
        // Mushrooms
        section.addItem(createItem("brown_mushroom", "Brown Mushroom", Material.BROWN_MUSHROOM, 5.0, 2.5, "Edible fungus"));
        section.addItem(createItem("red_mushroom", "Red Mushroom", Material.RED_MUSHROOM, 5.0, 2.5, "Colorful fungus"));
        section.addItem(createItem("crimson_fungus", "Crimson Fungus", Material.CRIMSON_FUNGUS, 15.0, 7.5, "Nether fungus"));
        section.addItem(createItem("warped_fungus", "Warped Fungus", Material.WARPED_FUNGUS, 15.0, 7.5, "Nether fungus"));
        
        // Nether wart
        section.addItem(createItem("nether_wart", "Nether Wart", Material.NETHER_WART, 10.0, 5.0, "Base brewing ingredient"));
        
        // Hay and dried items
        section.addItem(createItem("hay_block", "Hay Block", Material.HAY_BLOCK, 18.0, 9.0, "Compressed wheat"));
        section.addItem(createItem("dried_kelp_block", "Dried Kelp Block", Material.DRIED_KELP_BLOCK, 18.0, 9.0, "Compressed kelp"));
        
        // Honey items
        section.addItem(createItem("honey_bottle", "Honey Bottle", Material.HONEY_BOTTLE, 12.0, 6.0, "Sweet bee honey"));
        section.addItem(createItem("honeycomb", "Honeycomb", Material.HONEYCOMB, 8.0, 4.0, "Bee wax"));
        section.addItem(createItem("honey_block", "Honey Block", Material.HONEY_BLOCK, 36.0, 18.0, "Sticky honey block"));
        section.addItem(createItem("honeycomb_block", "Honeycomb Block", Material.HONEYCOMB_BLOCK, 32.0, 16.0, "Wax block"));
        section.addItem(createItem("bee_nest", "Bee Nest", Material.BEE_NEST, 100.0, 50.0, "Natural bee home"));
        section.addItem(createItem("beehive", "Beehive", Material.BEEHIVE, 80.0, 40.0, "Crafted bee home"));
        
        // Farmland and soil
        section.addItem(createItem("dirt", "Dirt", Material.DIRT, 0.5, 0.2, "Basic earth block"));
        section.addItem(createItem("coarse_dirt", "Coarse Dirt", Material.COARSE_DIRT, 0.8, 0.4, "Infertile dirt"));
        section.addItem(createItem("podzol", "Podzol", Material.PODZOL, 2.0, 1.0, "Forest soil"));
        section.addItem(createItem("mycelium", "Mycelium", Material.MYCELIUM, 5.0, 2.5, "Mushroom soil"));
        
        // Water and buckets
        section.addItem(createItem("water_bucket", "Water Bucket", Material.WATER_BUCKET, 20.0, 10.0, "Essential for farming"));
        section.addItem(createItem("bucket", "Empty Bucket", Material.BUCKET, 15.0, 7.5, "Water container"));
    }
    
    /**
     * Add decoration items
     */
    private static void addDecorationItems(ShopSection section) {
        // Flowers
        section.addItem(createItem("poppy", "Poppy", Material.POPPY, 2.0, 1.0, "Red flower"));
        section.addItem(createItem("dandelion", "Dandelion", Material.DANDELION, 2.0, 1.0, "Yellow flower"));
        section.addItem(createItem("blue_orchid", "Blue Orchid", Material.BLUE_ORCHID, 4.0, 2.0, "Rare blue flower"));
        section.addItem(createItem("allium", "Allium", Material.ALLIUM, 3.0, 1.5, "Purple flower"));
        section.addItem(createItem("azure_bluet", "Azure Bluet", Material.AZURE_BLUET, 2.5, 1.25, "Small white flower"));
        section.addItem(createItem("red_tulip", "Red Tulip", Material.RED_TULIP, 3.0, 1.5, "Red tulip"));
        section.addItem(createItem("orange_tulip", "Orange Tulip", Material.ORANGE_TULIP, 3.0, 1.5, "Orange tulip"));
        section.addItem(createItem("white_tulip", "White Tulip", Material.WHITE_TULIP, 3.0, 1.5, "White tulip"));
        section.addItem(createItem("pink_tulip", "Pink Tulip", Material.PINK_TULIP, 3.0, 1.5, "Pink tulip"));
        section.addItem(createItem("oxeye_daisy", "Oxeye Daisy", Material.OXEYE_DAISY, 2.5, 1.25, "White daisy"));
        section.addItem(createItem("cornflower", "Cornflower", Material.CORNFLOWER, 3.5, 1.75, "Blue cornflower"));
        section.addItem(createItem("lily_of_the_valley", "Lily of the Valley", Material.LILY_OF_THE_VALLEY, 4.0, 2.0, "White lily"));
        section.addItem(createItem("wither_rose", "Wither Rose", Material.WITHER_ROSE, 50.0, 25.0, "Deadly black rose"));
        
        // Tall flowers
        section.addItem(createItem("sunflower", "Sunflower", Material.SUNFLOWER, 5.0, 2.5, "Tall yellow flower"));
        section.addItem(createItem("lilac", "Lilac", Material.LILAC, 4.5, 2.25, "Purple lilac"));
        section.addItem(createItem("rose_bush", "Rose Bush", Material.ROSE_BUSH, 6.0, 3.0, "Beautiful rose bush"));
        section.addItem(createItem("peony", "Peony", Material.PEONY, 5.5, 2.75, "Pink peony"));
        
        // Decorative blocks
        section.addItem(createItem("flower_pot", "Flower Pot", Material.FLOWER_POT, 8.0, 4.0, "Plant container"));
        section.addItem(createItem("painting", "Painting", Material.PAINTING, 15.0, 7.5, "Wall decoration"));
        section.addItem(createItem("item_frame", "Item Frame", Material.ITEM_FRAME, 10.0, 5.0, "Display frame"));
        section.addItem(createItem("glow_item_frame", "Glow Item Frame", Material.GLOW_ITEM_FRAME, 20.0, 10.0, "Glowing display frame"));
        section.addItem(createItem("armor_stand", "Armor Stand", Material.ARMOR_STAND, 25.0, 12.5, "Equipment display"));
        
        // Banners and flags
        section.addItem(createItem("white_banner", "White Banner", Material.WHITE_BANNER, 20.0, 10.0, "Plain white banner"));
        section.addItem(createItem("red_banner", "Red Banner", Material.RED_BANNER, 22.0, 11.0, "Bold red banner"));
        section.addItem(createItem("blue_banner", "Blue Banner", Material.BLUE_BANNER, 22.0, 11.0, "Royal blue banner"));
        section.addItem(createItem("green_banner", "Green Banner", Material.GREEN_BANNER, 22.0, 11.0, "Natural green banner"));
        section.addItem(createItem("yellow_banner", "Yellow Banner", Material.YELLOW_BANNER, 22.0, 11.0, "Bright yellow banner"));
        section.addItem(createItem("orange_banner", "Orange Banner", Material.ORANGE_BANNER, 22.0, 11.0, "Vibrant orange banner"));
        section.addItem(createItem("purple_banner", "Purple Banner", Material.PURPLE_BANNER, 22.0, 11.0, "Royal purple banner"));
        section.addItem(createItem("pink_banner", "Pink Banner", Material.PINK_BANNER, 22.0, 11.0, "Soft pink banner"));
        section.addItem(createItem("black_banner", "Black Banner", Material.BLACK_BANNER, 22.0, 11.0, "Dark black banner"));
        section.addItem(createItem("gray_banner", "Gray Banner", Material.GRAY_BANNER, 22.0, 11.0, "Neutral gray banner"));
        section.addItem(createItem("light_gray_banner", "Light Gray Banner", Material.LIGHT_GRAY_BANNER, 22.0, 11.0, "Light gray banner"));
        section.addItem(createItem("cyan_banner", "Cyan Banner", Material.CYAN_BANNER, 22.0, 11.0, "Bright cyan banner"));
        section.addItem(createItem("magenta_banner", "Magenta Banner", Material.MAGENTA_BANNER, 22.0, 11.0, "Vibrant magenta banner"));
        section.addItem(createItem("lime_banner", "Lime Banner", Material.LIME_BANNER, 22.0, 11.0, "Bright lime banner"));
        section.addItem(createItem("brown_banner", "Brown Banner", Material.BROWN_BANNER, 22.0, 11.0, "Natural brown banner"));
        
        // Carpets
        section.addItem(createItem("white_carpet", "White Carpet", Material.WHITE_CARPET, 3.0, 1.5, "Soft white carpet"));
        section.addItem(createItem("red_carpet", "Red Carpet", Material.RED_CARPET, 3.5, 1.75, "Luxurious red carpet"));
        section.addItem(createItem("blue_carpet", "Blue Carpet", Material.BLUE_CARPET, 3.5, 1.75, "Elegant blue carpet"));
        section.addItem(createItem("green_carpet", "Green Carpet", Material.GREEN_CARPET, 3.5, 1.75, "Natural green carpet"));
        section.addItem(createItem("yellow_carpet", "Yellow Carpet", Material.YELLOW_CARPET, 3.5, 1.75, "Bright yellow carpet"));
        section.addItem(createItem("orange_carpet", "Orange Carpet", Material.ORANGE_CARPET, 3.5, 1.75, "Vibrant orange carpet"));
        section.addItem(createItem("purple_carpet", "Purple Carpet", Material.PURPLE_CARPET, 3.5, 1.75, "Royal purple carpet"));
        section.addItem(createItem("pink_carpet", "Pink Carpet", Material.PINK_CARPET, 3.5, 1.75, "Soft pink carpet"));
        section.addItem(createItem("black_carpet", "Black Carpet", Material.BLACK_CARPET, 3.5, 1.75, "Dark black carpet"));
        section.addItem(createItem("gray_carpet", "Gray Carpet", Material.GRAY_CARPET, 3.5, 1.75, "Neutral gray carpet"));
        section.addItem(createItem("light_gray_carpet", "Light Gray Carpet", Material.LIGHT_GRAY_CARPET, 3.5, 1.75, "Light gray carpet"));
        section.addItem(createItem("cyan_carpet", "Cyan Carpet", Material.CYAN_CARPET, 3.5, 1.75, "Bright cyan carpet"));
        section.addItem(createItem("magenta_carpet", "Magenta Carpet", Material.MAGENTA_CARPET, 3.5, 1.75, "Vibrant magenta carpet"));
        section.addItem(createItem("lime_carpet", "Lime Carpet", Material.LIME_CARPET, 3.5, 1.75, "Bright lime carpet"));
        section.addItem(createItem("brown_carpet", "Brown Carpet", Material.BROWN_CARPET, 3.5, 1.75, "Natural brown carpet"));
        
        // Lighting
        section.addItem(createItem("torch", "Torch", Material.TORCH, 2.0, 1.0, "Basic light source"));
        section.addItem(createItem("soul_torch", "Soul Torch", Material.SOUL_TORCH, 3.0, 1.5, "Blue flame torch"));
        section.addItem(createItem("lantern", "Lantern", Material.LANTERN, 12.0, 6.0, "Hanging light"));
        section.addItem(createItem("soul_lantern", "Soul Lantern", Material.SOUL_LANTERN, 15.0, 7.5, "Blue flame lantern"));
        section.addItem(createItem("sea_lantern", "Sea Lantern", Material.SEA_LANTERN, 25.0, 12.5, "Underwater light"));
        section.addItem(createItem("glowstone", "Glowstone", Material.GLOWSTONE, 20.0, 10.0, "Bright nether light"));
        section.addItem(createItem("shroomlight", "Shroomlight", Material.SHROOMLIGHT, 18.0, 9.0, "Nether mushroom light"));
        section.addItem(createItem("jack_o_lantern", "Jack o'Lantern", Material.JACK_O_LANTERN, 10.0, 5.0, "Carved pumpkin light"));
        section.addItem(createItem("redstone_lamp", "Redstone Lamp", Material.REDSTONE_LAMP, 20.0, 10.0, "Controllable light"));
        section.addItem(createItem("end_rod", "End Rod", Material.END_ROD, 80.0, 40.0, "End dimension light"));
        
        // Candles
        section.addItem(createItem("candle", "Candle", Material.CANDLE, 6.0, 3.0, "Wax candle"));
        section.addItem(createItem("white_candle", "White Candle", Material.WHITE_CANDLE, 7.0, 3.5, "White wax candle"));
        section.addItem(createItem("red_candle", "Red Candle", Material.RED_CANDLE, 7.5, 3.75, "Red wax candle"));
        section.addItem(createItem("blue_candle", "Blue Candle", Material.BLUE_CANDLE, 7.5, 3.75, "Blue wax candle"));
        section.addItem(createItem("green_candle", "Green Candle", Material.GREEN_CANDLE, 7.5, 3.75, "Green wax candle"));
        
        // Furniture-like blocks
        section.addItem(createItem("crafting_table", "Crafting Table", Material.CRAFTING_TABLE, 15.0, 7.5, "Item crafting station"));
        section.addItem(createItem("chest", "Chest", Material.CHEST, 20.0, 10.0, "Storage container"));
        section.addItem(createItem("barrel", "Barrel", Material.BARREL, 18.0, 9.0, "Compact storage"));
        section.addItem(createItem("bookshelf", "Bookshelf", Material.BOOKSHELF, 30.0, 15.0, "Knowledge storage"));
        section.addItem(createItem("lectern", "Lectern", Material.LECTERN, 25.0, 12.5, "Book display"));
        section.addItem(createItem("anvil", "Anvil", Material.ANVIL, 100.0, 50.0, "Item repair station"));
        section.addItem(createItem("enchanting_table", "Enchanting Table", Material.ENCHANTING_TABLE, 200.0, 100.0, "Magic enhancement"));
        section.addItem(createItem("brewing_stand", "Brewing Stand", Material.BREWING_STAND, 100.0, 50.0, "Potion crafting"));
        section.addItem(createItem("cauldron", "Cauldron", Material.CAULDRON, 80.0, 40.0, "Water storage"));
        
        // Beds
        section.addItem(createItem("white_bed", "White Bed", Material.WHITE_BED, 30.0, 15.0, "Comfortable white bed"));
        section.addItem(createItem("red_bed", "Red Bed", Material.RED_BED, 32.0, 16.0, "Comfortable red bed"));
        section.addItem(createItem("blue_bed", "Blue Bed", Material.BLUE_BED, 32.0, 16.0, "Comfortable blue bed"));
        section.addItem(createItem("green_bed", "Green Bed", Material.GREEN_BED, 32.0, 16.0, "Comfortable green bed"));
        section.addItem(createItem("yellow_bed", "Yellow Bed", Material.YELLOW_BED, 32.0, 16.0, "Comfortable yellow bed"));
        section.addItem(createItem("orange_bed", "Orange Bed", Material.ORANGE_BED, 32.0, 16.0, "Comfortable orange bed"));
        section.addItem(createItem("purple_bed", "Purple Bed", Material.PURPLE_BED, 32.0, 16.0, "Comfortable purple bed"));
        section.addItem(createItem("pink_bed", "Pink Bed", Material.PINK_BED, 32.0, 16.0, "Comfortable pink bed"));
        section.addItem(createItem("black_bed", "Black Bed", Material.BLACK_BED, 32.0, 16.0, "Comfortable black bed"));
        section.addItem(createItem("gray_bed", "Gray Bed", Material.GRAY_BED, 32.0, 16.0, "Comfortable gray bed"));
        section.addItem(createItem("light_gray_bed", "Light Gray Bed", Material.LIGHT_GRAY_BED, 32.0, 16.0, "Comfortable light gray bed"));
        section.addItem(createItem("cyan_bed", "Cyan Bed", Material.CYAN_BED, 32.0, 16.0, "Comfortable cyan bed"));
        section.addItem(createItem("magenta_bed", "Magenta Bed", Material.MAGENTA_BED, 32.0, 16.0, "Comfortable magenta bed"));
        section.addItem(createItem("lime_bed", "Lime Bed", Material.LIME_BED, 32.0, 16.0, "Comfortable lime bed"));
        section.addItem(createItem("brown_bed", "Brown Bed", Material.BROWN_BED, 32.0, 16.0, "Comfortable brown bed"));
        
        // Signs
        section.addItem(createItem("oak_sign", "Oak Sign", Material.OAK_SIGN, 8.0, 4.0, "Wooden sign"));
        section.addItem(createItem("birch_sign", "Birch Sign", Material.BIRCH_SIGN, 8.5, 4.25, "Light wood sign"));
        section.addItem(createItem("spruce_sign", "Spruce Sign", Material.SPRUCE_SIGN, 8.2, 4.1, "Dark wood sign"));
        section.addItem(createItem("jungle_sign", "Jungle Sign", Material.JUNGLE_SIGN, 9.0, 4.5, "Exotic wood sign"));
        section.addItem(createItem("acacia_sign", "Acacia Sign", Material.ACACIA_SIGN, 8.8, 4.4, "Orange wood sign"));
        section.addItem(createItem("dark_oak_sign", "Dark Oak Sign", Material.DARK_OAK_SIGN, 8.7, 4.35, "Dark oak sign"));
        section.addItem(createItem("crimson_sign", "Crimson Sign", Material.CRIMSON_SIGN, 15.0, 7.5, "Nether wood sign"));
        section.addItem(createItem("warped_sign", "Warped Sign", Material.WARPED_SIGN, 15.0, 7.5, "Nether wood sign"));
        
        // Heads and skulls
        section.addItem(createItem("skeleton_skull", "Skeleton Skull", Material.SKELETON_SKULL, 150.0, 75.0, "Undead trophy"));
        section.addItem(createItem("zombie_head", "Zombie Head", Material.ZOMBIE_HEAD, 150.0, 75.0, "Undead trophy"));
        section.addItem(createItem("creeper_head", "Creeper Head", Material.CREEPER_HEAD, 200.0, 100.0, "Explosive trophy"));
        section.addItem(createItem("dragon_head", "Dragon Head", Material.DRAGON_HEAD, 5000.0, 2500.0, "Ultimate trophy"));
        section.addItem(createItem("player_head", "Player Head", Material.PLAYER_HEAD, 100.0, 50.0, "Custom player head"));
        
        // Music and sound
        section.addItem(createItem("note_block", "Note Block", Material.NOTE_BLOCK, 20.0, 10.0, "Musical block"));
        section.addItem(createItem("jukebox", "Jukebox", Material.JUKEBOX, 40.0, 20.0, "Music player"));
        
        // Bells and other decorative items
        section.addItem(createItem("bell", "Bell", Material.BELL, 50.0, 25.0, "Village bell"));
        section.addItem(createItem("campfire", "Campfire", Material.CAMPFIRE, 25.0, 12.5, "Cooking fire"));
        section.addItem(createItem("soul_campfire", "Soul Campfire", Material.SOUL_CAMPFIRE, 30.0, 15.0, "Blue cooking fire"));
    }
    
    /**
     * Add potion items
     */
    private static void addPotionItems(ShopSection section) {
        // Basic potions
        section.addItem(createItem("potion_healing", "Potion of Healing", Material.POTION, 50.0, 25.0, "Restores health"));
        section.addItem(createItem("potion_regeneration", "Potion of Regeneration", Material.POTION, 75.0, 37.5, "Regenerates health over time"));
        section.addItem(createItem("potion_strength", "Potion of Strength", Material.POTION, 100.0, 50.0, "Increases melee damage"));
        section.addItem(createItem("potion_speed", "Potion of Speed", Material.POTION, 60.0, 30.0, "Increases movement speed"));
        section.addItem(createItem("potion_jump_boost", "Potion of Leaping", Material.POTION, 40.0, 20.0, "Increases jump height"));
        section.addItem(createItem("potion_fire_resistance", "Potion of Fire Resistance", Material.POTION, 80.0, 40.0, "Immunity to fire damage"));
        section.addItem(createItem("potion_water_breathing", "Potion of Water Breathing", Material.POTION, 70.0, 35.0, "Breathe underwater"));
        section.addItem(createItem("potion_night_vision", "Potion of Night Vision", Material.POTION, 60.0, 30.0, "See in the dark"));
        section.addItem(createItem("potion_invisibility", "Potion of Invisibility", Material.POTION, 150.0, 75.0, "Become invisible"));
        section.addItem(createItem("potion_slow_falling", "Potion of Slow Falling", Material.POTION, 90.0, 45.0, "Fall slowly"));
        
        // Splash potions
        section.addItem(createItem("splash_potion_healing", "Splash Potion of Healing", Material.SPLASH_POTION, 75.0, 37.5, "Area healing effect"));
        section.addItem(createItem("splash_potion_harming", "Splash Potion of Harming", Material.SPLASH_POTION, 100.0, 50.0, "Area damage effect"));
        section.addItem(createItem("splash_potion_poison", "Splash Potion of Poison", Material.SPLASH_POTION, 80.0, 40.0, "Area poison effect"));
        section.addItem(createItem("splash_potion_weakness", "Splash Potion of Weakness", Material.SPLASH_POTION, 60.0, 30.0, "Reduces melee damage"));
        section.addItem(createItem("splash_potion_slowness", "Splash Potion of Slowness", Material.SPLASH_POTION, 50.0, 25.0, "Reduces movement speed"));
        
        // Lingering potions
        section.addItem(createItem("lingering_potion_healing", "Lingering Potion of Healing", Material.LINGERING_POTION, 100.0, 50.0, "Persistent healing cloud"));
        section.addItem(createItem("lingering_potion_harming", "Lingering Potion of Harming", Material.LINGERING_POTION, 125.0, 62.5, "Persistent damage cloud"));
        section.addItem(createItem("lingering_potion_poison", "Lingering Potion of Poison", Material.LINGERING_POTION, 100.0, 50.0, "Persistent poison cloud"));
        
        // Brewing ingredients
        section.addItem(createItem("nether_wart", "Nether Wart", Material.NETHER_WART, 10.0, 5.0, "Base brewing ingredient"));
        section.addItem(createItem("blaze_powder", "Blaze Powder", Material.BLAZE_POWDER, 15.0, 7.5, "Brewing fuel"));
        section.addItem(createItem("blaze_rod", "Blaze Rod", Material.BLAZE_ROD, 30.0, 15.0, "Blaze drop"));
        section.addItem(createItem("ghast_tear", "Ghast Tear", Material.GHAST_TEAR, 50.0, 25.0, "Regeneration ingredient"));
        section.addItem(createItem("spider_eye", "Spider Eye", Material.SPIDER_EYE, 8.0, 4.0, "Poison ingredient"));
        section.addItem(createItem("fermented_spider_eye", "Fermented Spider Eye", Material.FERMENTED_SPIDER_EYE, 20.0, 10.0, "Corruption ingredient"));
        section.addItem(createItem("magma_cream", "Magma Cream", Material.MAGMA_CREAM, 25.0, 12.5, "Fire resistance ingredient"));
        section.addItem(createItem("glistering_melon_slice", "Glistering Melon Slice", Material.GLISTERING_MELON_SLICE, 30.0, 15.0, "Healing ingredient"));
        section.addItem(createItem("golden_carrot", "Golden Carrot", Material.GOLDEN_CARROT, 25.0, 12.5, "Night vision ingredient"));
        section.addItem(createItem("pufferfish", "Pufferfish", Material.PUFFERFISH, 40.0, 20.0, "Water breathing ingredient"));
        section.addItem(createItem("rabbit_foot", "Rabbit's Foot", Material.RABBIT_FOOT, 35.0, 17.5, "Jump boost ingredient"));
        section.addItem(createItem("sugar", "Sugar", Material.SUGAR, 5.0, 2.5, "Speed ingredient"));
        section.addItem(createItem("gunpowder", "Gunpowder", Material.GUNPOWDER, 12.0, 6.0, "Splash potion ingredient"));
        section.addItem(createItem("dragon_breath", "Dragon's Breath", Material.DRAGON_BREATH, 200.0, 100.0, "Lingering potion ingredient"));
        section.addItem(createItem("phantom_membrane", "Phantom Membrane", Material.PHANTOM_MEMBRANE, 80.0, 40.0, "Slow falling ingredient"));
        section.addItem(createItem("turtle_helmet", "Turtle Shell", Material.TURTLE_HELMET, 250.0, 125.0, "Water breathing ingredient"));
        
        // Redstone and glowstone
        section.addItem(createItem("redstone", "Redstone Dust", Material.REDSTONE, 5.0, 2.5, "Duration extender"));
        section.addItem(createItem("glowstone_dust", "Glowstone Dust", Material.GLOWSTONE_DUST, 8.0, 4.0, "Potency enhancer"));
        
        // Brewing equipment
        section.addItem(createItem("brewing_stand", "Brewing Stand", Material.BREWING_STAND, 100.0, 50.0, "Potion crafting station"));
        section.addItem(createItem("cauldron", "Cauldron", Material.CAULDRON, 80.0, 40.0, "Water storage"));
        section.addItem(createItem("glass_bottle", "Glass Bottle", Material.GLASS_BOTTLE, 5.0, 2.5, "Potion container"));
        
        // Tipped arrows
        section.addItem(createItem("tipped_arrow_healing", "Tipped Arrow of Healing", Material.TIPPED_ARROW, 15.0, 7.5, "Healing arrow"));
        section.addItem(createItem("tipped_arrow_harming", "Tipped Arrow of Harming", Material.TIPPED_ARROW, 20.0, 10.0, "Damage arrow"));
        section.addItem(createItem("tipped_arrow_poison", "Tipped Arrow of Poison", Material.TIPPED_ARROW, 18.0, 9.0, "Poison arrow"));
        section.addItem(createItem("tipped_arrow_weakness", "Tipped Arrow of Weakness", Material.TIPPED_ARROW, 12.0, 6.0, "Weakness arrow"));
        section.addItem(createItem("tipped_arrow_slowness", "Tipped Arrow of Slowness", Material.TIPPED_ARROW, 10.0, 5.0, "Slowness arrow"));
        
        // Experience bottles
        section.addItem(createItem("experience_bottle", "Bottle o' Enchanting", Material.EXPERIENCE_BOTTLE, 50.0, 25.0, "Experience potion"));
        
        // Milk bucket (removes effects)
        section.addItem(createItem("milk_bucket", "Milk Bucket", Material.MILK_BUCKET, 15.0, 7.5, "Removes all effects"));
        
        // Honey bottle (removes poison)
        section.addItem(createItem("honey_bottle", "Honey Bottle", Material.HONEY_BOTTLE, 12.0, 6.0, "Removes poison"));
        
        // Suspicious stew (random effects)
        section.addItem(createItem("suspicious_stew", "Suspicious Stew", Material.SUSPICIOUS_STEW, 15.0, 7.5, "Random effect stew"));
    }
    
    /**
     * Helper method to create a basic shop item
     */
    private static ShopItem createItem(String id, String displayName, Material material, double buyPrice, double sellPrice, String description) {
        ShopItem item = new ShopItem(id, displayName, material, buyPrice, sellPrice);
        item.setDescription(description);
        return item;
    }
}