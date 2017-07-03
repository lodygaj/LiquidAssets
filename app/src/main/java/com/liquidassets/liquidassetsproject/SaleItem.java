package com.liquidassets.liquidassetsproject;

/**
 * Created by Byte4: Liquid Assets on 4/13/2016.
 */
public class SaleItem {
    private boolean discount;
    private double extPrice, price;
    private int percentOff, quantity, sku;
    private String description;

    public SaleItem(String description, int sku, double price, double extPrice, int quantity, int percentOff, boolean discount) {
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.extPrice = extPrice;
        this.quantity = quantity;
        this.percentOff = percentOff;
        this.discount = discount;
    }

    public double getExtPrice() {
        return extPrice;
    }

    public void setExtPrice(double extPrice) {
        this.extPrice = extPrice;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

    public int getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(int percentOff) {
        this.percentOff = percentOff;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
