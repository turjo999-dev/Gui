package dev.turjo.easyshopgui.gui;

import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.models.ShopItem;
import dev.turjo.easyshopgui.models.ShopSection;
import dev.turjo.easyshopgui.utils.ItemBuilder;
import dev.turjo.easyshopgui.utils.MessageUtils;
import dev.turjo.easyshopgui.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart search GUI with fuzzy matching
 */
public class SearchGui {
    
    private final EasyShopGUI plugin;
    private final Player player;
    private String currentQuery = "";
    private List<ShopItem> searchResults = new ArrayList<>();
    private int currentPage = 0;
    private final Map<Player, String> playerSearchQueries = new HashMap<>();
    
    public SearchGui(EasyShopGUI plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public SearchGui(EasyShopGUI plugin, Player player, String query) {
        this.plugin = plugin;
        this.player = player;
        this.currentQuery = query;
        performSearch();
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, 
                MessageUtils.colorize("&b&l🔍 &e&lSEARCH ITEMS"));
        
        // Fill background
        fillBackground(gui);
        
        if (currentQuery.isEmpty()) {
            // Show search interface
            addSearchInterface(gui);
        } else {
            // Show search results
            addSearchResults(gui);
        }
        
        // Add navigation
        addNavigation(gui);
        
        player.openInventory(gui);
    }
    
    /**
     * Fill background
     */
    private void fillBackground(Inventory gui) {
        ItemStack background = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        
        // Fill all slots
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }
    }
    
    /**
     * Add search interface
     */
    private void addSearchInterface(Inventory gui) {
        // Search instruction
        gui.setItem(22, new ItemBuilder(Material.COMPASS)
                .setName("&b&l🔍 &e&lSMART SEARCH")
                .setLore(Arrays.asList(
                        "&7▸ &fType in chat to search items",
                        "&7▸ &fSmart fuzzy matching",
                        "&7▸ &fSearch by name or material",
                        "&7▸ &fPartial words work too!",
                        "",
                        "&b&l💡 &bExamples:",
                        "&7  • &f'dia' → finds diamonds",
                        "&7  • &f'iron' → finds iron items",
                        "&7  • &f'food' → finds all food",
                        "&7  • &f'red' → finds redstone items",
                        "",
                        "&a&l➤ &aType your search in chat!"
                ))
                .addGlow()
                .build());
        
        // Popular searches
        gui.setItem(19, new ItemBuilder(Material.DIAMOND)
                .setName("&b&l💎 &e&lPOPULAR: DIAMONDS")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick search for diamonds",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
        
        gui.setItem(21, new ItemBuilder(Material.IRON_INGOT)
                .setName("&7&l⚒ &e&lPOPULAR: IRON")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick search for iron items",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
        
        gui.setItem(23, new ItemBuilder(Material.BREAD)
                .setName("&6&l🍞 &e&lPOPULAR: FOOD")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick search for food items",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
        
        gui.setItem(25, new ItemBuilder(Material.REDSTONE)
                .setName("&c&l⚡ &e&lPOPULAR: REDSTONE")
                .setLore(Arrays.asList(
                        "&7▸ &fQuick search for redstone",
                        "",
                        "&a&l➤ &aClick to search!"
                ))
                .build());
    }
    
    /**
     * Add search results
     */
    private void addSearchResults(Inventory gui) {
        // Search info
        gui.setItem(4, new ItemBuilder(Material.BOOK)
                .setName("&b&l📊 &e&lSEARCH RESULTS")
                .setLore(Arrays.asList(
                        "&7▸ &fQuery: &e'" + currentQuery + "'",
                        "&7▸ &fResults Found: &a" + searchResults.size(),
                        "&7▸ &fPage: &e" + (currentPage + 1) + "/" + getTotalPages(),
                        "",
                        "&b&l💡 &bTip: &fClick items to buy/sell!"
                ))
                .build());
        
        // Display search results
        int startIndex = currentPage * 28;
        int[] itemSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        for (int i = 0; i < itemSlots.length && (startIndex + i) < searchResults.size(); i++) {
            ShopItem item = searchResults.get(startIndex + i);
            gui.setItem(itemSlots[i], createSearchResultItem(item));
        }
        
        // No results message
        if (searchResults.isEmpty()) {
            gui.setItem(22, new ItemBuilder(Material.BARRIER)
                    .setName("&c&l❌ &e&lNO RESULTS")
                    .setLore(Arrays.asList(
                            "&7▸ &fNo items found for: &c'" + currentQuery + "'",
                            "&7▸ &fTry different keywords",
                            "&7▸ &fCheck spelling",
                            "",
                            "&b&l💡 &bSuggestions:",
                            "&7  • &fTry shorter words",
                            "&7  • &fUse material names",
                            "&7  • &fSearch by category"
                    ))
                    .build());
        }
    }
    
    /**
     * Create search result item
     */
    private ItemStack createSearchResultItem(ShopItem item) {
        double balance = plugin.getEconomyManager().getEconomy().getBalance(player);
        boolean canAfford = balance >= item.getBuyPrice();
        
        return new ItemBuilder(item.getMaterial())
                .setName("&e&l⭐ " + item.getDisplayName())
                .setLore(Arrays.asList(
                        "&7▸ &fDescription: &7" + item.getDescription(),
                        "",
                        "&7▸ &fPricing:",
                        "&a  Buy: &f$" + String.format("%.2f", item.getBuyPrice()) + (canAfford ? " &a✓" : " &c✗"),
                        "&c  Sell: &f$" + String.format("%.2f", item.getSellPrice()),
                        "",
                        "&7▸ &fStock: " + (item.getStock() == -1 ? "&aUnlimited" : "&e" + item.getStock()),
                        "",
                        "&e&l⚡ ACTIONS:",
                        "&a▸ Left Click: &fBuy 1",
                        "&a▸ Right Click: &fSell 1",
                        "&a▸ Shift + Left: &fBuy 64",
                        "&a▸ Shift + Right: &fSell All"
                ))
                .addGlow(canAfford)
                .build();
    }
    
    /**
     * Perform smart search with fuzzy matching
     */
    public void performSearch() {
        searchResults.clear();
        currentPage = 0;
        
        if (currentQuery.trim().isEmpty()) {
            return;
        }
        
        String query = currentQuery.toLowerCase().trim();
        Map<String, ShopSection> sections = plugin.getGuiManager().getSections();
        
        Logger.debug("Performing search for: '" + query + "'");
        
        for (ShopSection section : sections.values()) {
            for (ShopItem item : section.getItems()) {
                if (matchesSearch(item, query)) {
                    searchResults.add(item);
                }
            }
        }
        
        // Sort results by relevance
        searchResults.sort((a, b) -> {
            int scoreA = getRelevanceScore(a, query);
            int scoreB = getRelevanceScore(b, query);
            return Integer.compare(scoreB, scoreA);
        });
        
        Logger.debug("Search completed. Found " + searchResults.size() + " results");
    }
    
    /**
     * Smart fuzzy matching
     */
    private boolean matchesSearch(ShopItem item, String query) {
        String itemName = MessageUtils.stripColor(item.getDisplayName()).toLowerCase();
        String itemDesc = item.getDescription().toLowerCase();
        String materialName = item.getMaterial().name().toLowerCase().replace("_", " ");
        
        // Exact matches (highest priority)
        if (itemName.contains(query) || itemDesc.contains(query) || materialName.contains(query)) {
            return true;
        }
        
        // Word-based matching
        String[] queryWords = query.split("\\s+");
        for (String word : queryWords) {
            if (word.length() >= 2) { // Ignore very short words
                if (itemName.contains(word) || itemDesc.contains(word) || materialName.contains(word)) {
                    return true;
                }
            }
        }
        
        // Fuzzy matching for typos (simple Levenshtein-like)
        if (query.length() >= 3) {
            return fuzzyMatch(itemName, query) || fuzzyMatch(materialName, query);
        }
        
        return false;
    }
    
    /**
     * Simple fuzzy matching
     */
    private boolean fuzzyMatch(String text, String query) {
        if (text.length() < query.length()) return false;
        
        int errors = 0;
        int maxErrors = Math.max(1, query.length() / 3); // Allow 1 error per 3 characters
        
        for (int i = 0; i <= text.length() - query.length(); i++) {
            errors = 0;
            for (int j = 0; j < query.length(); j++) {
                if (text.charAt(i + j) != query.charAt(j)) {
                    errors++;
                    if (errors > maxErrors) break;
                }
            }
            if (errors <= maxErrors) return true;
        }
        
        return false;
    }
    
    /**
     * Get relevance score for sorting
     */
    private int getRelevanceScore(ShopItem item, String query) {
        String itemName = MessageUtils.stripColor(item.getDisplayName()).toLowerCase();
        String materialName = item.getMaterial().name().toLowerCase().replace("_", " ");
        
        int score = 0;
        
        // Exact name match (highest score)
        if (itemName.equals(query)) score += 100;
        else if (itemName.startsWith(query)) score += 80;
        else if (itemName.contains(query)) score += 60;
        
        // Material name match
        if (materialName.equals(query)) score += 90;
        else if (materialName.startsWith(query)) score += 70;
        else if (materialName.contains(query)) score += 50;
        
        // Description match
        if (item.getDescription().toLowerCase().contains(query)) score += 30;
        
        return score;
    }
    
    /**
     * Add navigation
     */
    private void addNavigation(Inventory gui) {
        // Back button
        gui.setItem(45, new ItemBuilder(Material.ARROW)
                .setName("&c&l← &e&lBACK TO SHOP")
                .setLore(Arrays.asList(
                        "&7▸ &fReturn to main shop",
                        "",
                        "&a&l➤ &aClick to go back!"
                ))
                .build());
        
        // Clear search
        if (!currentQuery.isEmpty()) {
            gui.setItem(46, new ItemBuilder(Material.BARRIER)
                    .setName("&c&l✖ &e&lCLEAR SEARCH")
                    .setLore(Arrays.asList(
                            "&7▸ &fClear current search",
                            "&7▸ &fStart new search",
                            "",
                            "&c&l➤ &cClick to clear!"
                    ))
                    .build());
        }
        
        // Previous page
        if (currentPage > 0) {
            gui.setItem(48, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .setName("&e&l← &e&lPREVIOUS PAGE")
                    .setLore(Arrays.asList(
                            "&7▸ &fPage " + currentPage + " of " + getTotalPages(),
                            "",
                            "&a&l➤ &aClick to go back!"
                    ))
                    .build());
        }
        
        // Next page
        if (hasNextPage()) {
            gui.setItem(50, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .setName("&e&l→ &e&lNEXT PAGE")
                    .setLore(Arrays.asList(
                            "&7▸ &fPage " + (currentPage + 2) + " of " + getTotalPages(),
                            "",
                            "&a&l➤ &aClick to continue!"
                    ))
                    .build());
        }
        
        // Search tips
        gui.setItem(53, new ItemBuilder(Material.KNOWLEDGE_BOOK)
                .setName("&b&l💡 &e&lSEARCH TIPS")
                .setLore(Arrays.asList(
                        "&7▸ &fType in chat to search",
                        "&7▸ &fPartial words work",
                        "&7▸ &fTypos are forgiven",
                        "&7▸ &fUse material names",
                        "",
                        "&b&l📝 &bExamples:",
                        "&7  • &f'dia' → diamonds",
                        "&7  • &f'iro' → iron items",
                        "&7  • &f'foo' → food items"
                ))
                .build());
    }
    
    /**
     * Handle quick search
     */
    public void quickSearch(String query) {
        this.currentQuery = query;
        performSearch();
        open();
    }
    
    /**
     * Navigation methods
     */
    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
            open();
        }
    }
    
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            open();
        }
    }
    
    public void clearSearch() {
        currentQuery = "";
        searchResults.clear();
        currentPage = 0;
        open();
    }
    
    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) searchResults.size() / 28));
    }
    
    private boolean hasNextPage() {
        return (currentPage + 1) < getTotalPages();
    }
    
    public List<ShopItem> getSearchResults() {
        return searchResults;
    }
    
    public String getCurrentQuery() {
        return currentQuery;
    }
}