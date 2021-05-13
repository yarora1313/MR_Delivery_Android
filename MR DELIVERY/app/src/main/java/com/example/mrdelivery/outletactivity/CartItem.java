package com.example.mrdelivery.outletactivity;

public class CartItem{

    private String name;
    private int price;
    private int number;

    public CartItem(String name, int price, int number) {
        this.name = name;
        this.price = price;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getNumber() {
        return number;
    }
}
