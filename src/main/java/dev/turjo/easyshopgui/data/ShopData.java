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
        addBlockItems(blocks);
        sections.put("blocks", blocks);
        
        // Tools & Weapons Section
        ShopSection tools = new ShopSection("tools", "Tools & Weapons", "&c&l⚔ &e&lTOOLS & WEAPONS", Material.DIAMOND_SWORD);
        addToolItems(tools);
        sections.put("tools", tools);
        
        // Armor Section
        ShopSection armor = new ShopSection("armor", "Armor", "&9&l🛡 &e&lARMOR SECTION", Material.DIAMOND_CHESTPLATE);
        addArmorItems(armor);
        sections.put("armor", armor);
        
        // Food Section
        ShopSection food = new ShopSection("food", "Food", "&6&l🍎 &e&lFOOD SECTION", Material.GOLDEN_APPLE);
        addFoodItems(food);
        sections.put("food", food);
        
        // Redstone Section
        ShopSection redstone = new ShopSection("redstone", "Redstone", "&4&l⚡ &e&lREDSTONE SECTION", Material.REDSTONE);
        addRedstoneItems(redstone);
        sections.put("redstone", redstone);
        
        // Farming Section
        ShopSection farming = new ShopSection("farming", "Farming", "&2&l🌾 &e&lFARMING SECTION", Material.WHEAT);
        addFarmingItems(farming);
        sections.put("farming", farming);
        
        // Decoration Section
        ShopSection decoration = new ShopSection("decoration", "Decoration", "&d&l🌸 &e&lDECORATION", Material.FLOWER_POT);
        addDecorationItems(decoration);
        sections.put("decoration", decoration);
        
        // Spawners Section
        ShopSection spawners = new ShopSection("spawners", "Spawners", "&5&l👹 &e&lSPAWNERS", Material.SPAWNER);
        addSpawnerItems(spawners);
        sections.put("spawners", spawners);
        
        // Enchanted Books Section
        ShopSection enchantedBooks = new ShopSection("enchanted_books", "Enchanted Books", "&3&l📚 &e&lENCHANTED BOOKS", Material.ENCHANTED_BOOK);
        addEnchantedBookItems(enchantedBooks);
        sections.put("enchanted_books", enchantedBooks);
        
        // Potions Section
        ShopSection potions = new ShopSection("potions", "Potions", "&8&l🧪 &e&lPOTIONS & EFFECTS", Material.POTION);
        addPotionItems(potions);
        sections.put("potions", potions);
        
        // Rare Items Section
        ShopSection rareItems = new ShopSection("rare_items", "Rare Items", "&f&l⭐ &e&lRARE ITEMS", Material.NETHER_STAR);
        addRareItems(rareItems);
        sections.put("rare_items", rareItems);
        
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
        
        // Wood types
        section.addItem(createItem("oak_log", "Oak Log", Material.OAK_LOG, 2.0, 1.0, "Sturdy oak wood"));
        section.addItem(createItem("birch_log", "Birch Log", Material.BIRCH_LOG, 2.2, 1.1, "Light birch wood"));
        section.addItem(createItem("spruce_log", "Spruce Log", Material.SPRUCE_LOG, 2.1, 1.05, "Dark spruce wood"));
        section.addItem(createItem("jungle_log", "Jungle Log", Material.JUNGLE_LOG, 2.5, 1.25, "Exotic jungle wood"));
        
        // Planks
        section.addItem(createItem("oak_planks", "Oak Planks", Material.OAK_PLANKS, 1.5, 0.75, "Processed oak planks"));
        section.addItem(createItem("birch_planks", "Birch Planks", Material.BIRCH_PLANKS, 1.6, 0.8, "Processed birch planks"));
        
        // Ores and minerals
        section.addItem(createItem("coal_ore", "Coal Ore", Material.COAL_ORE, 8.0, 4.0, "Coal-bearing stone"));
        section.addItem(createItem("iron_ore", "Iron Ore", Material.IRON_ORE, 15.0, 7.5, "Iron-bearing stone"));
        section.addItem(createItem("gold_ore", "Gold Ore", Material.GOLD_ORE, 25.0, 12.5, "Gold-bearing stone"));
        section.addItem(createItem("diamond_ore", "Diamond Ore", Material.DIAMOND_ORE, 100.0, 50.0, "Precious diamond ore"));
        
        // Processed materials
        section.addItem(createItem("coal", "Coal", Material.COAL, 5.0, 2.5, "Fuel and crafting material"));
        section.addItem(createItem("iron_ingot", "Iron Ingot", Material.IRON_INGOT, 12.0, 6.0, "Refined iron"));
        section.addItem(createItem("gold_ingot", "Gold Ingot", Material.GOLD_INGOT, 20.0, 10.0, "Refined gold"));
        section.addItem(createItem("diamond", "Diamond", Material.DIAMOND, 80.0, 40.0, "Precious gemstone"));
        
        // Glass and decorative
        section.addItem(createItem("glass", "Glass", Material.GLASS, 3.0, 1.5, "Transparent glass block"));
        section.addItem(createItem("sand", "Sand", Material.SAND, 1.0, 0.5, "Fine sand particles"));
        section.addItem(createItem("sandstone", "Sandstone", Material.SANDSTONE, 2.5, 1.25, "Compressed sand block"));
        
        // Wool colors
        section.addItem(createItem("white_wool", "White Wool", Material.WHITE_WOOL, 4.0, 2.0, "Soft white wool"));
        section.addItem(createItem("red_wool", "Red Wool", Material.RED_WOOL, 4.5, 2.25, "Vibrant red wool"));
        section.addItem(createItem("blue_wool", "Blue Wool", Material.BLUE_WOOL, 4.5, 2.25, "Deep blue wool"));
        section.addItem(createItem("green_wool", "Green Wool", Material.GREEN_WOOL, 4.5, 2.25, "Natural green wool"));
    }
    
    /**
     * Add tool items with balanced pricing
     */
    private static void addToolItems(ShopSection section) {
        // Wooden tools (starter tier)
        section.addItem(createItem("wooden_sword", "Wooden Sword", Material.WOODEN_SWORD, 5.0, 2.5, "Basic wooden sword"));
        section.addItem(createItem("wooden_pickaxe", "Wooden Pickaxe", Material.WOODEN_PICKAXE, 6.0, 3.0, "Basic mining tool"));
        section.addItem(createItem("wooden_axe", "Wooden Axe", Material.WOODEN_AXE, 6.0, 3.0, "Basic wood cutting tool"));
        section.addItem(createItem("wooden_shovel", "Wooden Shovel", Material.WOODEN_SHOVEL, 4.0, 2.0, "Basic digging tool"));
        
        // Stone tools (common tier)
        section.addItem(createItem("stone_sword", "Stone Sword", Material.STONE_SWORD, 15.0, 7.5, "Sturdy stone sword"));
        section.addItem(createItem("stone_pickaxe", "Stone Pickaxe", Material.STONE_PICKAXE, 18.0, 9.0, "Reliable mining tool"));
        section.addItem(createItem("stone_axe", "Stone Axe", Material.STONE_AXE, 18.0, 9.0, "Efficient wood cutting"));
        section.addItem(createItem("stone_shovel", "Stone Shovel", Material.STONE_SHOVEL, 12.0, 6.0, "Fast digging tool"));
        
        // Iron tools (intermediate tier)
        section.addItem(createItem("iron_sword", "Iron Sword", Material.IRON_SWORD, 50.0, 25.0, "Sharp iron blade"));
        section.addItem(createItem("iron_pickaxe", "Iron Pickaxe", Material.IRON_PICKAXE, 60.0, 30.0, "Durable mining tool"));
        section.addItem(createItem("iron_axe", "Iron Axe", Material.IRON_AXE, 60.0, 30.0, "Professional wood cutting"));
        section.addItem(createItem("iron_shovel", "Iron Shovel", Material.IRON_SHOVEL, 40.0, 20.0, "Efficient digging"));
        
        // Diamond tools (premium tier)
        section.addItem(createItem("diamond_sword", "Diamond Sword", Material.DIAMOND_SWORD, 200.0, 100.0, "Legendary diamond blade"));
        section.addItem(createItem("diamond_pickaxe", "Diamond Pickaxe", Material.DIAMOND_PICKAXE, 250.0, 125.0, "Ultimate mining tool"));
        section.addItem(createItem("diamond_axe", "Diamond Axe", Material.DIAMOND_AXE, 250.0, 125.0, "Master wood cutting"));
        section.addItem(createItem("diamond_shovel", "Diamond Shovel", Material.DIAMOND_SHOVEL, 180.0, 90.0, "Premium digging tool"));
        
        // Netherite tools (elite tier)
        section.addItem(createItem("netherite_sword", "Netherite Sword", Material.NETHERITE_SWORD, 500.0, 250.0, "Ultimate combat weapon"));
        section.addItem(createItem("netherite_pickaxe", "Netherite Pickaxe", Material.NETHERITE_PICKAXE, 600.0, 300.0, "Godlike mining power"));
        section.addItem(createItem("netherite_axe", "Netherite Axe", Material.NETHERITE_AXE, 600.0, 300.0, "Supreme wood cutting"));
        section.addItem(createItem("netherite_shovel", "Netherite Shovel", Material.NETHERITE_SHOVEL, 450.0, 225.0, "Elite digging tool"));
        
        // Ranged weapons
        section.addItem(createItem("bow", "Bow", Material.BOW, 80.0, 40.0, "Classic ranged weapon"));
        section.addItem(createItem("crossbow", "Crossbow", Material.CROSSBOW, 120.0, 60.0, "Powerful crossbow"));
        section.addItem(createItem("arrow", "Arrow", Material.ARROW, 2.0, 1.0, "Sharp projectile"));
        section.addItem(createItem("spectral_arrow", "Spectral Arrow", Material.SPECTRAL_ARROW, 5.0, 2.5, "Glowing arrow"));
        
        // Special tools
        section.addItem(createItem("fishing_rod", "Fishing Rod", Material.FISHING_ROD, 25.0, 12.5, "Catch fish and items"));
        section.addItem(createItem("flint_and_steel", "Flint and Steel", Material.FLINT_AND_STEEL, 15.0, 7.5, "Create fire"));
        section.addItem(createItem("shears", "Shears", Material.SHEARS, 30.0, 15.0, "Cut wool and leaves"));
    }
    
    /**
     * Add armor items with balanced pricing
     */
    private static void addArmorItems(ShopSection section) {
        // Leather armor (starter tier)
        section.addItem(createItem("leather_helmet", "Leather Helmet", Material.LEATHER_HELMET, 20.0, 10.0, "Basic head protection"));
        section.addItem(createItem("leather_chestplate", "Leather Chestplate", Material.LEATHER_CHESTPLATE, 35.0, 17.5, "Basic chest protection"));
        section.addItem(createItem("leather_leggings", "Leather Leggings", Material.LEATHER_LEGGINGS, 30.0, 15.0, "Basic leg protection"));
        section.addItem(createItem("leather_boots", "Leather Boots", Material.LEATHER_BOOTS, 15.0, 7.5, "Basic foot protection"));
        
        // Chainmail armor (uncommon tier)
        section.addItem(createItem("chainmail_helmet", "Chainmail Helmet", Material.CHAINMAIL_HELMET, 60.0, 30.0, "Flexible head armor"));
        section.addItem(createItem("chainmail_chestplate", "Chainmail Chestplate", Material.CHAINMAIL_CHESTPLATE, 100.0, 50.0, "Flexible chest armor"));
        section.addItem(createItem("chainmail_leggings", "Chainmail Leggings", Material.CHAINMAIL_LEGGINGS, 80.0, 40.0, "Flexible leg armor"));
        section.addItem(createItem("chainmail_boots", "Chainmail Boots", Material.CHAINMAIL_BOOTS, 40.0, 20.0, "Flexible foot armor"));
        
        // Iron armor (common tier)
        section.addItem(createItem("iron_helmet", "Iron Helmet", Material.IRON_HELMET, 100.0, 50.0, "Sturdy iron helmet"));
        section.addItem(createItem("iron_chestplate", "Iron Chestplate", Material.IRON_CHESTPLATE, 180.0, 90.0, "Strong iron chestplate"));
        section.addItem(createItem("iron_leggings", "Iron Leggings", Material.IRON_LEGGINGS, 150.0, 75.0, "Durable iron leggings"));
        section.addItem(createItem("iron_boots", "Iron Boots", Material.IRON_BOOTS, 80.0, 40.0, "Protective iron boots"));
        
        // Diamond armor (premium tier)
        section.addItem(createItem("diamond_helmet", "Diamond Helmet", Material.DIAMOND_HELMET, 400.0, 200.0, "Sparkling diamond helmet"));
        section.addItem(createItem("diamond_chestplate", "Diamond Chestplate", Material.DIAMOND_CHESTPLATE, 700.0, 350.0, "Brilliant diamond chestplate"));
        section.addItem(createItem("diamond_leggings", "Diamond Leggings", Material.DIAMOND_LEGGINGS, 600.0, 300.0, "Lustrous diamond leggings"));
        section.addItem(createItem("diamond_boots", "Diamond Boots", Material.DIAMOND_BOOTS, 300.0, 150.0, "Gleaming diamond boots"));
        
        // Netherite armor (elite tier)
        section.addItem(createItem("netherite_helmet", "Netherite Helmet", Material.NETHERITE_HELMET, 1000.0, 500.0, "Ultimate head protection"));
        section.addItem(createItem("netherite_chestplate", "Netherite Chestplate", Material.NETHERITE_CHESTPLATE, 1800.0, 900.0, "Supreme chest armor"));
        section.addItem(createItem("netherite_leggings", "Netherite Leggings", Material.NETHERITE_LEGGINGS, 1500.0, 750.0, "Legendary leg armor"));
        section.addItem(createItem("netherite_boots", "Netherite Boots", Material.NETHERITE_BOOTS, 800.0, 400.0, "Elite foot protection"));
        
        // Shields and accessories
        section.addItem(createItem("shield", "Shield", Material.SHIELD, 150.0, 75.0, "Defensive shield"));
        section.addItem(createItem("turtle_helmet", "Turtle Helmet", Material.TURTLE_HELMET, 250.0, 125.0, "Water breathing helmet"));
        section.addItem(createItem("elytra", "Elytra", Material.ELYTRA, 2000.0, 1000.0, "Wings of flight"));
    }
    
    /**
     * Add food items with balanced pricing
     */
    private static void addFoodItems(ShopSection section) {
        // Basic foods
        section.addItem(createItem("apple", "Apple", Material.APPLE, 3.0, 1.5, "Crisp red apple"));
        section.addItem(createItem("bread", "Bread", Material.BREAD, 5.0, 2.5, "Fresh baked bread"));
        section.addItem(createItem("cooked_beef", "Cooked Beef", Material.COOKED_BEEF, 8.0, 4.0, "Juicy steak"));
        section.addItem(createItem("cooked_porkchop", "Cooked Porkchop", Material.COOKED_PORKCHOP, 8.0, 4.0, "Tender pork"));
        section.addItem(createItem("cooked_chicken", "Cooked Chicken", Material.COOKED_CHICKEN, 6.0, 3.0, "Roasted chicken"));
        
        // Premium foods
        section.addItem(createItem("golden_apple", "Golden Apple", Material.GOLDEN_APPLE, 100.0, 50.0, "Magical golden apple"));
        section.addItem(createItem("enchanted_golden_apple", "Enchanted Golden Apple", Material.ENCHANTED_GOLDEN_APPLE, 1000.0, 500.0, "Legendary healing fruit"));
        section.addItem(createItem("golden_carrot", "Golden Carrot", Material.GOLDEN_CARROT, 25.0, 12.5, "Nutritious golden carrot"));
        
        // Raw materials
        section.addItem(createItem("wheat", "Wheat", Material.WHEAT, 2.0, 1.0, "Basic grain"));
        section.addItem(createItem("carrot", "Carrot", Material.CARROT, 2.5, 1.25, "Orange vegetable"));
        section.addItem(createItem("potato", "Potato", Material.POTATO, 2.5, 1.25, "Starchy tuber"));
        section.addItem(createItem("beetroot", "Beetroot", Material.BEETROOT, 3.0, 1.5, "Red root vegetable"));
        
        // Fish
        section.addItem(createItem("cooked_cod", "Cooked Cod", Material.COOKED_COD, 6.0, 3.0, "Flaky white fish"));
        section.addItem(createItem("cooked_salmon", "Cooked Salmon", Material.COOKED_SALMON, 7.0, 3.5, "Pink salmon fillet"));
        section.addItem(createItem("tropical_fish", "Tropical Fish", Material.TROPICAL_FISH, 10.0, 5.0, "Exotic colorful fish"));
        
        // Sweet treats
        section.addItem(createItem("cake", "Cake", Material.CAKE, 20.0, 10.0, "Delicious birthday cake"));
        section.addItem(createItem("cookie", "Cookie", Material.COOKIE, 4.0, 2.0, "Sweet chocolate chip cookie"));
        section.addItem(createItem("pumpkin_pie", "Pumpkin Pie", Material.PUMPKIN_PIE, 8.0, 4.0, "Seasonal pumpkin pie"));
        
        // Beverages and potions
        section.addItem(createItem("milk_bucket", "Milk Bucket", Material.MILK_BUCKET, 15.0, 7.5, "Fresh cow milk"));
        section.addItem(createItem("honey_bottle", "Honey Bottle", Material.HONEY_BOTTLE, 12.0, 6.0, "Sweet bee honey"));
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
        
        // Redstone lamps and torches
        section.addItem(createItem("redstone_lamp", "Redstone Lamp", Material.REDSTONE_LAMP, 20.0, 10.0, "Controllable light"));
        section.addItem(createItem("redstone_torch", "Redstone Torch", Material.REDSTONE_TORCH, 6.0, 3.0, "Power source"));
        section.addItem(createItem("tripwire_hook", "Tripwire Hook", Material.TRIPWIRE_HOOK, 12.0, 6.0, "Invisible trigger"));
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
        
        // Breeding items
        section.addItem(createItem("lead", "Lead", Material.LEAD, 15.0, 7.5, "Animal leash"));
        section.addItem(createItem("name_tag", "Name Tag", Material.NAME_TAG, 25.0, 12.5, "Pet naming tag"));
        section.addItem(createItem("saddle", "Saddle", Material.SADDLE, 50.0, 25.0, "Horse riding equipment"));
        
        // Fertilizer and growth
        section.addItem(createItem("bone_meal", "Bone Meal", Material.BONE_MEAL, 4.0, 2.0, "Plant fertilizer"));
        section.addItem(createItem("composter", "Composter", Material.COMPOSTER, 30.0, 15.0, "Organic waste processor"));
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
        section.addItem(createItem("rose_bush", "Rose Bush", Material.ROSE_BUSH, 6.0, 3.0, "Beautiful rose bush"));
        section.addItem(createItem("sunflower", "Sunflower", Material.SUNFLOWER, 5.0, 2.5, "Tall yellow flower"));
        
        // Decorative blocks
        section.addItem(createItem("flower_pot", "Flower Pot", Material.FLOWER_POT, 8.0, 4.0, "Plant container"));
        section.addItem(createItem("painting", "Painting", Material.PAINTING, 15.0, 7.5, "Wall decoration"));
        section.addItem(createItem("item_frame", "Item Frame", Material.ITEM_FRAME, 10.0, 5.0, "Display frame"));
        section.addItem(createItem("armor_stand", "Armor Stand", Material.ARMOR_STAND, 25.0, 12.5, "Equipment display"));
        
        // Banners and flags
        section.addItem(createItem("white_banner", "White Banner", Material.WHITE_BANNER, 20.0, 10.0, "Plain white banner"));
        section.addItem(createItem("red_banner", "Red Banner", Material.RED_BANNER, 22.0, 11.0, "Bold red banner"));
        section.addItem(createItem("blue_banner", "Blue Banner", Material.BLUE_BANNER, 22.0, 11.0, "Royal blue banner"));
        section.addItem(createItem("green_banner", "Green Banner", Material.GREEN_BANNER, 22.0, 11.0, "Natural green banner"));
        
        // Carpets
        section.addItem(createItem("white_carpet", "White Carpet", Material.WHITE_CARPET, 3.0, 1.5, "Soft white carpet"));
        section.addItem(createItem("red_carpet", "Red Carpet", Material.RED_CARPET, 3.5, 1.75, "Luxurious red carpet"));
        section.addItem(createItem("blue_carpet", "Blue Carpet", Material.BLUE_CARPET, 3.5, 1.75, "Elegant blue carpet"));
        
        // Lighting
        section.addItem(createItem("torch", "Torch", Material.TORCH, 2.0, 1.0, "Basic light source"));
        section.addItem(createItem("lantern", "Lantern", Material.LANTERN, 12.0, 6.0, "Hanging light"));
        section.addItem(createItem("soul_lantern", "Soul Lantern", Material.SOUL_LANTERN, 15.0, 7.5, "Blue flame lantern"));
        section.addItem(createItem("sea_lantern", "Sea Lantern", Material.SEA_LANTERN, 25.0, 12.5, "Underwater light"));
        section.addItem(createItem("glowstone", "Glowstone", Material.GLOWSTONE, 20.0, 10.0, "Bright nether light"));
        
        // Furniture-like blocks
        section.addItem(createItem("crafting_table", "Crafting Table", Material.CRAFTING_TABLE, 15.0, 7.5, "Item crafting station"));
        section.addItem(createItem("chest", "Chest", Material.CHEST, 20.0, 10.0, "Storage container"));
        section.addItem(createItem("barrel", "Barrel", Material.BARREL, 18.0, 9.0, "Compact storage"));
        section.addItem(createItem("bookshelf", "Bookshelf", Material.BOOKSHELF, 30.0, 15.0, "Knowledge storage"));
    }
    
    /**
     * Add spawner items (premium section)
     */
    private static void addSpawnerItems(ShopSection section) {
        // Common mob spawners
        section.addItem(createItem("zombie_spawner", "Zombie Spawner", Material.SPAWNER, 5000.0, 2500.0, "Spawns zombies"));
        section.addItem(createItem("skeleton_spawner", "Skeleton Spawner", Material.SPAWNER, 6000.0, 3000.0, "Spawns skeletons"));
        section.addItem(createItem("spider_spawner", "Spider Spawner", Material.SPAWNER, 4500.0, 2250.0, "Spawns spiders"));
        section.addItem(createItem("creeper_spawner", "Creeper Spawner", Material.SPAWNER, 8000.0, 4000.0, "Spawns creepers"));
        
        // Passive mob spawners
        section.addItem(createItem("cow_spawner", "Cow Spawner", Material.SPAWNER, 3000.0, 1500.0, "Spawns cows"));
        section.addItem(createItem("pig_spawner", "Pig Spawner", Material.SPAWNER, 2800.0, 1400.0, "Spawns pigs"));
        section.addItem(createItem("chicken_spawner", "Chicken Spawner", Material.SPAWNER, 2500.0, 1250.0, "Spawns chickens"));
        section.addItem(createItem("sheep_spawner", "Sheep Spawner", Material.SPAWNER, 3200.0, 1600.0, "Spawns sheep"));
        
        // Rare mob spawners
        section.addItem(createItem("enderman_spawner", "Enderman Spawner", Material.SPAWNER, 15000.0, 7500.0, "Spawns endermen"));
        section.addItem(createItem("blaze_spawner", "Blaze Spawner", Material.SPAWNER, 12000.0, 6000.0, "Spawns blazes"));
        section.addItem(createItem("witch_spawner", "Witch Spawner", Material.SPAWNER, 10000.0, 5000.0, "Spawns witches"));
        
        // Ultra rare spawners
        section.addItem(createItem("iron_golem_spawner", "Iron Golem Spawner", Material.SPAWNER, 25000.0, 12500.0, "Spawns iron golems"));
        section.addItem(createItem("villager_spawner", "Villager Spawner", Material.SPAWNER, 20000.0, 10000.0, "Spawns villagers"));
        
        // Spawner accessories
        section.addItem(createItem("spawn_egg", "Random Spawn Egg", Material.EGG, 100.0, 50.0, "Summon random mob"));
        section.addItem(createItem("soul_sand", "Soul Sand", Material.SOUL_SAND, 15.0, 7.5, "Slows movement"));
        section.addItem(createItem("magma_block", "Magma Block", Material.MAGMA_BLOCK, 25.0, 12.5, "Damages entities"));
    }
    
    /**
     * Add enchanted book items
     */
    private static void addEnchantedBookItems(ShopSection section) {
        // Weapon enchantments
        section.addItem(createEnchantedBook("sharpness_1", "Sharpness I", Enchantment.SHARPNESS, 1, 50.0, 25.0));
        section.addItem(createEnchantedBook("sharpness_5", "Sharpness V", Enchantment.SHARPNESS, 5, 500.0, 250.0));
        section.addItem(createEnchantedBook("smite_5", "Smite V", Enchantment.SMITE, 5, 400.0, 200.0));
        section.addItem(createEnchantedBook("bane_of_arthropods_5", "Bane of Arthropods V", Enchantment.BANE_OF_ARTHROPODS, 5, 300.0, 150.0));
        section.addItem(createEnchantedBook("knockback_2", "Knockback II", Enchantment.KNOCKBACK, 2, 200.0, 100.0));
        section.addItem(createEnchantedBook("fire_aspect_2", "Fire Aspect II", Enchantment.FIRE_ASPECT, 2, 300.0, 150.0));
        section.addItem(createEnchantedBook("looting_3", "Looting III", Enchantment.LOOTING, 3, 800.0, 400.0));
        section.addItem(createEnchantedBook("sweeping_edge_3", "Sweeping Edge III", Enchantment.SWEEPING_EDGE, 3, 400.0, 200.0));
        
        // Tool enchantments
        section.addItem(createEnchantedBook("efficiency_1", "Efficiency I", Enchantment.EFFICIENCY, 1, 40.0, 20.0));
        section.addItem(createEnchantedBook("efficiency_5", "Efficiency V", Enchantment.EFFICIENCY, 5, 600.0, 300.0));
        section.addItem(createEnchantedBook("fortune_3", "Fortune III", Enchantment.FORTUNE, 3, 1000.0, 500.0));
        section.addItem(createEnchantedBook("silk_touch", "Silk Touch", Enchantment.SILK_TOUCH, 1, 800.0, 400.0));
        section.addItem(createEnchantedBook("unbreaking_3", "Unbreaking III", Enchantment.UNBREAKING, 3, 500.0, 250.0));
        section.addItem(createEnchantedBook("mending", "Mending", Enchantment.MENDING, 1, 1500.0, 750.0));
        
        // Armor enchantments
        section.addItem(createEnchantedBook("protection_4", "Protection IV", Enchantment.PROTECTION, 4, 600.0, 300.0));
        section.addItem(createEnchantedBook("fire_protection_4", "Fire Protection IV", Enchantment.FIRE_PROTECTION, 4, 400.0, 200.0));
        section.addItem(createEnchantedBook("blast_protection_4", "Blast Protection IV", Enchantment.BLAST_PROTECTION, 4, 400.0, 200.0));
        section.addItem(createEnchantedBook("projectile_protection_4", "Projectile Protection IV", Enchantment.PROJECTILE_PROTECTION, 4, 400.0, 200.0));
        section.addItem(createEnchantedBook("thorns_3", "Thorns III", Enchantment.THORNS, 3, 300.0, 150.0));
        section.addItem(createEnchantedBook("respiration_3", "Respiration III", Enchantment.RESPIRATION, 3, 200.0, 100.0));
        section.addItem(createEnchantedBook("aqua_affinity", "Aqua Affinity", Enchantment.AQUA_AFFINITY, 1, 150.0, 75.0));
        section.addItem(createEnchantedBook("depth_strider_3", "Depth Strider III", Enchantment.DEPTH_STRIDER, 3, 250.0, 125.0));
        section.addItem(createEnchantedBook("frost_walker_2", "Frost Walker II", Enchantment.FROST_WALKER, 2, 300.0, 150.0));
        section.addItem(createEnchantedBook("feather_falling_4", "Feather Falling IV", Enchantment.FEATHER_FALLING, 4, 200.0, 100.0));
        
        // Bow enchantments
        section.addItem(createEnchantedBook("power_5", "Power V", Enchantment.POWER, 5, 400.0, 200.0));
        section.addItem(createEnchantedBook("punch_2", "Punch II", Enchantment.PUNCH, 2, 200.0, 100.0));
        section.addItem(createEnchantedBook("flame", "Flame", Enchantment.FLAME, 1, 300.0, 150.0));
        section.addItem(createEnchantedBook("infinity", "Infinity", Enchantment.INFINITY, 1, 800.0, 400.0));
        
        // Fishing enchantments
        section.addItem(createEnchantedBook("luck_of_the_sea_3", "Luck of the Sea III", Enchantment.LUCK_OF_THE_SEA, 3, 500.0, 250.0));
        section.addItem(createEnchantedBook("lure_3", "Lure III", Enchantment.LURE, 3, 300.0, 150.0));
        
        // Trident enchantments (1.13+)
        section.addItem(createEnchantedBook("loyalty_3", "Loyalty III", Enchantment.LOYALTY, 3, 400.0, 200.0));
        section.addItem(createEnchantedBook("impaling_5", "Impaling V", Enchantment.IMPALING, 5, 500.0, 250.0));
        section.addItem(createEnchantedBook("riptide_3", "Riptide III", Enchantment.RIPTIDE, 3, 600.0, 300.0));
        section.addItem(createEnchantedBook("channeling", "Channeling", Enchantment.CHANNELING, 1, 800.0, 400.0));
        
        // Curse enchantments
        section.addItem(createEnchantedBook("curse_of_binding", "Curse of Binding", Enchantment.BINDING_CURSE, 1, 50.0, 25.0));
        section.addItem(createEnchantedBook("curse_of_vanishing", "Curse of Vanishing", Enchantment.VANISHING_CURSE, 1, 50.0, 25.0));
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
        section.addItem(createItem("ghast_tear", "Ghast Tear", Material.GHAST_TEAR, 50.0, 25.0, "Regeneration ingredient"));
        section.addItem(createItem("spider_eye", "Spider Eye", Material.SPIDER_EYE, 8.0, 4.0, "Poison ingredient"));
        section.addItem(createItem("fermented_spider_eye", "Fermented Spider Eye", Material.FERMENTED_SPIDER_EYE, 20.0, 10.0, "Corruption ingredient"));
        section.addItem(createItem("magma_cream", "Magma Cream", Material.MAGMA_CREAM, 25.0, 12.5, "Fire resistance ingredient"));
        section.addItem(createItem("glistering_melon_slice", "Glistering Melon Slice", Material.GLISTERING_MELON_SLICE, 30.0, 15.0, "Healing ingredient"));
        section.addItem(createItem("golden_carrot", "Golden Carrot", Material.GOLDEN_CARROT, 25.0, 12.5, "Night vision ingredient"));
        section.addItem(createItem("pufferfish", "Pufferfish", Material.PUFFERFISH, 40.0, 20.0, "Water breathing ingredient"));
        section.addItem(createItem("rabbit_foot", "Rabbit's Foot", Material.RABBIT_FOOT, 35.0, 17.5, "Jump boost ingredient"));
        
        // Brewing equipment
        section.addItem(createItem("brewing_stand", "Brewing Stand", Material.BREWING_STAND, 100.0, 50.0, "Potion crafting station"));
        section.addItem(createItem("cauldron", "Cauldron", Material.CAULDRON, 80.0, 40.0, "Water storage"));
        section.addItem(createItem("glass_bottle", "Glass Bottle", Material.GLASS_BOTTLE, 5.0, 2.5, "Potion container"));
    }
    
    /**
     * Add rare items
     */
    private static void addRareItems(ShopSection section) {
        // Ultra rare materials
        section.addItem(createItem("nether_star", "Nether Star", Material.NETHER_STAR, 5000.0, 2500.0, "Wither boss drop"));
        section.addItem(createItem("dragon_egg", "Dragon Egg", Material.DRAGON_EGG, 50000.0, 25000.0, "Ender Dragon trophy"));
        section.addItem(createItem("elytra", "Elytra", Material.ELYTRA, 10000.0, 5000.0, "Wings of flight"));
        section.addItem(createItem("totem_of_undying", "Totem of Undying", Material.TOTEM_OF_UNDYING, 8000.0, 4000.0, "Prevents death"));
        
        // Rare blocks
        section.addItem(createItem("beacon", "Beacon", Material.BEACON, 6000.0, 3000.0, "Area effect provider"));
        section.addItem(createItem("conduit", "Conduit", Material.CONDUIT, 4000.0, 2000.0, "Underwater beacon"));
        section.addItem(createItem("end_crystal", "End Crystal", Material.END_CRYSTAL, 2000.0, 1000.0, "Healing crystal"));
        
        // Rare nether items
        section.addItem(createItem("ancient_debris", "Ancient Debris", Material.ANCIENT_DEBRIS, 500.0, 250.0, "Netherite source"));
        section.addItem(createItem("netherite_scrap", "Netherite Scrap", Material.NETHERITE_SCRAP, 400.0, 200.0, "Refined ancient debris"));
        section.addItem(createItem("netherite_ingot", "Netherite Ingot", Material.NETHERITE_INGOT, 2000.0, 1000.0, "Ultimate material"));
        section.addItem(createItem("crying_obsidian", "Crying Obsidian", Material.CRYING_OBSIDIAN, 100.0, 50.0, "Respawn anchor fuel"));
        
        // End items
        section.addItem(createItem("shulker_shell", "Shulker Shell", Material.SHULKER_SHELL, 1000.0, 500.0, "Shulker box material"));
        section.addItem(createItem("shulker_box", "Shulker Box", Material.SHULKER_BOX, 2500.0, 1250.0, "Portable storage"));
        section.addItem(createItem("chorus_fruit", "Chorus Fruit", Material.CHORUS_FRUIT, 50.0, 25.0, "Teleportation fruit"));
        section.addItem(createItem("end_rod", "End Rod", Material.END_ROD, 80.0, 40.0, "End dimension light"));
        
        // Music discs
        section.addItem(createItem("music_disc_13", "Music Disc - 13", Material.MUSIC_DISC_13, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_cat", "Music Disc - Cat", Material.MUSIC_DISC_CAT, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_blocks", "Music Disc - Blocks", Material.MUSIC_DISC_BLOCKS, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_chirp", "Music Disc - Chirp", Material.MUSIC_DISC_CHIRP, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_far", "Music Disc - Far", Material.MUSIC_DISC_FAR, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_mall", "Music Disc - Mall", Material.MUSIC_DISC_MALL, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_mellohi", "Music Disc - Mellohi", Material.MUSIC_DISC_MELLOHI, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_stal", "Music Disc - Stal", Material.MUSIC_DISC_STAL, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_strad", "Music Disc - Strad", Material.MUSIC_DISC_STRAD, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_ward", "Music Disc - Ward", Material.MUSIC_DISC_WARD, 200.0, 100.0, "Rare music disc"));
        section.addItem(createItem("music_disc_11", "Music Disc - 11", Material.MUSIC_DISC_11, 500.0, 250.0, "Mysterious music disc"));
        section.addItem(createItem("music_disc_wait", "Music Disc - Wait", Material.MUSIC_DISC_WAIT, 200.0, 100.0, "Rare music disc"));
        
        // Special heads
        section.addItem(createItem("skeleton_skull", "Skeleton Skull", Material.SKELETON_SKULL, 150.0, 75.0, "Undead trophy"));
        section.addItem(createItem("wither_skeleton_skull", "Wither Skeleton Skull", Material.WITHER_SKELETON_SKULL, 1000.0, 500.0, "Rare nether trophy"));
        section.addItem(createItem("zombie_head", "Zombie Head", Material.ZOMBIE_HEAD, 150.0, 75.0, "Undead trophy"));
        section.addItem(createItem("creeper_head", "Creeper Head", Material.CREEPER_HEAD, 200.0, 100.0, "Explosive trophy"));
        section.addItem(createItem("dragon_head", "Dragon Head", Material.DRAGON_HEAD, 5000.0, 2500.0, "Ultimate trophy"));
        
        // Heart of the sea and nautilus shells
        section.addItem(createItem("heart_of_the_sea", "Heart of the Sea", Material.HEART_OF_THE_SEA, 3000.0, 1500.0, "Ocean's heart"));
        section.addItem(createItem("nautilus_shell", "Nautilus Shell", Material.NAUTILUS_SHELL, 500.0, 250.0, "Conduit material"));
        
        // Trident
        section.addItem(createItem("trident", "Trident", Material.TRIDENT, 2500.0, 1250.0, "Legendary weapon"));
    }
    
    /**
     * Helper method to create a basic shop item
     */
    private static ShopItem createItem(String id, String displayName, Material material, double buyPrice, double sellPrice, String description) {
        ShopItem item = new ShopItem(id, displayName, material, buyPrice, sellPrice);
        item.setDescription(description);
        return item;
    }
    
    /**
     * Helper method to create an enchanted book
     */
    private static ShopItem createEnchantedBook(String id, String displayName, Enchantment enchantment, int level, double buyPrice, double sellPrice) {
        ShopItem item = new ShopItem(id, displayName, Material.ENCHANTED_BOOK, buyPrice, sellPrice);
        item.setDescription("Enchanted book with " + enchantment.getKey().getKey() + " " + level);
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        item.setEnchantments(enchantments);
        item.setGlowing(true);
        return item;
    }
}