package com.liquidassets.liquidassetsproject;

/**
 * Created by Byte4: Liquid Assets on 2/23/2016.
 */
public class Product {
    private double price;
    private int quantity, sku;
    private String category, description;

    public Product(String description, int sku, double price, int quantity, String category) {
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return description;
    }
}
