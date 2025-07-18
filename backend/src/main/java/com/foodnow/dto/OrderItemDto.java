package com.foodnow.dto;

public class OrderItemDto {
    private String itemName;
    private int quantity;
    private double price;

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    /**
     * This is the method your controller is looking for.
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
