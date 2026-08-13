package dev.turjo.easyshopgui.models;

import java.util.Date;

/**
 * Represents a transaction record
 */
public class Transaction {
    
    private String type; // BUY or SELL
    private String itemName;
    private int quantity;
    private double amount;
    private Date date;
    private String playerId;
    
    public Transaction(String type, String itemName, int quantity, double amount, Date date) {
        this.type = type;
        this.itemName = itemName;
        this.quantity = quantity;
        this.amount = amount;
        this.date = date;
    }
    
    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
}