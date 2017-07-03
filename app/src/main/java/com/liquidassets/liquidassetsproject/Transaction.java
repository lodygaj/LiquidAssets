package com.liquidassets.liquidassetsproject;

/**
 * Created by Byte4: Liquid Assets on 4/24/2016.
 */
public class Transaction {
    private double subtotal, tax, total;
    private String date, time;

    public Transaction (String date, String time, double subtotal, double tax, double total) {
        this.date = date;
        this.time = time;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
